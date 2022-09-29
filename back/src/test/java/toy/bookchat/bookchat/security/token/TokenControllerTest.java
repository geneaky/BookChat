package toy.bookchat.bookchat.security.token;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;
import toy.bookchat.bookchat.domain.AuthenticationTestExtension;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.security.SecurityConfig;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenManager;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TokenController.class,
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                SecurityConfig.class}))
@AutoConfigureRestDocs
class TokenControllerTest extends AuthenticationTestExtension {

    @MockBean(value = JwtTokenManager.class)
    TokenManager jwtTokenManager;
    @MockBean
    UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void Access토큰_만료시_만료되지_않은_리프레시_토큰으로_갱신() throws Exception {

        mockMvc.perform(post("/v1/api/auth/token")
                        .content("refresh token"))
                .andExpect(status().isCreated());

//        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo()
    }
}