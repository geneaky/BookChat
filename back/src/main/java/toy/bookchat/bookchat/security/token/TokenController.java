package toy.bookchat.bookchat.security.token;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.user.api.dto.Token;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenProvider;

@RestController
@RequestMapping("/v1/api")
public class TokenController {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenManager jwtTokenManager;

    public TokenController(JwtTokenProvider jwtTokenProvider,
        @Qualifier("jwtTokenManager") TokenManager jwtTokenManager) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtTokenManager = jwtTokenManager;
    }

    @PostMapping("/auth/token")
    public ResponseEntity<Token> getAccessToken(@RequestBody String refreshToken) {

        String userName = jwtTokenManager.getOAuth2MemberNumberFromToken(refreshToken, null);
        String userEmail = jwtTokenManager.getUserEmailFromToken(refreshToken, null);
        OAuth2Provider oAuth2Provider = jwtTokenManager.getOAuth2ProviderFromToken(
            refreshToken);
        Token token = jwtTokenProvider.createToken(userName, userEmail, oAuth2Provider);

        /*
        리프레시토큰 기간 2주 만료 3일 이내에 요청보낸경우 자동 갱신

         */
        return ResponseEntity.ok(token);
    }
}
