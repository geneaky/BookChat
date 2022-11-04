package toy.bookchat.bookchat.domain.user.api;

import static toy.bookchat.bookchat.utils.constants.AuthConstants.OIDC;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.api.dto.Token;
import toy.bookchat.bookchat.domain.user.api.dto.UserProfileResponse;
import toy.bookchat.bookchat.domain.user.service.UserService;
import toy.bookchat.bookchat.domain.user.service.dto.request.ChangeUserNicknameRequest;
import toy.bookchat.bookchat.domain.user.service.dto.request.UserSignInRequest;
import toy.bookchat.bookchat.domain.user.service.dto.request.UserSignUpRequest;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenProvider;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenRecorder;
import toy.bookchat.bookchat.security.token.openid.OpenIdTokenManager;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPayload;

@Validated
@RestController
@RequestMapping("/v1/api")
public class UserController {

    private final UserService userService;
    private final OpenIdTokenManager openIdTokenManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenRecorder jwtTokenRecorder;

    public UserController(UserService userService, OpenIdTokenManager openIdTokenManager,
        JwtTokenProvider jwtTokenProvider, JwtTokenRecorder jwtTokenRecorder) {
        this.userService = userService;
        this.openIdTokenManager = openIdTokenManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtTokenRecorder = jwtTokenRecorder;
    }

    /* TODO: 2022-08-29
            추후: 사용자 프로필의 경우 프로필 수정을 누르지 않으면 동일한 데이터에 대해 read
            연산을 하므로 캐시할 수 있을듯 하다
            s3와 cdn을 사용해서 사용자 프로필을 캐시해서 제공해 성능 개선
        */
    @GetMapping("/users/profile")
    public UserProfileResponse userProfile(@UserPayload TokenPayload tokenPayload) {

        return UserProfileResponse.of(tokenPayload);
    }

    /* TODO: 2022-08-29 인터셉터 적용해서 1분안에 50번 요청 보낼시 1시간동안
        해당 ip block
     */
    @GetMapping("/users/profile/nickname")
    public ResponseEntity<Void> checkDuplicatedNickname(String nickname) {
        if (userService.isDuplicatedName(nickname)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/user")
    public void changeUserNickName(
        @Valid @RequestBody ChangeUserNicknameRequest changeUserNicknameRequest,
        @UserPayload TokenPayload tokenPayload) {
        userService.changeUserNickname(changeUserNicknameRequest, tokenPayload.getUserId());
    }

    @PostMapping("/users/signup")
    public void userSignUp(@Valid @RequestPart UserSignUpRequest userSignUpRequest,
        @RequestPart(required = false) MultipartFile userProfileImage,
        @RequestHeader(OIDC) @NotBlank @Pattern(regexp = "^(Bearer)\\s.+") String bearerToken) {
        String oauth2MemberNumber = openIdTokenManager.getOAuth2MemberNumberFromToken(bearerToken,
            userSignUpRequest.getOauth2Provider());
        String userEmail = openIdTokenManager.getUserEmailFromToken(bearerToken,
            userSignUpRequest.getOauth2Provider());

        userService.registerNewUser(userSignUpRequest, userProfileImage, oauth2MemberNumber,
            userEmail);
    }

    @PostMapping("/users/signin")
    public Token userSignIn(
        @RequestHeader(OIDC) @NotBlank @Pattern(regexp = "^(Bearer)\\s.+") String bearerToken,
        @Valid @RequestBody UserSignInRequest userSignInRequest) {
        String userName = openIdTokenManager.getOAuth2MemberNumberFromToken(bearerToken,
            userSignInRequest.getOauth2Provider());
        User user = userService.findUserByUsername(userName);

        Token token = jwtTokenProvider.createToken(user);
        jwtTokenRecorder.record(user.getId(), token.getRefreshToken());

        return token;
    }

    @DeleteMapping("/users")
    public void withdrawUser(@UserPayload TokenPayload tokenPayload) {
        userService.deleteUser(tokenPayload.getUserId());
    }
}
