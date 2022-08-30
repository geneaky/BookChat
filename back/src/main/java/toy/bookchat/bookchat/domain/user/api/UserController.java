package toy.bookchat.bookchat.domain.user.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import toy.bookchat.bookchat.domain.user.api.dto.UserProfileResponse;
import toy.bookchat.bookchat.domain.user.service.UserService;
import toy.bookchat.bookchat.domain.user.service.dto.UserSignUpRequestDto;
import toy.bookchat.bookchat.security.jwt.JwtTokenManager;
import toy.bookchat.bookchat.security.user.UserPrincipal;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/v1/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenManager jwtTokenManager;

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final int BEGIN_INDEX = 7;

    @GetMapping("/users/profile")
    public ResponseEntity<UserProfileResponse> userProfile(
        @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(UserProfileResponse.of(userPrincipal));
    }

    /* TODO: 2022-08-29 인터셉터 적용해서 1분안에 50번 요청 보낼시 1시간동안
        해당 ip block
     */
    @GetMapping("/users/profile/nickname")
    public ResponseEntity<Void> checkDuplicatedNickname(String nickname) {
        if(userService.isDuplicatedName(nickname)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        return ResponseEntity.ok(null);
    }

    /* TODO: 2022-08-29
        추후: 사용자 프로필의 경우 프로필 수정을 누르지 않으면 동일한 데이터에 대해 read
        연산을 하므로 캐시할 수 있을듯 하다
     */
    @PostMapping("/users")
    public ResponseEntity<Void> userSignUp(@Valid @ModelAttribute UserSignUpRequestDto userSignUpRequestDto, HttpServletRequest request) {

        if(isNotValidatedRequest(request)) {
            return ResponseEntity.badRequest().body(null);
        }

        String oauth2MemberNumber = jwtTokenManager.isNotValidatedToken(getOpenIdToken(request));
        userService.registerNewUser(userSignUpRequestDto, oauth2MemberNumber);

        return ResponseEntity.ok(null);
    }

    private String getOpenIdToken(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION).substring(BEGIN_INDEX);
    }

    public boolean isNotValidatedRequest(HttpServletRequest request) {
        Optional<String> authorization = Optional.ofNullable(request.getHeader(AUTHORIZATION));
        return authorization.isEmpty() || !authorization.get().startsWith(BEARER) || !StringUtils.hasText(authorization.get().substring(BEGIN_INDEX));
    }

}
