package toy.bookchat.bookchat.security.token;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static toy.bookchat.bookchat.domain.user.ROLE.USER;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import toy.bookchat.bookchat.domain.AuthenticationTestExtension;
import toy.bookchat.bookchat.domain.user.ReadingTaste;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.api.dto.Token;
import toy.bookchat.bookchat.exception.security.DenidedTokenException;
import toy.bookchat.bookchat.exception.security.ExpiredTokenException;
import toy.bookchat.bookchat.security.SecurityConfig;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.token.dto.RefreshTokenRequestDto;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenProvider;

@WebMvcTest(controllers = TokenController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        SecurityConfig.class}))
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "bookchat.link", uriPort = 443)
class TokenControllerTest extends AuthenticationTestExtension {

    @MockBean
    TokenService tokenService;
    @SpyBean
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    private User getUser() {
        return User.builder()
            .id(1L)
            .email("test@gmail.com")
            .nickname("nickname")
            .role(USER)
            .name("testGOOGLE")
            .profileImageUrl("test@gamil.com")
            .defaultProfileImageType(1)
            .provider(OAuth2Provider.GOOGLE)
            .readingTastes(List.of(ReadingTaste.DEVELOPMENT, ReadingTaste.ART))
            .build();
    }

    @Test
    void Access토큰_만료시_만료되지_않은_리프레시_토큰으로_갱신() throws Exception {
        Token token = jwtTokenProvider.createToken(getUser());

        RefreshTokenRequestDto refreshTokenRequestDto = RefreshTokenRequestDto.builder()
            .refreshToken(token.getRefreshToken())
            .build();

        Token newToken = jwtTokenProvider.createToken(getUser());

        when(tokenService.generateToken(any())).thenReturn(newToken);

        mockMvc.perform(post("/v1/api/auth/token")
                .content(objectMapper.writeValueAsString(refreshTokenRequestDto))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("token-reissue", requestFields(
                fieldWithPath("refreshToken").type(STRING).description("리프레쉬 토큰")
            ), responseFields(
                fieldWithPath("accessToken").type(STRING).description("엑세스 토큰"),
                fieldWithPath("refreshToken").type(STRING).description("리프레쉬 토큰")
            )));
    }

    @Test
    void 리프레시_토큰없이_요청시_400응답() throws Exception {
        RefreshTokenRequestDto refreshTokenRequestDto = RefreshTokenRequestDto.builder()
            .refreshToken(null)
            .build();

        mockMvc.perform(post("/v1/api/auth/token").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenRequestDto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void 만료된_리프레시_토큰으로_요청시_401응답() throws Exception {
        Map<String, Object> claims = new HashMap<>();

        Date date = new Date(0);
        String expiredRefreshToken = Jwts.builder()
            .setClaims(claims)
            .setExpiration(date)
            .signWith(SignatureAlgorithm.HS256, "test")
            .compact();

        RefreshTokenRequestDto refreshTokenRequestDto = RefreshTokenRequestDto.builder()
            .refreshToken(expiredRefreshToken)
            .build();

        doThrow(ExpiredTokenException.class).when(tokenService)
            .generateToken(any());

        mockMvc.perform(post("/v1/api/auth/token").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenRequestDto)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void 유효하지않은_토큰으로_요청시_401응답() throws Exception {
        Token token = jwtTokenProvider.createToken(getUser());

        RefreshTokenRequestDto refreshTokenRequestDto = RefreshTokenRequestDto.builder()
            .refreshToken(token.getRefreshToken() + "invalid")
            .build();

        doThrow(DenidedTokenException.class).when(tokenService)
            .generateToken(any());

        mockMvc.perform(post("/v1/api/auth/token").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenRequestDto)))
            .andExpect(status().isUnauthorized());
    }
}