package toy.bookchat.bookchat.security.openid;

import io.jsonwebtoken.*;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.config.OpenIdTokenConfig;
import toy.bookchat.bookchat.security.exception.DenidedTokenException;
import toy.bookchat.bookchat.security.exception.ExpiredTokenException;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenIdTokenManager {

    private final OpenIdTokenConfig openIdTokenConfig;

    public String getOAuth2MemberNumberFromOpenIdToken(String openIdToken, String tokenProvider) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            extractProviderMemberNumberFromOpenIdToken(tokenProvider, openIdToken, stringBuilder);
        } catch (ExpiredJwtException exception) {
            log.info("Token :: {} :: is expired", openIdToken);
            throw new ExpiredTokenException(exception.getMessage(), exception);
        } catch (JwtException exception) {
            log.info("Token :: {} :: is denied", openIdToken);
            throw new DenidedTokenException(exception.getMessage(), exception);
        }

        return stringBuilder.toString();
    }

    private void extractProviderMemberNumberFromOpenIdToken(String tokenProvider, String openIdToken, StringBuilder stringBuilder) {

        Claims body = Jwts.parser()
            .setSigningKey(openIdTokenConfig.getPublicKey(extractKeyIdFromOpenIdToken(openIdToken), tokenProvider))
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
        String[] splitToken = openIdToken.split("\\.");

        StringBuilder unsignedTokenBuilder = new StringBuilder();
        unsignedTokenBuilder.append(splitToken[0]);
        unsignedTokenBuilder.append(".");
        unsignedTokenBuilder.append(splitToken[1]);
        unsignedTokenBuilder.append(".");

        return unsignedTokenBuilder;
    }

    private void getOAuth2MemberNumberFromClaims(StringBuilder stringBuilder, Claims body,
        String issuer) {
        if (issuer.contains(OAuth2Provider.KAKAO.getValue())) {
            stringBuilder.append(body.getSubject());
            stringBuilder.append(OAuth2Provider.KAKAO.getValue());
        } else if (issuer.contains(OAuth2Provider.GOOGLE.getValue())) {
            stringBuilder.append(body.getSubject());
            stringBuilder.append(OAuth2Provider.GOOGLE.getValue());
        } else {
            throw new DenidedTokenException("Not AllowedFormat Token Exception");
        }
    }
}
