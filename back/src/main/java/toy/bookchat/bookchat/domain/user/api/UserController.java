package toy.bookchat.bookchat.domain.user.api;

import static toy.bookchat.bookchat.utils.constants.AuthConstants.AUTHORIZATION;
import static toy.bookchat.bookchat.utils.constants.AuthConstants.BEARER;
import static toy.bookchat.bookchat.utils.constants.AuthConstants.BEGIN_INDEX;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.user.api.dto.UserProfileResponse;
import toy.bookchat.bookchat.domain.user.service.UserService;
import toy.bookchat.bookchat.domain.user.service.dto.UserSignUpRequestDto;
import toy.bookchat.bookchat.security.openid.OpenIdTokenManager;
import toy.bookchat.bookchat.security.user.UserPrincipal;

@RestController
@RequestMapping("/v1/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final OpenIdTokenManager openIdTokenManager;

    /* TODO: 2022-08-29
        추후: 사용자 프로필의 경우 프로필 수정을 누르지 않으면 동일한 데이터에 대해 read
        연산을 하므로 캐시할 수 있을듯 하다
        s3와 cdn을 사용해서 사용자 프로필을 캐시해서 제공해 성능 개선
    */
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
        if (userService.isDuplicatedName(nickname)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        return ResponseEntity.ok(null);
    }

    @PostMapping("/users")
    public ResponseEntity<Void> userSignUp(
        @Valid @ModelAttribute UserSignUpRequestDto userSignUpRequestDto,
        HttpServletRequest request) {

        if (isNotValidatedRequest(request)) {
            return ResponseEntity.badRequest().body(null);
        }

        String oauth2MemberNumber = openIdTokenManager.getOauth2MemberNumberFromRequest(
            getOpenIdToken(request));
        userService.registerNewUser(userSignUpRequestDto, oauth2MemberNumber);

        return ResponseEntity.ok(null);
    }

    private String getOpenIdToken(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION).substring(BEGIN_INDEX);
    }

    public boolean isNotValidatedRequest(HttpServletRequest request) {
        Optional<String> authorization = Optional.ofNullable(request.getHeader(AUTHORIZATION));
        return authorization.isEmpty() || !authorization.get().startsWith(BEARER)
            || !StringUtils.hasText(authorization.get().substring(BEGIN_INDEX));
    }

}
