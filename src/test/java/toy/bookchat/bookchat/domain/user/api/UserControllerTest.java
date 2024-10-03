package toy.bookchat.bookchat.domain.user.api;

import static io.jsonwebtoken.SignatureAlgorithm.RS256;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static toy.bookchat.bookchat.domain.user.ReadingTaste.ART;
import static toy.bookchat.bookchat.domain.user.ReadingTaste.DEVELOPMENT;
import static toy.bookchat.bookchat.domain.user.ReadingTaste.SCIENCE;
import static toy.bookchat.bookchat.security.oauth.OAuth2Provider.KAKAO;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.Base64Utils;
import toy.bookchat.bookchat.db_module.user.UserEntity;
import toy.bookchat.bookchat.domain.ControllerTestExtension;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.api.v1.request.ChangeUserNicknameRequest;
import toy.bookchat.bookchat.domain.user.api.v1.request.UserSignInRequest;
import toy.bookchat.bookchat.domain.user.api.v1.request.UserSignUpRequest;
import toy.bookchat.bookchat.domain.user.api.v1.response.MemberProfileResponse;
import toy.bookchat.bookchat.domain.user.api.v1.response.Token;
import toy.bookchat.bookchat.domain.user.service.UserService;
import toy.bookchat.bookchat.exception.conflict.device.DeviceAlreadyRegisteredException;
import toy.bookchat.bookchat.exception.unauthorized.ExpiredTokenException;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenProvider;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenRecorder;
import toy.bookchat.bookchat.security.token.openid.OpenIdTestUtil;

@UserPresentationTest
class UserControllerTest extends ControllerTestExtension {

  public final String JWT_TOKEN = getTestToken();
  private final String OIDC = "OIDC";
  private final String BEARER = "Bearer ";


  @MockBean
  UserService userService;
  @MockBean
  JwtTokenProvider jwtTokenProvider;
  @MockBean
  JwtTokenRecorder jwtTokenRecorder;
  @Autowired
  ObjectMapper objectMapper;
  OpenIdTestUtil openIdTestUtil;
  @Autowired
  private MockMvc mockMvc;

  private Map<String, Object> getClaims() {
    Map<String, Object> claims = new HashMap<>();
    claims.put("email", "test@gmail.com");
    claims.put("iss", "https://kauth.kakao.com");
    claims.put("sub", "test");
    return claims;
  }

  @BeforeEach
  public void init() throws FileNotFoundException {
    openIdTestUtil = new OpenIdTestUtil(
        "src/test/java/toy/bookchat/bookchat/security/token/openid/token_key.pem",
        "src/test/java/toy/bookchat/bookchat/security/token/openid/openidRSA256-public.pem");
  }

  @Test
  void 인증받지_않은_사용자_요청_401응답() throws Exception {
    when(getJwtTokenManager().getTokenPayloadFromToken(any())).thenReturn(null);
    mockMvc.perform(get("/v1/api/users/profile"))
        .andExpect(status().isUnauthorized())
        .andDo(document("user-profile-error"));
  }

  @Test
  @DisplayName("사용자 프로필 정보 반환")
  void userProfile() throws Exception {
    UserEntity userEntity = getUser();
    User user = User.builder()
        .id(userEntity.getId())
        .email(userEntity.getEmail())
        .nickname(userEntity.getNickname())
        .profileImageUrl(userEntity.getProfileImageUrl())
        .defaultProfileImageType(userEntity.getDefaultProfileImageType())
        .build();

    given(userService.findUser(any())).willReturn(user);

    mockMvc.perform(get("/v1/api/users/profile")
            .with(user(getUserPrincipal()))
            .header(AUTHORIZATION, JWT_TOKEN))
        .andExpect(status().isOk())
        .andDo(document("user",
            requestHeaders(
                headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
            ),
            responseFields(
                fieldWithPath("userId").type(NUMBER).description("사용자 ID"),
                fieldWithPath("userNickname").type(STRING).description("닉네임"),
                fieldWithPath("userProfileImageUri").type(STRING).description("프로필 사진 URI"),
                fieldWithPath("defaultProfileImageType").type(NUMBER).description("기본 이미지 타입")
            )));
  }

