package toy.bookchat.bookchat.domain.user.api;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import toy.bookchat.bookchat.domain.user.dto.UserProfileResponse;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.security.handler.CustomAuthenticationFailureHandler;
import toy.bookchat.bookchat.security.handler.CustomAuthenticationSuccessHandler;
import toy.bookchat.bookchat.security.handler.RestAuthenticationEntryPoint;
import toy.bookchat.bookchat.security.jwt.JwtAuthenticationFilter;
import toy.bookchat.bookchat.security.jwt.JwtTokenProvider;
import toy.bookchat.bookchat.security.oauth.CustomOAuth2UserService;
import toy.bookchat.bookchat.security.oauth.HttpCookieOAuth2AuthorizationRequestRepository;
import toy.bookchat.bookchat.security.user.UserPrincipal;

@WebMvcTest(controllers = UserController.class,
    includeFilters = @ComponentScan.Filter(classes = {EnableWebSecurity.class}))
@Import({JwtAuthenticationFilter.class, RestAuthenticationEntryPoint.class})
@AutoConfigureRestDocs
public class UserControllerTest {

    @MockBean
    CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    @MockBean
    CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    @MockBean
    CustomOAuth2UserService customOAuth2UserService;
    @MockBean
    JwtTokenProvider jwtTokenProvider;
    @MockBean
    UserRepository userRepository;
    @MockBean
    HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private UserPrincipal getUserPrincipal() {
        List<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_USER")
        );
        UserPrincipal userPrincipal = new UserPrincipal(1L, "test@gmail.com", "password",
            "testUser", "somethingImageUrl.com", authorities);

        return userPrincipal;
    }

    @Test
    public void 인증받지_않은_사용자_요청_401응답() throws Exception {
        mockMvc.perform(get("/v1/api/users/profile"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void 인증받은_사용자의_요청_200응답() throws Exception {
        mockMvc.perform(get("/v1/api/users/profile")
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk());
    }

    @Test
    public void 사용자_프로필_정보_반환() throws Exception {
        String real = objectMapper.writeValueAsString(UserProfileResponse.builder()
            .userEmail("test@gmail.com")
            .userName("testUser")
            .userProfileImageUri("somethingImageUrl.com")
            .build());

        MvcResult mvcResult = mockMvc.perform(get("/v1/api/users/profile")
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andReturn();

        Assertions.assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo(real);
    }


}
