package toy.bookchat.bookchat.domain.bookreport.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static toy.bookchat.bookchat.domain.user.ROLE.USER;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.LocalDateTime;
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
import org.springframework.test.web.servlet.MockMvc;
import toy.bookchat.bookchat.domain.AuthenticationTestExtension;
import toy.bookchat.bookchat.domain.bookreport.BookReport;
import toy.bookchat.bookchat.domain.bookreport.service.BookReportService;
import toy.bookchat.bookchat.domain.bookreport.service.dto.request.ReviseBookReportRequest;
import toy.bookchat.bookchat.domain.bookreport.service.dto.request.WriteBookReportRequest;
import toy.bookchat.bookchat.domain.bookreport.service.dto.response.BookReportResponse;
import toy.bookchat.bookchat.domain.user.ReadingTaste;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.security.SecurityConfig;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.user.TokenPayload;
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
            .id(1L)
            .email("test@gmail.com")
            .nickname("nickname")
            .role(USER)
            .name("testUser")
            .profileImageUrl("somethingImageUrl@naver.com")
            .defaultProfileImageType(1)
            .provider(OAuth2Provider.KAKAO)
            .readingTastes(List.of(ReadingTaste.DEVELOPMENT, ReadingTaste.ART))
            .build();
    }

    private UserPrincipal getUserPrincipal() {
        User user = getUser();
        TokenPayload tokenPayload = TokenPayload.of(user.getId(), user.getName(),
            user.getNickname(),
            user.getEmail(), user.getProfileImageUrl(), user.getDefaultProfileImageType(),
            user.getRole());
        return UserPrincipal.create(tokenPayload);
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

        WriteBookReportRequest writeBookReportRequest = WriteBookReportRequest.builder()
            .title("어렵지만 많이 배웠다")
            .content("요런 요런 내용, 저런저런 내용을 많이 배움")
            .build();

        mockMvc.perform(post("/v1/api/books/{bookId}/report", 1L)
                .header("Authorization", "Bearer " + getTestToken())
                .with(user(getUserPrincipal()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(writeBookReportRequest)))
            .andExpect(status().isOk())
            .andDo(document("post-book-report",
                requestHeaders(
                    headerWithName("Authorization").description("Bearer [JWT token]")
                ),
                pathParameters(
                    parameterWithName("bookId").description("Book Id")
                ),
                requestFields(
                    fieldWithPath("title").type(STRING).description("독후감 제목"),
                    fieldWithPath("content").type(STRING).description("독후감 내용")
                )));

        verify(bookReportService).writeReport(any(), any(), any());
    }

    @Test
    void 읽은책_독후감_조회_성공() throws Exception {

        BookReport bookReport = BookReport.builder()
            .title("재미있네")
            .content("다 읽은 후기 알려드립니다")
            .build();
        bookReport.setCreatedAt(LocalDateTime.now());
        BookReportResponse bookReportResponse = BookReportResponse.from(bookReport);
        when(bookReportService.getBookReportResponse(any(), any())).thenReturn(bookReportResponse);
        mockMvc.perform(get("/v1/api/books/{bookId}/report", 1L)
                .header("Authorization", "Bearer " + getTestToken())
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(document("get-book-report",
                requestHeaders(
                    headerWithName("Authorization").description("Bearer [JWT token]")
                ),
                pathParameters(
                    parameterWithName("bookId").description("Book Id")
                ),
                responseFields(
                    fieldWithPath("reportTitle").type(STRING).description("독후감 제목"),
                    fieldWithPath("reportContent").type(STRING).description("독후감 내용"),
                    fieldWithPath("reportCreatedAt").type(STRING).description("독후감 작성 시간")
                )));
    }

    @Test
    void 독후감_삭제_성공() throws Exception {
        mockMvc.perform(delete("/v1/api/books/{bookId}/report", 1L)
                .header("Authorization", "Bearer " + getTestToken())
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(document("delete-book-report",
                requestHeaders(
                    headerWithName("Authorization").description("Bearer [JWT token]")
                ),
                pathParameters(
                    parameterWithName("bookId").description("Book Id")
                )));

        verify(bookReportService).deleteBookReport(any(), any());
    }

    @Test
    void 독후감_수정_성공() throws Exception {
        ReviseBookReportRequest reviseBookReportRequest = ReviseBookReportRequest.builder()
            .reportTitle("제목 바꿔버리기")
            .reportContent(
                "내용은 바꿀수도 아닐수도 있기 때문에 이전 상태 값을 완전히 가지고있기 때문에 똑같이 보내주거나 바꿔서 보내주세요 put으로 멱등성을 보장해줍시다.")
            .build();

        mockMvc.perform(put("/v1/api/books/{bookId}/report", 1L)
                .header("Authorization", "Bearer " + getTestToken())
                .with(user(getUserPrincipal()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviseBookReportRequest)))
            .andExpect(status().isOk())
            .andDo(document("put-book-report",
                requestHeaders(
                    headerWithName("Authorization").description("Bearer [JWT token]")
                ),
                pathParameters(
                    parameterWithName("bookId").description("Book Id")
                ),
                requestFields(
                    fieldWithPath("reportTitle").type(STRING).description("독후감 제목"),
                    fieldWithPath("reportContent").type(STRING).description("독후감 내용")
                )));

        verify(bookReportService).reviseBookReport(any(), any(), any());
    }
}