  @Test
  void 사용자_닉네임_중복_아닐시_200반환() throws Exception {
    when(userService.isDuplicatedName(anyString())).thenReturn(false);
    mockMvc.perform(get("/v1/api/users/profile/nickname").param("nickname", "HiBs"))
        .andExpect(status().isOk())
        .andDo(document("user-nickname", requestParameters(
            parameterWithName("nickname").description("사용자 nickname")
        )));
  }

  @Test
  void 사용자_닉네임_중복시_409반환() throws Exception {
    when(userService.isDuplicatedName(anyString())).thenReturn(true);
    mockMvc.perform(get("/v1/api/users/profile/nickname").param("nickname", "HiBs"))
        .andExpect(status().isConflict())
        .andDo(document("user-nickname-error"));
  }

  @Test
  void 사용자_회원가입_요청시_header_인증정보_없을시_400반환() throws Exception {
    mockMvc.perform(post("/v1/api/users/signup")
            .header(AUTHORIZATION, " ")
            .param("nickname", "nick")
            .param("userEmail", "kaktus418@gmail.com")
            .param("oauth2Provider", "kakao")
            .param("defaultProfileImageType", "2"))
        .andExpect(status().isBadRequest())
        .andDo(document("user-signup-error1"));
  }

  @Test
  void 사용자_회원가입_요청시_header_openid없는_인증정보_400반환() throws Exception {
    mockMvc.perform(post("/v1/api/users/signup")
            .header(AUTHORIZATION, "Bearer ")
            .param("nickname", "nick")
            .param("defaultProfileImageType", "2"))
        .andExpect(status().isBadRequest())
        .andDo(document("user-signup-error2"));
  }

  private X509EncodedKeySpec getPublicPkcs8EncodedKeySpec(OpenIdTestUtil openIdTestUtil)
      throws IOException {
    String publicKey = openIdTestUtil.getPublicKey(9);
    byte[] decodePublicKey = Base64Utils.decode(publicKey.getBytes());
    return new X509EncodedKeySpec(decodePublicKey);
  }

  private PKCS8EncodedKeySpec getPrivatePkcs8EncodedKeySpec(OpenIdTestUtil openIdTestUtil)
      throws IOException {
    String privateKey = openIdTestUtil.getPrivateKey(28);
    byte[] decodePrivateKey = Base64Utils.decode(privateKey.getBytes());
    return new PKCS8EncodedKeySpec(
        decodePrivateKey);
  }

