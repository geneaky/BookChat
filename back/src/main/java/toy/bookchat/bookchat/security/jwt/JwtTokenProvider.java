package toy.bookchat.bookchat.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.user.UserPrincipal;

@Slf4j
@Component
public class JwtTokenProvider {

    /*@todo
     *   외부 의존성인 io.jwts를 사용하는 것이 아니라 인터페이스를 만들고 그 구현체에서 사용하는 방식으로
     *   작성하면 이후 io.jwts가 아니라 다른 jwt 구현체를 사용하도록 확장가능*/

    public static final String EMAIL = "email";
    public static final String SOCIAL_TYPE = "social_type";
    public static final String KAKAO_ACCOUNT = "kakao_account";

    @Value("${token.secret}")
    private String secret;

    @Value("${token.expired_time}")
    private long expiredTime;

    public String createToken(Authentication authentication) {
        UserPrincipal defaultOAuth2User = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + expiredTime);

        OAuth2Provider oAuth2Provider = (OAuth2Provider) defaultOAuth2User.getAttributes()
            .get(SOCIAL_TYPE);
        String email;
        if (oAuth2Provider == OAuth2Provider.kakao) {
            email = (String) ((Map<String, Object>) defaultOAuth2User.getAttributes()
                .get(KAKAO_ACCOUNT)).get(EMAIL);
        } else {
            email = (String) defaultOAuth2User.getAttributes().get(EMAIL);
        }
        /*@todo
         *   oauth2 provider 를 토큰에 같이 넘겨주기 -> 우선 token에 대한 조사 분석부터*/

        return Jwts.builder()
            .setSubject(email)
            .setClaims(createClaims(oAuth2Provider, email))
            .setIssuedAt(now)
            .setExpiration(expiredDate)
            .signWith(SignatureAlgorithm.HS256, secret)
            .compact();
    }

    private Map<String, Object> createClaims(OAuth2Provider oAuth2Provider, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("provider", oAuth2Provider.name());
        return claims;
    }

    public String getEmailFromToken(String token) {
        return Jwts.parser()
            .setSigningKey(secret)
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            log.info("Token :: {} :: is not valid JWT signature");
        } catch (MalformedJwtException ex) {
            log.info("Token :: {} :: is not valid JWT token");
        } catch (ExpiredJwtException ex) {
            log.info("Token :: {} :: is expired token");
        } catch (IllegalArgumentException ex) {
            log.info("Token :: {} :: claims info is not existed");
        }
        return false;
    }
}
