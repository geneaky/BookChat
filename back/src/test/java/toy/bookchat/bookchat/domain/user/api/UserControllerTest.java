package toy.bookchat.bookchat.domain.user.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.Base64Utils;
import toy.bookchat.bookchat.config.OpenIdTokenConfig;
import toy.bookchat.bookchat.domain.AuthenticationTestExtension;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.api.dto.UserProfileResponse;
import toy.bookchat.bookchat.domain.user.service.UserService;
import toy.bookchat.bookchat.domain.user.service.dto.UserSignUpRequestDto;
import toy.bookchat.bookchat.security.SecurityConfig;
import toy.bookchat.bookchat.security.openid.OpenIdTestUtil;
import toy.bookchat.bookchat.security.openid.OpenIdTokenManager;
import toy.bookchat.bookchat.security.user.UserPrincipal;

@WebMvcTest(controllers = UserController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class}))
@AutoConfigureRestDocs
public class UserControllerTest extends AuthenticationTestExtension {

    @MockBean
    UserService userService;

    @SpyBean
    OpenIdTokenManager openIdTokenManager;

    @MockBean
    OpenIdTokenConfig openIdTokenConfig;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    OpenIdTestUtil openIdTestUtil;

    @BeforeEach
    public void init() throws FileNotFoundException {
        openIdTestUtil = new OpenIdTestUtil(
                "src/test/java/toy/bookchat/bookchat/security/openid/token_key.pem",
                "src/test/java/toy/bookchat/bookchat/security/openid/openidRSA256-public.pem");
    }

    private UserPrincipal getUserPrincipal() {
        List<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_USER")
        );
        User user = User.builder()
            .email("test@gmail.com")
            .name("testUser")
            .profileImageUrl("somethingImageUrl@naver.com")
            .build();

        return new UserPrincipal(1L, user.getEmail(),
            user.getName(), user.getProfileImageUrl(), authorities, user);
    }

    @Test
    public void 인증받지_않은_사용자_요청_401응답() throws Exception {
        mockMvc.perform(get("/v1/api/users/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void 사용자_프로필_정보_반환() throws Exception {
        String real = objectMapper.writeValueAsString(UserProfileResponse.builder()
            .userEmail("test@gmail.com")
            .userName("testUser")
            .userProfileImageUri("somethingImageUrl@naver.com")
            .build());

        MvcResult mvcResult = mockMvc.perform(get("/v1/api/users/profile")
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(document("user"))
            .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo(real);
    }

    @Test
    public void 사용자_닉네임_중복_아닐시_200반환() throws Exception {
        when(userService.isDuplicatedName(anyString())).thenReturn(false);
        mockMvc.perform(get("/v1/api/users/profile/nickname").param("nickname", "HiBs"))
            .andExpect(status().isOk())
            .andDo(document("user_nickname", requestParameters(
                parameterWithName("nickname").description("사용자 nickname")
            )));
    }

    @Test
    public void 사용자_닉네임_중복시_409반환() throws Exception {
        when(userService.isDuplicatedName(anyString())).thenReturn(true);
        mockMvc.perform(get("/v1/api/users/profile/nickname").param("nickname", "HiBs"))
            .andExpect(status().isConflict());
    }

    @Test
    public void 사용자_회원가입_요청시_header_인증정보_없을시_400반환() throws Exception {
        mockMvc.perform(post("/v1/api/users")
                .param("nickname", "nick")
                .param("userEmail", "kaktus418@gmail.com")
                .param("oauth2Provider", "kakao")
                .param("defaultProfileImageType", "2"))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void 사용자_회원가입_요청시_header_유효하지않은_인증정보_400반환() throws Exception {
        mockMvc.perform(post("/v1/api/users")
                .header("Authorization", " ")
                .param("nickname", "nick")
                .param("userEmail", "kaktus418@gmail.com")
                .param("oauth2Provider", "kakao")
                .param("defaultProfileImageType", "2"))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void 사용자_회원가입_요청시_header_openid없는_인증정보_400반환() throws Exception {
        mockMvc.perform(post("/v1/api/users")
                .header("Authorization", "Bearer ")
                .param("nickname", "nick")
                .param("userEmail", "kaktus418@gmail.com")
                .param("oauth2Provider", "google")
                .param("defaultProfileImageType", "2"))
            .andExpect(status().isBadRequest());
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

    private PublicKey getPublicKey() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec = getPublicPkcs8EncodedKeySpec(openIdTestUtil);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
        return publicKey;
    }

    private PrivateKey getPrivateKey() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec privateKeySpec = getPrivatePkcs8EncodedKeySpec(openIdTestUtil);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        return privateKey;
    }

    @Test
    public void 사용자_회원가입_요청시_header_openid가_유효하지않은_경우_412반환() throws Exception {
        PrivateKey privateKey = getPrivateKey();
        PublicKey publicKey = getPublicKey();

        when(openIdTokenConfig.getPublicKey(any(),any())).thenReturn(publicKey);

        String testToken = Jwts.builder()
                .setSubject("test")
                .setExpiration(new Date(0))
                .signWith(SignatureAlgorithm.RS256, privateKey).compact();

        mockMvc.perform(post("/v1/api/users")
                .header("Authorization", "Bearer " + testToken)
                .param("nickname", "nick")
                .param("userEmail", "kaktus418@gmail.com")
                .param("oauth2Provider", "kakao")
                .param("defaultProfileImageType", "2"))
            .andExpect(status().isPreconditionFailed());
    }

    @Test
    public void 사용자_회원가입_요청시_header_openid가_유효한경우_회원가입진행() throws Exception {
        PrivateKey privateKey = getPrivateKey();
        PublicKey publicKey = getPublicKey();

        when(openIdTokenConfig.getPublicKey(any(),any())).thenReturn(publicKey);

        String testToken = Jwts.builder()
            .setSubject("test")
            .setHeaderParam("kid","abcedf")
            .setIssuer(" https://kauth.kakao.com")
            .signWith(SignatureAlgorithm.RS256, privateKey)
            .compact();

        mockMvc.perform(post("/v1/api/users")
                .header("Authorization", "Bearer " + testToken)
                .param("nickname", "nick")
                .param("userEmail", "kaktus418@gmail.com")
                .param("oauth2Provider", "google")
                .param("defaultProfileImageType", "1"))
            .andExpect(status().isOk())
            .andDo(document("user_sign_up", requestParameters(
                parameterWithName("nickname").description("닉네임"),
                parameterWithName("userEmail").description("이메일"),
                parameterWithName("oauth2Provider").description("원천 제공자"),
                parameterWithName("defaultProfileImageType").description("기본 이미지 타입"),
                parameterWithName("userProfileImage").optional().description("프로필 이미지"),
                parameterWithName("readingTastes").optional().description("독서 취향")
            )));

        verify(userService).registerNewUser(any(UserSignUpRequestDto.class), anyString());
    }

    @Test
    public void 사용자_회원가입_요청시_올바르지않은_파라미터형식_예외처리() throws Exception {
        PrivateKey privateKey = getPrivateKey();

        String testToken = Jwts.builder().setSubject("test")
            .signWith(SignatureAlgorithm.RS256, privateKey).compact();

        mockMvc.perform(post("/v1/api/users")
                .header("Authorization", "Bearer " + testToken)
                .param("nickname", "")
                .param("userEmail", "abcdefg")
                .param("oauth2Provider", ""))
            .andExpect(status().isBadRequest());
    }
}
