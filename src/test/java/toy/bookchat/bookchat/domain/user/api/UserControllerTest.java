package toy.bookchat.bookchat.domain.user.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;
import toy.bookchat.bookchat.config.OpenIdTokenConfig;
import toy.bookchat.bookchat.domain.AuthenticationTestExtension;
import toy.bookchat.bookchat.domain.user.ROLE;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.api.dto.Token;
import toy.bookchat.bookchat.domain.user.api.dto.UserProfileResponse;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.domain.user.service.UserService;
import toy.bookchat.bookchat.domain.user.service.dto.UserSignInRequestDto;
import toy.bookchat.bookchat.domain.user.service.dto.UserSignUpRequestDto;
import toy.bookchat.bookchat.security.SecurityConfig;
import toy.bookchat.bookchat.security.exception.ExpiredTokenException;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenManager;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenProvider;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenRecorder;
import toy.bookchat.bookchat.security.token.openid.OpenIdTestUtil;
import toy.bookchat.bookchat.security.token.openid.OpenIdTokenManager;
import toy.bookchat.bookchat.security.user.UserPrincipal;

@WebMvcTest(controllers = UserController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        SecurityConfig.class}))
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "bookchat.link", uriPort = 443)
class UserControllerTest extends AuthenticationTestExtension {

    @MockBean
    UserService userService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    OpenIdTokenManager openIdTokenManager;

    @MockBean
    JwtTokenManager jwtTokenManager;

    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @MockBean
    JwtTokenRecorder jwtTokenRecorder;

    @MockBean
    OpenIdTokenConfig openIdTokenConfig;
    @Autowired
    ObjectMapper objectMapper;
    OpenIdTestUtil openIdTestUtil;
    @Autowired
    private MockMvc mockMvc;

