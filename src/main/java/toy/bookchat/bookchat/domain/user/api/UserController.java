package toy.bookchat.bookchat.domain.user.api;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import toy.bookchat.bookchat.domain.common.RateLimit;
import toy.bookchat.bookchat.domain.user.UserEntity;
import toy.bookchat.bookchat.domain.user.UserProfile;
import toy.bookchat.bookchat.domain.user.api.dto.response.MemberProfileResponse;
import toy.bookchat.bookchat.domain.user.api.dto.response.Token;
import toy.bookchat.bookchat.domain.user.api.dto.response.UserProfileResponse;
import toy.bookchat.bookchat.domain.user.service.UserService;
import toy.bookchat.bookchat.domain.user.service.dto.request.ChangeUserNicknameRequest;
import toy.bookchat.bookchat.domain.user.service.dto.request.UserSignInRequest;
import toy.bookchat.bookchat.domain.user.service.dto.request.UserSignUpRequest;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenProvider;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenRecorder;
import toy.bookchat.bookchat.security.token.openid.IdTokenManager;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPayload;

@Validated
@RestController
@RequestMapping("/v1/api")
public class UserController {

    private final String OIDC = "OIDC";

    private final UserService userService;
    private final IdTokenManager idTokenManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenRecorder jwtTokenRecorder;

    public UserController(UserService userService, IdTokenManager idTokenManager,
        JwtTokenProvider jwtTokenProvider, JwtTokenRecorder jwtTokenRecorder) {
        this.userService = userService;
        this.idTokenManager = idTokenManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtTokenRecorder = jwtTokenRecorder;
    }

    @GetMapping("/users/profile")
    public UserProfileResponse userProfile(@UserPayload TokenPayload tokenPayload) {
        UserProfile userProfile = userService.findUser(tokenPayload.getUserId());
        return UserProfileResponse.of(userProfile);
    }

    @GetMapping("/users/profile/nickname")
    @RateLimit(keyName = "nicknameCall", capacity = 300, tokens = 100, seconds = 1)
    public ResponseEntity<Void> checkDuplicatedNickname(String nickname) {
        if (userService.isDuplicatedName(nickname)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/profile")
    public void updateUserProfile(
        @Valid @RequestPart ChangeUserNicknameRequest changeUserNicknameRequest,
        @RequestPart(required = false) MultipartFile userProfileImage,
        @UserPayload TokenPayload tokenPayload) {
        userService.updateUserProfile(changeUserNicknameRequest, userProfileImage,
            tokenPayload.getUserId());
    }

    @PostMapping("/users/signup")
    public void userSignUp(@Valid @RequestPart UserSignUpRequest userSignUpRequest,
        @RequestPart(required = false) MultipartFile userProfileImage,
        @RequestHeader(OIDC) @NotBlank @Pattern(regexp = "^(Bearer)\\s.+") String bearerToken) {
        String oauth2MemberNumber = idTokenManager.getOAuth2MemberNumberFromIdToken(bearerToken,
            userSignUpRequest.getOauth2Provider());
        String userEmail = idTokenManager.getUserEmailFromToken(bearerToken,
            userSignUpRequest.getOauth2Provider());

        userService.registerNewUser(userSignUpRequest, userProfileImage, oauth2MemberNumber, userEmail);
    }

    @PostMapping("/users/signin")
    public Token userSignIn(
        @RequestHeader(OIDC) @NotBlank @Pattern(regexp = "^(Bearer)\\s.+") String bearerToken,
        @Valid @RequestBody UserSignInRequest userSignInRequest) {
        String userName = idTokenManager.getOAuth2MemberNumberFromIdToken(bearerToken, userSignInRequest.getOauth2Provider());

        UserEntity userEntity = userService.findUserByUsername(userName);
        userService.checkDevice(userSignInRequest, userEntity);

        Token token = jwtTokenProvider.createToken(userEntity);
        jwtTokenRecorder.record(userEntity.getId(), token.getRefreshToken());

        return token;
    }

    @PostMapping("/users/logout")
    public void userLogout(@UserPayload TokenPayload tokenPayload) {
        userService.deleteDevice(tokenPayload.getUserId());
    }

    @DeleteMapping("/users")
    public void withdrawUser(@UserPayload TokenPayload tokenPayload) {
        userService.deleteUser(tokenPayload.getUserId());
    }

    @GetMapping("/members")
    public MemberProfileResponse memberProfile(@RequestParam Long memberId) {
        return userService.getMemberProfile(memberId);
    }
}
