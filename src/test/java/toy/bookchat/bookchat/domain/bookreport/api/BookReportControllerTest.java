package toy.bookchat.bookchat.domain.bookreport.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static toy.bookchat.bookchat.domain.user.ROLE.USER;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import toy.bookchat.bookchat.domain.AuthenticationTestExtension;
import toy.bookchat.bookchat.domain.bookreport.service.BookReportService;
import toy.bookchat.bookchat.domain.bookreport.service.dto.request.WriteBookReportRequestDto;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.security.SecurityConfig;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.user.UserPrincipal;

@WebMvcTest(controllers = BookReportController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        SecurityConfig.class}))
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "bookchat.link", uriPort = 443)
class BookReportControllerTest extends AuthenticationTestExtension {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookReportService bookReportService;

    private User getUser() {
        return User.builder()
            .email("test@gmail.com")
            .role(USER)
            .name("testUser")
            .profileImageUrl("somethingImageUrl@naver.com")
            .build();
    }

    private UserPrincipal getUserPrincipal() {
        List<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_USER")
        );
        User user = getUser();

        return new UserPrincipal(1L, user.getEmail(), user.getName(), user.getNickname(),
            user.getProfileImageUrl(),
            user.getDefaultProfileImageType(), authorities, user);
    }

    private String getTestToken() throws Exception {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "test");
        claims.put("name", "google123");
        claims.put("provider", OAuth2Provider.GOOGLE);
        claims.put("email", "test@gmail.com");

        String testToken = Jwts.builder()
            .setClaims(claims)
            .signWith(SignatureAlgorithm.HS256, "test")
            .compact();

        return testToken;
    }

    @Test
    void 다_읽은_책_독후감_작성_성공() throws Exception {

        WriteBookReportRequestDto writeBookReportRequestDto = WriteBookReportRequestDto.builder()
            .bookShelfId(1L)
            .title("어렵지만 많이 배웠다")
            .content("요런 요런 내용, 저런저런 내용을 많이 배움")
            .build();

        mockMvc.perform(post("/v1/api/bookreports")
                .header("Authorization", "Bearer " + getTestToken())
                .with(user(getUserPrincipal()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(writeBookReportRequestDto)))
            .andExpect(status().isOk())
            .andDo(document("post-book-report",
                requestHeaders(
                    headerWithName("Authorization").description("Bearer [JWT token]")
                ),
                requestFields(
                    fieldWithPath("bookShelfId").type(NUMBER).description("Book Shelf Id"),
                    fieldWithPath("title").type(STRING).description("독후감 제목"),
                    fieldWithPath("content").type(STRING).description("독후감 내용")
                )));

        verify(bookReportService).writeReport(any(), any());
    }
}