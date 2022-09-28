package toy.bookchat.bookchat.domain.user.api;

import static toy.bookchat.bookchat.utils.constants.AuthConstants.AUTHORIZATION;
import static toy.bookchat.bookchat.utils.constants.AuthConstants.BEGIN_INDEX;
import static toy.bookchat.bookchat.utils.constants.AuthConstants.PROVIDER_TYPE;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.user.api.dto.Token;
import toy.bookchat.bookchat.domain.user.api.dto.UserProfileResponse;
import toy.bookchat.bookchat.domain.user.service.UserService;
import toy.bookchat.bookchat.domain.user.service.dto.UserSignUpRequestDto;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.token.TokenManager;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenProvider;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenRecorder;
import toy.bookchat.bookchat.security.user.UserPrincipal;

@Validated
@RestController
@RequestMapping("/v1/api")
public class UserController {

    private final UserService userService;
    private final TokenManager openIdTokenManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenRecorder jwtTokenRecorder;

    public UserController(UserService userService, TokenManager openIdTokenManager,
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
        @RequestHeader(AUTHORIZATION) @NotBlank @Pattern(regexp = "^(Bearer)\\s.+") String bearerToken,
        @RequestHeader(PROVIDER_TYPE) @NotNull OAuth2Provider oAuth2Provider) {

        String oauth2MemberNumber = openIdTokenManager.getOAuth2MemberNumberFromToken(
            getOpenIdToken(bearerToken), oAuth2Provider);
        String userEmail = openIdTokenManager.getUserEmailFromToken(getOpenIdToken(bearerToken),
            oAuth2Provider);
        userService.registerNewUser(userSignUpRequestDto, oauth2MemberNumber, userEmail,
            oAuth2Provider);

        return ResponseEntity.ok(null);
    }

    @PostMapping("/users/signin")
    public ResponseEntity<Token> userSignIn(
        @RequestHeader(AUTHORIZATION) @NotBlank @Pattern(regexp = "^(Bearer)\\s.+") String bearerToken,
        @RequestHeader(PROVIDER_TYPE) @NotNull OAuth2Provider oAuth2Provider) {

        String userName = openIdTokenManager.getOAuth2MemberNumberFromToken(
            getOpenIdToken(bearerToken), oAuth2Provider);
        userService.checkRegisteredUser(userName);
        String userEmail = openIdTokenManager.getUserEmailFromToken(getOpenIdToken(bearerToken),
            oAuth2Provider);

        Token token = jwtTokenProvider.createToken(userName, userEmail, oAuth2Provider);
        jwtTokenRecorder.record(userName, token.getRefreshToken());

        return ResponseEntity.ok(token);
    }

    private String getOpenIdToken(String bearerToken) {
        return bearerToken.substring(BEGIN_INDEX);
    }
}
