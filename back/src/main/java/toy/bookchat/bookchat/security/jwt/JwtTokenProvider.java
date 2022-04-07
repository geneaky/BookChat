package toy.bookchat.bookchat.security.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.user.UserPrincipal;

import java.util.Date;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

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

        OAuth2Provider oAuth2Provider = (OAuth2Provider) defaultOAuth2User.getAttributes().get(SOCIAL_TYPE);
        String email;
        if (oAuth2Provider == OAuth2Provider.kakao) {
            email = (String) ((Map<String, Object>) defaultOAuth2User.getAttributes().get(KAKAO_ACCOUNT)).get(EMAIL);
        } else {
            email = (String) defaultOAuth2User.getAttributes().get(EMAIL);
        }

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
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
        } catch(SignatureException ex) {
            log.info("Token :: {} :: is not valid JWT signature");
        } catch(MalformedJwtException ex) {
            log.info("Token :: {} :: is not valid JWT token");
        } catch(ExpiredJwtException ex) {
            log.info("Token :: {} :: is expired token");
        } catch(IllegalArgumentException ex) {
            log.info("Token :: {} :: claims info is not existed");
        }
        return false;
    }
}