  private PublicKey getPublicKey()
      throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    X509EncodedKeySpec publicKeySpec = getPublicPkcs8EncodedKeySpec(openIdTestUtil);
    return keyFactory.generatePublic(publicKeySpec);
  }

  private PrivateKey getPrivateKey()
      throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    PKCS8EncodedKeySpec privateKeySpec = getPrivatePkcs8EncodedKeySpec(openIdTestUtil);
    return keyFactory.generatePrivate(privateKeySpec);
  }

  @Test
  void 사용자_회원가입_요청시_header_openid가_유효하지않은_경우_401반환() throws Exception {
    PrivateKey privateKey = getPrivateKey();
    PublicKey publicKey = getPublicKey();
    MockMultipartFile multipartFile = new MockMultipartFile("userProfileImage",
        "testImage".getBytes());

    UserSignUpRequest userSignUpRequest = UserSignUpRequest.builder()
        .nickname("nick")
        .defaultProfileImageType(1)
        .oauth2Provider(KAKAO)
        .readingTastes(List.of(DEVELOPMENT, ART, SCIENCE))
        .build();

    Claims claims = Jwts.claims().setIssuer("https://kauth.kakao.com")
        .setSubject("test").setExpiration(new Date(0));
    String testToken = Jwts.builder()
        .setHeaderParam("kid", "abcdefg")
        .setClaims(claims)
        .signWith(RS256, privateKey).compact();

    when(getPublicKeyFetcher().getPublicKey(any(), any())).thenReturn(
        publicKey);

    when(getIdTokenManager().getOAuth2MemberNumberFromIdToken(any(), any())).thenThrow(
        new ExpiredTokenException());

    mockMvc.perform(multipart("/v1/api/users/signup")
            .file(multipartFile)
            .file(new MockMultipartFile("userSignUpRequest", "", "application/json",
                objectMapper.writeValueAsString(userSignUpRequest)
                    .getBytes(UTF_8)))
            .header(OIDC, BEARER + testToken))
        .andExpect(status().isUnauthorized())
        .andDo(document("user-signup-error4"));
  }

  @Test
  void 사용자_회원가입_요청시_header_openid가_유효한경우_회원가입진행() throws Exception {
    PrivateKey privateKey = getPrivateKey();
    PublicKey publicKey = getPublicKey();

    when(getPublicKeyFetcher().getPublicKey(any(), any())).thenReturn(
        publicKey);
    when(getIdTokenManager().getOAuth2MemberNumberFromIdToken(any(), any())).thenReturn(
        "testkakao");
    when(getIdTokenManager().getUserEmailFromToken(any(), any())).thenReturn(
        "test@gmail.com");

    String testToken = Jwts.builder()
        .setHeaderParam("kid", "abcedf")
        .setClaims(getClaims())
        .signWith(RS256, privateKey)
        .compact();

    MockMultipartFile multipartFile = new MockMultipartFile("userProfileImage",
        "test".getBytes());
    UserSignUpRequest userSignUpRequest = UserSignUpRequest.builder()
        .nickname("nick")
        .defaultProfileImageType(1)
        .oauth2Provider(KAKAO)
        .readingTastes(List.of(DEVELOPMENT, ART, SCIENCE))
        .build();

    mockMvc.perform(multipart("/v1/api/users/signup")
            .file(multipartFile)
            .file(new MockMultipartFile("userSignUpRequest", "", APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(userSignUpRequest)
                    .getBytes(UTF_8)))
            .header(OIDC, BEARER + testToken))
        .andExpect(status().isOk())
        .andDo(document("user-signup", requestHeaders(
                headerWithName(OIDC).description("Bearer [openid token]")
            ),
            requestParts(
                partWithName("userSignUpRequest").description("회원가입 입력 폼"),
                partWithName("userProfileImage").optional()
                    .description("프로필 이미지 [200 x 200].webp")
            ),
            requestPartFields("userSignUpRequest",
                fieldWithPath("nickname").description("닉네임"),
                fieldWithPath("defaultProfileImageType").optional().description("기본 이미지 타입"),
                fieldWithPath("readingTastes").optional().description("독서 취향"),
                fieldWithPath("oauth2Provider").description("프로바이더 타입[kakao/google]")
            )));

    verify(userService).registerNewUser(any(), any(), any(), any());
  }

  @Test
  void 토큰형식_정규표현식_확인() throws Exception {
    PrivateKey privateKey = getPrivateKey();

    String testToken = Jwts.builder().setSubject("test")
        .signWith(RS256, privateKey).compact();

    String A = "Tearer " + testToken;

    String B = BEARER + testToken;

    assertAll(
        () -> assertThat(A.matches("^(Bearer)\\s.+")).isFalse(),
        () -> assertThat(B.matches("^(Bearer)\\s.+")).isTrue()
    );

  }

  @Test
  void 사용자_회원가입_요청시_올바르지않은_토큰_요청규격_예외처리() throws Exception {
    PrivateKey privateKey = getPrivateKey();

    String testToken = Jwts.builder().setSubject("test")
        .signWith(RS256, privateKey).compact();

    mockMvc.perform(post("/v1/api/users/signup")
            .header(AUTHORIZATION, "Tearer" + testToken)
            .param("defaultProfileImageType", "1")
            .param("nickname", "testName"))
        .andExpect(status().isBadRequest())
        .andDo(document("user-signup-error3"));
  }

  @Test
  void 사용자_회원가입_요청시_올바르지않은_파라미터형식_예외처리() throws Exception {
    PrivateKey privateKey = getPrivateKey();

    String testToken = Jwts.builder().setSubject("test")
        .signWith(RS256, privateKey).compact();

    mockMvc.perform(post("/v1/api/users/signup")
            .header(AUTHORIZATION, BEARER + testToken)
            .param("nickname", "")
            .param("userEmail", "abcdefg")
            .param("oauth2Provider", ""))
        .andExpect(status().isBadRequest())
        .andDo(document("user-signup-error5"));
  }

  @Test
  void 사용자_성공적으로_로그인시_토큰_반환() throws Exception {
    PrivateKey privateKey = getPrivateKey();
    PublicKey publicKey = getPublicKey();

    when(getPublicKeyFetcher().getPublicKey(any(), any())).thenReturn(
        publicKey);

    String testToken = Jwts.builder()
        .setHeaderParam("kid", "abcedf")
        .setClaims(getClaims())
        .signWith(RS256, privateKey)
        .compact();

    Token responseToken = Token.builder()
        .accessToken("accessToken")
        .refreshToken("refreshToken")
        .build();

    UserSignInRequest userSignInRequest = UserSignInRequest.builder()
        .oauth2Provider(KAKAO)
        .fcmToken("5Nh2lN")
        .deviceToken("JI82uSMi")
        .approveChangingDevice(false)
        .build();

    when(jwtTokenProvider.createToken(any())).thenReturn(responseToken);
    when(userService.findUserByUsername(any())).thenReturn(getUser());
    MvcResult mvcResult = mockMvc.perform(post("/v1/api/users/signin")
            .header(OIDC, BEARER + testToken)
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userSignInRequest)))
        .andExpect(status().isOk())
        .andDo(document("user-signin",
            requestHeaders(
                headerWithName(OIDC).description("Bearer [openid token]")
            ),
            requestFields(
                fieldWithPath("oauth2Provider").type(STRING)
                    .description("프로바이더 타입[kakao/google]"),
                fieldWithPath("fcmToken").type(STRING).description("FCM Token"),
                fieldWithPath("deviceToken").type(STRING).description("Unique Device Id"),
                fieldWithPath("approveChangingDevice").type(BOOLEAN).optional()
                    .description("기기 변경 승인")
            ),
            responseFields(
                fieldWithPath("accessToken").type(STRING).description("Access Token"),
                fieldWithPath("refreshToken").type(STRING).description("Refresh Token")
            )))
        .andReturn();

    verify(jwtTokenRecorder).record(any(), any());
    assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo(
        objectMapper.writeValueAsString(responseToken));
  }

  @Test
  void 사용자가_새로운_기기로_접속시_승인이없다면_충돌응답() throws Exception {
    PrivateKey privateKey = getPrivateKey();
    PublicKey publicKey = getPublicKey();

    String testToken = Jwts.builder()
        .setHeaderParam("kid", "abcedf")
        .setClaims(getClaims())
        .signWith(RS256, privateKey)
        .compact();

    Token responseToken = Token.builder()
        .accessToken("accessToken")
        .refreshToken("refreshToken")
        .build();

    UserSignInRequest userSignInRequest = UserSignInRequest.builder()
        .oauth2Provider(KAKAO)
        .fcmToken("5Nh2lN")
        .deviceToken("JI82uSMi")
        .approveChangingDevice(false)
        .build();

    when(getPublicKeyFetcher().getPublicKey(any(), any())).thenReturn(publicKey);
    doThrow(new DeviceAlreadyRegisteredException()).when(userService).checkDevice(any(), any());
    when(jwtTokenProvider.createToken(any())).thenReturn(responseToken);
    when(userService.findUserByUsername(any())).thenReturn(getUser());

    mockMvc.perform(post("/v1/api/users/signin")
            .header(OIDC, BEARER + testToken)
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userSignInRequest)))
        .andExpect(status().isConflict());
  }

  @Test
  void 로그인_요청시_Header없으면_예외발생() throws Exception {
    mockMvc.perform(post("/v1/api/users/signin"))
        .andExpect(status().isBadRequest())
        .andDo(document("user-signin-error1"));
  }

  @Test
  void 로그인_요청시_Header에_토큰이_없으면_예외발생() throws Exception {
    mockMvc.perform(post("/v1/api/users/signin")
            .header(OIDC, BEARER))
        .andExpect(status().isBadRequest())
        .andDo(document("user-signin-error2"));
  }

  @Test
  void 로그인_요청시_Header인증정보가_Bearer양식에_맞지않으면_예외발생() throws Exception {
    PrivateKey privateKey = getPrivateKey();
    PublicKey publicKey = getPublicKey();

    when(getPublicKeyFetcher().getPublicKey(any(), any())).thenReturn(
        publicKey);

    String testToken = Jwts.builder()
        .setHeaderParam("kid", "abcedf")
        .setClaims(getClaims())
        .signWith(RS256, privateKey)
        .compact();

    mockMvc.perform(post("/v1/api/users/signin")
            .header(OIDC, "Tearer " + testToken))
        .andExpect(status().isBadRequest())
        .andDo(document("user-signin-error3"));
  }

  @Test
  void 만료된_토큰으로_요청시_401_예외발생() throws Exception {
    PrivateKey privateKey = getPrivateKey();

    when(getIdTokenManager().getOAuth2MemberNumberFromIdToken(any(), any())).thenThrow(
        new ExpiredTokenException());

    Claims claims = Jwts.claims().setIssuer("https://kauth.kakao.com")
        .setSubject("test").setExpiration(new Date(0));
    String testToken = Jwts.builder()
        .setHeaderParam("kid", "abcdefg")
        .setClaims(claims)
        .signWith(RS256, privateKey).compact();

    mockMvc.perform(post("/v1/api/users/signin")
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                UserSignInRequest.builder().oauth2Provider(KAKAO).fcmToken("jMYy")
                    .deviceToken("40vB5")
                    .build()))
            .header(OIDC, BEARER + testToken))
        .andExpect(status().isUnauthorized())
        .andDo(document("user-signin-error4"));
  }

  @Test
  void 가입된_사용자_회원탈퇴_성공() throws Exception {
    mockMvc.perform(delete("/v1/api/users")
            .with(user(getUserPrincipal()))
            .header(AUTHORIZATION, JWT_TOKEN))
        .andExpect(status().isOk())
        .andDo(document("delete-user",
            requestHeaders(
                headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
            )));

    verify(userService).deleteUser(any());
  }

  @Test
  void 사용자_닉네임과_프로필이미지_변경성공() throws Exception {
    ChangeUserNicknameRequest changeUserNicknameRequest = new ChangeUserNicknameRequest(
        "newNickname");
    MockMultipartFile userProfileImage = new MockMultipartFile("userProfileImage",
        "test".getBytes());

    mockMvc.perform(multipart("/v1/api/users/profile")
            .file(userProfileImage)
            .file(new MockMultipartFile("changeUserNicknameRequest", "", APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(changeUserNicknameRequest)
                    .getBytes(UTF_8)))
            .header(AUTHORIZATION, JWT_TOKEN)
            .with(user(getUserPrincipal())))
        .andExpect(status().isOk())
        .andDo(document("post-update-user-profile",
            requestHeaders(
                headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
            ),
            requestParts(
                partWithName("changeUserNicknameRequest").description("변경할 닉네임"),
                partWithName("userProfileImage").optional()
                    .description("프로필 이미지 [200 x 200].webp")
            ),
            requestPartFields("changeUserNicknameRequest",
                fieldWithPath("nickname").type(STRING).description("변경할 닉네임")
            )));

    verify(userService).updateUserProfile(any(), any(), any());
  }

  @Test
  void 로그아웃_요청_성공() throws Exception {
    mockMvc.perform(post("/v1/api/users/logout")
            .header(AUTHORIZATION, JWT_TOKEN)
            .with(user(getUserPrincipal())))
        .andExpect(status().isOk())
        .andDo(document("post-logout",
            requestHeaders(
                headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
            )));
  }

  @Test
  void 회원정보_조회_요청_성공() throws Exception {
    MemberProfileResponse response = MemberProfileResponse.builder()
        .userId(1L)
        .userNickname("test")
        .userEmail("nkwksn1sse")
        .userProfileImageUri("test")
        .defaultProfileImageType(1)
        .build();

    given(userService.getMemberProfile(any())).willReturn(response);

    mockMvc.perform(get("/v1/api/members")
            .param("memberId", "1")
            .header(AUTHORIZATION, JWT_TOKEN)
            .with(user(getUserPrincipal())))
        .andExpect(status().isOk())
        .andDo(document("get-member-profile",
            requestHeaders(
                headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
            ),
            requestParameters(
                parameterWithName("memberId").description("회원 ID")
            ),
            responseFields(
                fieldWithPath("userId").type(NUMBER).description("사용자 ID"),
                fieldWithPath("userNickname").type(STRING).description("닉네임"),
                fieldWithPath("userEmail").type(STRING).description("이메일"),
                fieldWithPath("userProfileImageUri").type(STRING).description("프로필 사진 URI"),
                fieldWithPath("defaultProfileImageType").type(NUMBER).description("기본 이미지 타입")
            )
        ));
  }
}
