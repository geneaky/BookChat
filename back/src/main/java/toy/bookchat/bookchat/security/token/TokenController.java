package toy.bookchat.bookchat.security.token;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.user.api.dto.Token;

@RestController
@RequestMapping("/v1/api")
public class TokenController {

    @PostMapping("/auth/token")
    public ResponseEntity<Token> getAccessToken(String refreshToken) {

    }
}
