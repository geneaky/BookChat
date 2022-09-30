package toy.bookchat.bookchat.security.token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import toy.bookchat.bookchat.domain.AuthenticationTestExtension;
import toy.bookchat.bookchat.domain.user.api.dto.Token;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.security.SecurityConfig;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.token.dto.RefreshTokenRequestDto;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenManager;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenProvider;

@WebMvcTest(controllers = TokenController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        SecurityConfig.class}))
@AutoConfigureRestDocs
class TokenControllerTest extends AuthenticationTestExtension {

    @MockBean
    JwtTokenManager jwtTokenManager;
    @MockBean
    UserRepository userRepository;
    @SpyBean
    TokenService tokenService;
    @SpyBean
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void Access토큰_만료시_만료되지_않은_리프레시_토큰으로_갱신() throws Exception {
        Token token = jwtTokenProvider.createToken("testGoogle", "test@gamil.com",
            OAuth2Provider.GOOGLE);

        RefreshTokenRequestDto refreshTokenRequestDto = RefreshTokenRequestDto.builder()
                .refreshToken(token.getRefreshToken())
                .build();

        when(tokenService.generateToken(any(RefreshTokenRequestDto.class))).thenReturn(token);

        MvcResult mvcResult = mockMvc.perform(post("/v1/api/auth/token")
                .content(objectMapper.writeValueAsString(refreshTokenRequestDto))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        String accessToken = mvcResult.getResponse().getContentAsString();
        String userName = jwtTokenManager.getOAuth2MemberNumberFromToken(
            accessToken);

        assertThat(userName).isEqualTo("testGoogle");
    }
}