    private static String getTestToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "test");
        claims.put("name", "testkakao");
        claims.put("provider", OAuth2Provider.GOOGLE);
        claims.put("email", "test@gmail.com");

        String testToken = Jwts.builder()
            .setClaims(claims)
            .signWith(SignatureAlgorithm.HS256, "test")
            .compact();
        return testToken;
    }

    @BeforeEach
    public void init() throws FileNotFoundException {
        openIdTestUtil = new OpenIdTestUtil(
            "src/test/java/toy/bookchat/bookchat/security/token/openid/token_key.pem",
            "src/test/java/toy/bookchat/bookchat/security/token/openid/openidRSA256-public.pem");
    }

    private UserPrincipal getUserPrincipal() {
        List<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_USER")
        );
        User user = User.builder()
            .email("test@gmail.com")
            .name("testkakao")
            .profileImageUrl("somethingImageUrl@naver.com")
            .build();

        return new UserPrincipal(1L, user.getEmail(),
            user.getName(), user.getNickname(), user.getProfileImageUrl(),
            user.getDefaultProfileImageType(), authorities, user);
    }

    @Test
    void 인증받지_않은_사용자_요청_401응답() throws Exception {
        mockMvc.perform(get("/v1/api/users/profile"))
            .andExpect(status().isUnauthorized())
            .andDo(document("user-profile-error"));
    }

    @Test
    void 사용자_프로필_정보_반환() throws Exception {

        String testToken = getTestToken();

        String real = objectMapper.writeValueAsString(UserProfileResponse.builder()
            .userEmail("test@gmail.com")
            .userNickname("nickname")
            .userProfileImageUri("somethingImageUrl@naver.com")
            .defaultProfileImageType(1)
            .build());

        User user = User.builder()
            .email("test@gmail.com")
            .name("testkakao")
            .nickname("nickname")
            .role(ROLE.USER)
            .profileImageUrl("somethingImageUrl@naver.com")
            .defaultProfileImageType(1)
            .build();

        when(userRepository.findByName(any())).thenReturn(Optional.of(user));

        MvcResult mvcResult = mockMvc.perform(get("/v1/api/users/profile")
                .with(user(getUserPrincipal()))
                .header("Authorization", "Bearer " + testToken))
            .andExpect(status().isOk())
            .andDo(document("user",
                requestHeaders(
                    headerWithName("Authorization").description("Bearer [JWT token]")
                ),
                responseFields(
                    fieldWithPath("userNickname").type(STRING).description("닉네임"),
                    fieldWithPath("userEmail").type(STRING).description("이메일"),
                    fieldWithPath("userProfileImageUri").type(STRING).description("프로필 사진 URI"),
                    fieldWithPath("defaultProfileImageType").type(NUMBER).description("기본 이미지 타입")
                )))
            .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo(real);
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
                .header("Authorization", " ")
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
                .header("Authorization", "Bearer ")
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
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
        return publicKey;
    }

    private PrivateKey getPrivateKey()
        throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec privateKeySpec = getPrivatePkcs8EncodedKeySpec(openIdTestUtil);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        return privateKey;
    }

    @Test
    void 사용자_회원가입_요청시_header_openid가_유효하지않은_경우_401반환() throws Exception {
        PrivateKey privateKey = getPrivateKey();
        PublicKey publicKey = getPublicKey();
        MockMultipartFile multipartFile = new MockMultipartFile("userProfileImage",
            "testImage".getBytes());

        UserSignUpRequestDto requestDto = UserSignUpRequestDto.builder()
            .nickname("nick")
            .defaultProfileImageType(1)
            .oauth2Provider(OAuth2Provider.KAKAO)
            .readingTastes(List.of(DEVELOPMENT, ART, SCIENCE))
            .build();

        Claims claims = Jwts.claims().setIssuer("https://kauth.kakao.com")
            .setSubject("test").setExpiration(new Date(0));
        String testToken = Jwts.builder()
            .setHeaderParam("kid", "abcdefg")
            .setClaims(claims)
            .signWith(SignatureAlgorithm.RS256, privateKey).compact();

        when(openIdTokenConfig.getPublicKey(any(), any())).thenReturn(publicKey);
        doThrow(ExpiredTokenException.class).when(openIdTokenManager)
            .getOAuth2MemberNumberFromToken(any(), any());

        mockMvc.perform(multipart("/v1/api/users/signup")
                .file(multipartFile)
                .file(new MockMultipartFile("userSignUpRequestDto", "", "application/json",
                    objectMapper.writeValueAsString(requestDto)
                        .getBytes(StandardCharsets.UTF_8)))
                .header("OIDC", "Bearer " + testToken))
            .andExpect(status().isUnauthorized())
            .andDo(document("user-signup-error4"));
    }

    @Test
    void 사용자_회원가입_요청시_header_openid가_유효한경우_회원가입진행() throws Exception {
        PrivateKey privateKey = getPrivateKey();
        PublicKey publicKey = getPublicKey();

        when(openIdTokenConfig.getPublicKey(any(), any())).thenReturn(publicKey);
        when(openIdTokenManager.getOAuth2MemberNumberFromToken(any(), any())).thenReturn(
            "testkakao");
        when(openIdTokenManager.getUserEmailFromToken(any(), any())).thenReturn("test@gmail.com");

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", "test@gmail.com");
        claims.put("iss", "https://kauth.kakao.com");
        claims.put("sub", "test");

        String testToken = Jwts.builder()
            .setHeaderParam("kid", "abcedf")
            .setClaims(claims)
            .signWith(SignatureAlgorithm.RS256, privateKey)
            .compact();

        MockMultipartFile multipartFile = new MockMultipartFile("userProfileImage",
            "test".getBytes());
        UserSignUpRequestDto userSignUpRequestDto = UserSignUpRequestDto.builder()
            .nickname("nick")
            .defaultProfileImageType(1)
            .oauth2Provider(OAuth2Provider.KAKAO)
            .readingTastes(List.of(DEVELOPMENT, ART, SCIENCE))
            .build();

        mockMvc.perform(multipart("/v1/api/users/signup")
                .file(multipartFile)
                .file(new MockMultipartFile("userSignUpRequestDto", "", "application/json",
                    objectMapper.writeValueAsString(userSignUpRequestDto)
                        .getBytes(StandardCharsets.UTF_8)))
                .header("OIDC", "Bearer " + testToken))
            .andExpect(status().isOk())
            .andDo(document("user-signup", requestHeaders(
                    headerWithName("OIDC").description("Bearer [openid token]")
                ),
                requestParts(
                    partWithName("userProfileImage").description("프로필 이미지 [300 x 300].webp"),
                    partWithName("userSignUpRequestDto").description("회원가입 입력 폼")
                ),
                requestPartFields("userSignUpRequestDto",
                    fieldWithPath("nickname").description("닉네임"),
                    fieldWithPath("defaultProfileImageType").optional().description("기본 이미지 타입"),
                    fieldWithPath("readingTastes").optional().description("독서 취향"),
                    fieldWithPath("oauth2Provider").description("프로바이더 타입[kakao/google]")
                )));

        verify(userService).registerNewUser(any(UserSignUpRequestDto.class),
            any(MultipartFile.class),
            anyString(),
            anyString());
    }

    @Test
    void 토큰형식_정규표현식_확인() throws Exception {
        PrivateKey privateKey = getPrivateKey();

        String testToken = Jwts.builder().setSubject("test")
            .signWith(SignatureAlgorithm.RS256, privateKey).compact();

        String A = "Tearer " + testToken;

        String B = "Bearer " + testToken;

        assertThat(A.matches("^(Bearer)\\s.+")).isFalse();
        assertThat(B.matches("^(Bearer)\\s.+")).isTrue();

    }

    @Test
    void 사용자_회원가입_요청시_올바르지않은_토큰_요청규격_예외처리() throws Exception {
        PrivateKey privateKey = getPrivateKey();

        String testToken = Jwts.builder().setSubject("test")
            .signWith(SignatureAlgorithm.RS256, privateKey).compact();

        mockMvc.perform(post("/v1/api/users/signup")
                .header("Authorization", "Tearer" + testToken)
                .param("defaultProfileImageType", "1")
                .param("nickname", "testName"))
            .andExpect(status().isBadRequest())
            .andDo(document("user-signup-error3"));
    }

    @Test
    void 사용자_회원가입_요청시_올바르지않은_파라미터형식_예외처리() throws Exception {
        PrivateKey privateKey = getPrivateKey();

        String testToken = Jwts.builder().setSubject("test")
            .signWith(SignatureAlgorithm.RS256, privateKey).compact();

        mockMvc.perform(post("/v1/api/users/signup")
                .header("Authorization", "Bearer " + testToken)
                .param("nickname", "")
                .param("userEmail", "abcdefg")
                .param("oauth2Provider", ""))
            .andExpect(status().isBadRequest())
            .andDo(document("user-signup-error5"));
    }

    @Test
    void 사용자_성공적으로_로그인시_응답_header에_jwt_token삽입() throws Exception {
        PrivateKey privateKey = getPrivateKey();
        PublicKey publicKey = getPublicKey();

        when(openIdTokenConfig.getPublicKey(any(), any())).thenReturn(publicKey);

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", "test@gmail.com");
        claims.put("iss", "https://kauth.kakao.com");
        claims.put("sub", "test");

        String testToken = Jwts.builder()
            .setHeaderParam("kid", "abcedf")
            .setClaims(claims)
            .signWith(SignatureAlgorithm.RS256, privateKey)
            .compact();

        Token responseToken = Token.builder()
            .accessToken("accessToken")
            .refreshToken("refreshToken")
            .build();

        UserSignInRequestDto userSignInRequestDto = UserSignInRequestDto.builder()
            .oauth2Provider(OAuth2Provider.KAKAO)
            .build();

        when(jwtTokenProvider.createToken(any(), any(), any())).thenReturn(responseToken);
        MvcResult mvcResult = mockMvc.perform(post("/v1/api/users/signin")
                .header("OIDC", "Bearer " + testToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userSignInRequestDto)))
            .andExpect(status().isOk())
            .andDo(document("user-signin",
                requestHeaders(
                    headerWithName("OIDC").description("Bearer [openid token]")
                ),
                requestFields(
                    fieldWithPath("oauth2Provider").type(STRING)
                        .description("프로바이더 타입[kakao/google]")
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
    void 로그인_요청시_Header없으면_예외발생() throws Exception {

        mockMvc.perform(post("/v1/api/users/signin"))
            .andExpect(status().isBadRequest())
            .andDo(document("user-signin-error1"));
    }

    @Test
    void 로그인_요청시_Header에_토큰이_없으면_예외발생() throws Exception {
        mockMvc.perform(post("/v1/api/users/signin")
                .header("OIDC", "Bearer "))
            .andExpect(status().isBadRequest())
            .andDo(document("user-signin-error2"));
    }

    @Test
    void 로그인_요청시_Header인증정보가_Bearer양식에_맞지않으면_예외발생() throws Exception {
        PrivateKey privateKey = getPrivateKey();
        PublicKey publicKey = getPublicKey();

        when(openIdTokenConfig.getPublicKey(any(), any())).thenReturn(publicKey);

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", "test@gmail.com");
        claims.put("iss", "https://kauth.kakao.com");
        claims.put("sub", "test");

        String testToken = Jwts.builder()
            .setHeaderParam("kid", "abcedf")
            .setClaims(claims)
            .signWith(SignatureAlgorithm.RS256, privateKey)
            .compact();

        mockMvc.perform(post("/v1/api/users/signin")
                .header("OIDC", "Tearer " + testToken))
            .andExpect(status().isBadRequest())
            .andDo(document("user-signin-error3"));
    }

    @Test
    void 만료된_토큰으로_요청시_401_예외발생() throws Exception {
        PrivateKey privateKey = getPrivateKey();
        PublicKey publicKey = getPublicKey();

        when(openIdTokenConfig.getPublicKey(any(), any())).thenReturn(publicKey);

        Claims claims = Jwts.claims().setIssuer("https://kauth.kakao.com")
            .setSubject("test").setExpiration(new Date(0));
        String testToken = Jwts.builder()
            .setHeaderParam("kid", "abcdefg")
            .setClaims(claims)
            .signWith(SignatureAlgorithm.RS256, privateKey).compact();

        mockMvc.perform(post("/v1/api/users/siginin")
                .header("OIDC", "Bearer " + testToken))
            .andExpect(status().isUnauthorized())
            .andDo(document("user-signin-error4"));
    }

    @Test
    void 가입된_사용자_회원탈퇴_성공() throws Exception {
        String testToken = getTestToken();

        User user = User.builder()
            .email("test@gmail.com")
            .name("testkakao")
            .nickname("nickname")
            .role(ROLE.USER)
            .profileImageUrl("somethingImageUrl@naver.com")
            .defaultProfileImageType(1)
            .build();

        when(userRepository.findByName(any())).thenReturn(Optional.of(user));

        mockMvc.perform(delete("/v1/api/users")
                .with(user(getUserPrincipal()))
                .header("Authorization", "Bearer " + testToken))
            .andExpect(status().isOk())
            .andDo(document("delete-user",
                requestHeaders(
                    headerWithName("Authorization").description("Bearer [JWT token]")
                )));

        verify(userService).deleteUser(any());
    }
}
