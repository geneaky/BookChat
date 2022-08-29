package toy.bookchat.bookchat.security.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.config.JwtTokenConfig;

@Component
@RequiredArgsConstructor
public class JwtTokenManager {

    @Autowired
    JwtTokenConfig jwtTokenConfig;

    public boolean isNotValidatedToken(String openIdToken) {
        try {
            Jwts.parser().setSigningKey(jwtTokenConfig.getSecret()).parse(openIdToken);
        } catch (JwtException exception) {
            return true;
        }
        return false;
    }
}
