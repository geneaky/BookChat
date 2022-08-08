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
     *   작성하면 이후 io.jwts가 아니라 다른 jwt 구현체를 사용하도록 확장가능
     *   근데 추상화 비용을 생각해보면 추상화된 토큰 방식의 구현체를 변경할 일이 있을까 싶네,,,
     *   사용하고 있는 라이브러리가 deprecated되거나 다른 라이브러리로 변경될때를 대비하는 의미로 할까 고민중
     * */

    public static final String EMAIL = "email";
    public static final String KAKAO_ACCOUNT = "kakao_account";
    public static final String OAUTH2_PROVIDER = "oAuth2Provider";

    private final String secret;

    private final long expiredTime;

    /*@TODO
     *   @Value를 사용해서 설정 정보를 받는 방식에서
     *   별도의 Configuration 파일 (예: TokenConfiguration)을 만들어서
     *   사용하는 것이 설정 값을 한 곳에 묶어서 사용할 수 있고 수정 사항을 한 곳으로
     *   모아둘 수 있음.
     *   @Value방식은 빈으로 등록된 후 Reflection을 사용해서 값을 runtime에 동적으로
     *   넣어주기 때문에 비효율적이고 테스트할 때도 불편했음
     * https://kkambi.tistory.com/210
     *https://tuhrig.de/why-using-springs-value-annotation-is-bad/
     * */
    public JwtTokenProvider(@Value("${token.secret}") String secret,
        @Value("${token.expired_time}") long expiredTime) {
        this.secret = secret;
        this.expiredTime = expiredTime;
    }

    public String createToken(Authentication authentication) {

        Date expiredDate = new Date(new Date().getTime() + expiredTime);

        OAuth2Provider oAuth2Provider = ((UserPrincipal) authentication.getPrincipal()).getUser()
            .getProvider();

        String email = extractEmailInAuthenticationByOAuth2Provider(authentication, oAuth2Provider);

        return Jwts.builder()
            .setSubject("bookchat")
            .setClaims(createClaims(oAuth2Provider, email))
            .setIssuedAt(new Date())
            .setExpiration(expiredDate)
            .signWith(SignatureAlgorithm.HS256, secret)
            .compact();
    }

    private String extractEmailInAuthenticationByOAuth2Provider(Authentication authentication,
        OAuth2Provider oAuth2Provider) {
        if (oAuth2Provider == OAuth2Provider.kakao) {
            return (String) ((Map<String, Object>) ((UserPrincipal) authentication.getPrincipal()).getAttributes()
                .get(KAKAO_ACCOUNT)).get(EMAIL);
        }
        return (String) ((UserPrincipal) authentication.getPrincipal()).getAttributes().get(EMAIL);
    }

    private Map<String, Object> createClaims(OAuth2Provider oAuth2Provider, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(EMAIL, email);
        claims.put(OAUTH2_PROVIDER, oAuth2Provider);
        return claims;
    }

    public String getEmailFromToken(String token) {
        return (String) Jwts.parser()
            .setSigningKey(secret)
            .parseClaimsJws(token)
            .getBody()
            .get(EMAIL);
    }

    public OAuth2Provider getOauth2TokenProviderFromToken(String token) {
        return OAuth2Provider.valueOf(Jwts.parser()
            .setSigningKey(secret)
            .setSigningKey(secret)
            .parseClaimsJws(token)
            .getBody().get(OAUTH2_PROVIDER).toString());
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
