package toy.bookchat.bookchat.security.openid;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
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

    public String getOAuth2MemberNumberFromRequest(String openIdToken) {
        StringBuilder stringBuilder = new StringBuilder();

        getOAuth2MemberNumberFromOpenIdToken(openIdToken, stringBuilder);

        return stringBuilder.toString();
    }

    private void getOAuth2MemberNumberFromOpenIdToken(String openIdToken,
        StringBuilder stringBuilder) {
        try {
            String keyId = Jwts.parser().parseClaimsJws(openIdToken).getHeader().getKeyId();

            Claims body = Jwts.parser()
                .setSigningKey(openIdTokenConfig.getSecret(keyId))
                .parseClaimsJws(openIdToken)
                .getBody();

            String issuer = Optional.ofNullable(body.getIssuer())
                .orElseThrow(() -> {
                    throw new DenidedTokenException("Not Allowed Format Token Exception");
                });

            getOAuth2MemberNumberFromClaims(stringBuilder, body, issuer);

        } catch (ExpiredJwtException exception) {
            log.info("Token :: {} :: is expired", openIdToken);
            throw new ExpiredTokenException(exception.getMessage(), exception);
        } catch (JwtException exception) {
            log.info("Token :: {} :: is denied", openIdToken);
            throw new DenidedTokenException(exception.getMessage(), exception);
        }
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
