package toy.bookchat.bookchat.security.token;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.user.api.dto.Token;

@RestController
@RequestMapping("/v1/api")
public class TokenController {
    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/auth/token")
    public ResponseEntity<Token> getAccessToken(@RequestBody String refreshTokenRequestDto) {
        return ResponseEntity.ok(tokenService.generateToken(refreshTokenRequestDto));
    }
}
