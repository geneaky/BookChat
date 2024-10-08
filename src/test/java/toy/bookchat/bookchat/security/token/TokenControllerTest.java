package toy.bookchat.bookchat.security.token;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
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
import toy.bookchat.bookchat.domain.ControllerTestExtension;
import toy.bookchat.bookchat.domain.user.api.v1.response.Token;
import toy.bookchat.bookchat.exception.unauthorized.DeniedTokenException;
import toy.bookchat.bookchat.exception.unauthorized.ExpiredTokenException;
import toy.bookchat.bookchat.security.SecurityConfig;
import toy.bookchat.bookchat.security.token.dto.RefreshTokenRequest;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenProvider;

@WebMvcTest(controllers = TokenController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        SecurityConfig.class}))
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "bookchat.link", uriPort = 443)
class TokenControllerTest extends ControllerTestExtension {

  @MockBean
  TokenService tokenService;
  @SpyBean
  JwtTokenProvider jwtTokenProvider;
  @Autowired
  ObjectMapper objectMapper;
  @Autowired
  private MockMvc mockMvc;

  @Test
  void Access토큰_만료시_만료되지_않은_리프레시_토큰으로_갱신() throws Exception {
    Token token = jwtTokenProvider.createToken(getUser());

    RefreshTokenRequest refreshTokenRequest = RefreshTokenRequest.builder()
        .refreshToken(token.getRefreshToken())
        .build();

    Token newToken = jwtTokenProvider.createToken(getUser());

    when(tokenService.generateToken(any())).thenReturn(newToken);

    mockMvc.perform(post("/v1/api/auth/token")
            .content(objectMapper.writeValueAsString(refreshTokenRequest))
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
    RefreshTokenRequest refreshTokenRequest = RefreshTokenRequest.builder()
        .refreshToken(null)
        .build();

    mockMvc.perform(post("/v1/api/auth/token").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(refreshTokenRequest)))
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

    RefreshTokenRequest refreshTokenRequest = RefreshTokenRequest.builder()
        .refreshToken(expiredRefreshToken)
        .build();

    when(tokenService.generateToken(any())).thenThrow(new ExpiredTokenException());

    mockMvc.perform(post("/v1/api/auth/token").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(refreshTokenRequest)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void 유효하지않은_토큰으로_요청시_401응답() throws Exception {
    Token token = jwtTokenProvider.createToken(getUser());

    RefreshTokenRequest refreshTokenRequest = RefreshTokenRequest.builder()
        .refreshToken(token.getRefreshToken() + "invalid")
        .build();

    when(tokenService.generateToken(any())).thenThrow(new DeniedTokenException());

    mockMvc.perform(post("/v1/api/auth/token").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(refreshTokenRequest)))
        .andExpect(status().isUnauthorized());
  }
}