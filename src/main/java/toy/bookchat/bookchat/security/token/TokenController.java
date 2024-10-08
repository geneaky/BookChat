package toy.bookchat.bookchat.security.token;

import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.user.api.v1.response.Token;
import toy.bookchat.bookchat.security.token.dto.RefreshTokenRequest;

@RestController
@RequestMapping("/v1/api")
public class TokenController {

  private final TokenService tokenService;

  public TokenController(TokenService tokenService) {
    this.tokenService = tokenService;
  }

  @PostMapping("/auth/token")
  public ResponseEntity<Token> getAccessToken(
      @Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
    return ResponseEntity.ok(tokenService.generateToken(refreshTokenRequest));
  }
}
