package toy.bookchat.bookchat.domain.user.api.v1;

import static toy.bookchat.bookchat.support.Constants.OIDC;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
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
import toy.bookchat.bookchat.db_module.user.UserEntity;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.api.v1.request.ChangeUserNicknameRequest;
import toy.bookchat.bookchat.domain.user.api.v1.request.UserSignInRequest;
import toy.bookchat.bookchat.domain.user.api.v1.request.UserSignUpRequest;
import toy.bookchat.bookchat.domain.user.api.v1.response.MemberProfileResponse;
import toy.bookchat.bookchat.domain.user.api.v1.response.Token;
import toy.bookchat.bookchat.domain.user.api.v1.response.UserProfileResponse;
import toy.bookchat.bookchat.domain.user.service.UserService;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenProvider;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenRecorder;
import toy.bookchat.bookchat.security.token.openid.IdTokenManager;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPayload;
import toy.bookchat.bookchat.support.RateLimit;

@RequiredArgsConstructor

@Validated
@RestController
@RequestMapping("/v1/api")
public class UserController {

  private final UserService userService;
  private final IdTokenManager idTokenManager;
  private final JwtTokenProvider jwtTokenProvider;
  private final JwtTokenRecorder jwtTokenRecorder;

  /**
   * 유저 프로필 조회
   *
   * @param tokenPayload
   * @return
   */
  @GetMapping("/users/profile")
  public UserProfileResponse userProfile(@UserPayload TokenPayload tokenPayload) {
    User user = userService.findUser(tokenPayload.getUserId());
    return UserProfileResponse.of(user);
  }

  /**
   * 닉네임 중복 체크
   *
   * @param nickname
   * @return
   */
  @GetMapping("/users/profile/nickname")
  @RateLimit(keyName = "nicknameCall", capacity = 300, tokens = 100, seconds = 1)
  public ResponseEntity<Void> checkDuplicatedNickname(String nickname) {
    if (userService.isDuplicatedName(nickname)) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
    }
    return ResponseEntity.ok().build();
  }

  /**
   * 유저 프로필 수정
   *
   * @param changeUserNicknameRequest
   * @param userProfileImage
   * @param tokenPayload
   */
  @PostMapping("/users/profile")
  public void updateUserProfile(@Valid @RequestPart ChangeUserNicknameRequest changeUserNicknameRequest,
      @RequestPart(required = false) MultipartFile userProfileImage, @UserPayload TokenPayload tokenPayload) {
    userService.updateUserProfile(changeUserNicknameRequest, userProfileImage, tokenPayload.getUserId());
  }

  /**
   * 회원가입
   *
   * @param userSignUpRequest
   * @param userProfileImage
   * @param bearerToken
   */
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

  /**
   * 로그인
   *
   * @param bearerToken
   * @param userSignInRequest
   * @return
   */
  @PostMapping("/users/signin")
  public Token userSignIn(
      @RequestHeader(OIDC) @NotBlank @Pattern(regexp = "^(Bearer)\\s.+") String bearerToken,
      @Valid @RequestBody UserSignInRequest userSignInRequest) {
    String userName = idTokenManager.getOAuth2MemberNumberFromIdToken(bearerToken,
        userSignInRequest.getOauth2Provider());

    UserEntity userEntity = userService.findUserByUsername(userName);
    userService.checkDevice(userSignInRequest, userEntity.getId());

    Token token = jwtTokenProvider.createToken(userEntity);
    jwtTokenRecorder.record(userEntity.getId(), token.getRefreshToken());

    return token;
  }

  /**
   * 로그아웃
   *
   * @param tokenPayload
   */
  @PostMapping("/users/logout")
  public void userLogout(@UserPayload TokenPayload tokenPayload) {
    userService.deleteDevice(tokenPayload.getUserId());
  }

  /**
   * 회원탈퇴
   *
   * @param tokenPayload
   */
  @DeleteMapping("/users")
  public void withdrawUser(@UserPayload TokenPayload tokenPayload) {
    userService.deleteUser(tokenPayload.getUserId());
  }

  /**
   * 회원 프로필 조회
   *
   * @param memberId
   * @return
   */
  @GetMapping("/members")
  public MemberProfileResponse memberProfile(@RequestParam Long memberId) {
    return userService.getMemberProfile(memberId);
  }
}
