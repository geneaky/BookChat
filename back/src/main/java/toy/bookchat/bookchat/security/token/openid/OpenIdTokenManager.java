package toy.bookchat.bookchat.security.openid;

import io.jsonwebtoken.*;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.config.OpenIdTokenConfig;
import toy.bookchat.bookchat.security.exception.DenidedTokenException;
import toy.bookchat.bookchat.security.exception.ExpiredTokenException;
import toy.bookchat.bookchat.security.exception.IllegalStandardTokenException;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenIdTokenManager {

    /* TODO: 2022-09-16 토큰 검증 로직들 리팩토링 다시 해보기
        openidtokenmanager는 usercontroller에서도 사용하기 때문에 예외 처리부분 좀 더 고려해보기
        template method pattern?
        openidtoken class를 만드는건?

        openidtoken manager의 역할은? 이녀석이 할 일이 뭐야?
        맨 처음 나의 생각은 토큰을 검증하고, 토큰에서 email이나 oauth2MemberNumber등을 빼올 수 있는
        관리자 클래스의 역할을 기대했음
        토큰을 검증하고 정보를 빼는 것은 토큰 클래스에서 하고 , 일련의 처리 과정은 manager가 계산 순서를
        보장해주는 것이 더 깔끔하지 않을까? 근데 그러면 openidtoken class에 openidtokenconfig가 포함되어야함
        생성되는 모든 openid에 대해 config를 가진는게 비효율적임 config는 manager가 넣어줄까?
     */

    public static final int STANDARD_TOKEN_LENGTH = 3;
    private final OpenIdTokenConfig openIdTokenConfig;

    public String getOAuth2MemberNumberFromOpenIdToken(String openIdToken, OAuth2Provider oAuth2Provider) {
        OpenIdToken token = OpenIdToken.of(openIdToken);
        String oAuth2MemberNumber = token.getOAuth2MemberNumber(openIdTokenConfig.getPublicKey(token.getKeyId(), oAuth2Provider.getValue()));
        return oAuth2MemberNumber;
    }

    private void validateTokenLength(String openIdToken) {
        if(openIdToken.split("\\.").length != STANDARD_TOKEN_LENGTH) {
            throw new IllegalStandardTokenException("Illegal Standard Token Format");
        }
    }

    private void extractProviderMemberNumberFromOpenIdToken(OAuth2Provider oAuth2Provider, String openIdToken, StringBuilder stringBuilder) {

        Claims body = Jwts.parser()
            .setSigningKey(openIdTokenConfig.getPublicKey(extractKeyIdFromOpenIdToken(openIdToken), oAuth2Provider.getValue()))
            .parseClaimsJws(openIdToken)
            .getBody();

        String issuer = Optional.ofNullable(body.getIssuer())
            .orElseThrow(() -> {
                throw new DenidedTokenException("Not Allowed Format Token Exception");
            });

        getOAuth2MemberNumberFromClaims(stringBuilder, body, issuer);
    }

    private String extractKeyIdFromOpenIdToken(String openIdToken) {
        return (String) Jwts.parser()
                .parseClaimsJwt(getUnsignedTokenBuilder(openIdToken).toString())
                .getHeader()
                .get("kid");
    }

    private StringBuilder getUnsignedTokenBuilder(String openIdToken) {
        String[] tokenParts = divideTokenIntoParts(openIdToken);

        StringBuilder unsignedTokenBuilder = new StringBuilder();
        unsignedTokenBuilder.append(tokenParts[0]);
        unsignedTokenBuilder.append(".");
        unsignedTokenBuilder.append(tokenParts[1]);
        unsignedTokenBuilder.append(".");

        return unsignedTokenBuilder;
    }

    private void getOAuth2MemberNumberFromClaims(StringBuilder stringBuilder, Claims body,
        String issuer) {
        if (issuer.contains(OAuth2Provider.KAKAO.getValue())) {
            stringBuilder.append(body.getSubject());
            stringBuilder.append(OAuth2Provider.KAKAO.getValue());
            return;
        }

        if (issuer.contains(OAuth2Provider.GOOGLE.getValue())) {
            stringBuilder.append(body.getSubject());
            stringBuilder.append(OAuth2Provider.GOOGLE.getValue());
            return;
        }

        throw new DenidedTokenException("Not Allowed Format Token Exception");
    }

    private String[] divideTokenIntoParts(String openIdToken) {
        String[] splitToken = openIdToken.split("\\.");
        if(splitToken.length != STANDARD_TOKEN_LENGTH) {
            throw new IllegalStandardTokenException("Illegal Standard Token Format");
        }
        return splitToken;
    }

    public String getUserEmailFromOpenIdToken(String openIdToken, OAuth2Provider oAuth2Provider) {
        OpenIdToken token = OpenIdToken.of(openIdToken);
        return token.getEmail(openIdTokenConfig.getPublicKey(token.getKeyId(), oAuth2Provider));
    }
}
