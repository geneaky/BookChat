package toy.bookchat.bookchat.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.config.JwtTokenConfig;
import toy.bookchat.bookchat.security.exception.DenidedTokenException;
import toy.bookchat.bookchat.security.exception.ExpiredTokenException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenManager {

    @Autowired
    JwtTokenConfig jwtTokenConfig;

    /* TODO: 2022-08-30 jwt library에서 지정한 예외를 커스텀 예외로 변경해야 이후
        라이브러리를 교체시 예외를 던지는 부분에서만 수정하면되고 global exception handler에서 직접적으로
        예외를 수정할 필요가 없어짐
     */
    public String isNotValidatedToken(String openIdToken) {
        String oauth2MemberNumber;
        try {
            oauth2MemberNumber = Jwts.parser()
                    .setSigningKey(jwtTokenConfig.getSecret())
                    .parseClaimsJws(openIdToken)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException exception) {
            log.info("Token :: {} :: is expired", openIdToken);
            throw new ExpiredTokenException(exception.getMessage(),exception);
        } catch (JwtException exception) {
            log.info("Token :: {} :: is denied", openIdToken);
            throw new DenidedTokenException(exception.getMessage(),exception);
        }
        return oauth2MemberNumber;
    }
}
