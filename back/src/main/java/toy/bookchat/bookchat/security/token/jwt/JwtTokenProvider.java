package toy.bookchat.bookchat.security.token.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.config.JwtTokenConfig;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.domain.user.api.dto.Token;
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

    private final JwtTokenConfig jwtTokenConfig;

    public JwtTokenProvider(JwtTokenConfig jwtTokenConfig) {
        this.jwtTokenConfig = jwtTokenConfig;
    }

    public String createToken(Authentication authentication) {

        Date expiredDate = new Date(
            new Date().getTime() + jwtTokenConfig.getAccessTokenExpiredTime());

        OAuth2Provider oAuth2Provider = ((UserPrincipal) authentication.getPrincipal()).getUser()
            .getProvider();

        String email = extractEmailInAuthenticationByOAuth2Provider(authentication, oAuth2Provider);

        return Jwts.builder()
            .setSubject("bookchat")
            .setClaims(createClaims(oAuth2Provider, email))
            .setIssuedAt(new Date())
            .setExpiration(expiredDate)
            .signWith(SignatureAlgorithm.RS256, jwtTokenConfig.getSecret())
            .compact();
    }

    private String extractEmailInAuthenticationByOAuth2Provider(Authentication authentication,
        OAuth2Provider oAuth2Provider) {
        if (oAuth2Provider == OAuth2Provider.KAKAO) {
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
            .setSigningKey(jwtTokenConfig.getSecret())
            .parseClaimsJws(token)
            .getBody()
            .get(EMAIL);
    }

    public OAuth2Provider getOauth2TokenProviderFromToken(String token) {
        return OAuth2Provider.valueOf(Jwts.parser()
            .setSigningKey(jwtTokenConfig.getSecret())
            .setSigningKey(jwtTokenConfig.getSecret())
            .parseClaimsJws(token)
            .getBody().get(OAUTH2_PROVIDER).toString());
    }

    public JwtTokenValidationCode validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtTokenConfig.getSecret()).parseClaimsJws(token);
            return JwtTokenValidationCode.ACCESS;
        } catch (ExpiredJwtException ex) {
            log.debug("Token :: {} :: is expired token", token);
            return JwtTokenValidationCode.EXPIRED;
        } catch (Exception ex) {
            log.debug("Token :: {} :: is denied", token);
        }
        return JwtTokenValidationCode.DENIED;
    }

    public Token createToken() {

        return Token.builder()
                .accessToken(createAccessToken())
                .refreshToken(createRefreshToken())
                .build();
    }

    private String createRefreshToken() {
        return null;
    }

    private String createAccessToken() {
        return null;
    }
}
