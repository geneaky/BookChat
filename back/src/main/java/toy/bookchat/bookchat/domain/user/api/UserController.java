package toy.bookchat.bookchat.domain.user.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.user.api.dto.UserProfileResponse;
import toy.bookchat.bookchat.domain.user.service.UserService;
import toy.bookchat.bookchat.security.user.UserPrincipal;

@RestController
@RequestMapping("/v1/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/users/profile")
    public ResponseEntity<UserProfileResponse> userProfile(
        @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(UserProfileResponse.of(userPrincipal));
    }

    @GetMapping("/users/profile/nickname")
    public ResponseEntity<Void> checkDuplicatedNickname(String nickname) {
        if(userService.isDuplicatedName(nickname)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        return ResponseEntity.ok(null);
    }
}
