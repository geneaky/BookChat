package toy.bookchat.bookchat.domain.book.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static toy.bookchat.bookchat.domain.user.ROLE.USER;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import toy.bookchat.bookchat.domain.AuthenticationTestExtension;
import toy.bookchat.bookchat.domain.book.service.BookSearchService;
import toy.bookchat.bookchat.domain.book.service.dto.request.BookSearchRequest;
import toy.bookchat.bookchat.domain.book.service.dto.request.Meta;
import toy.bookchat.bookchat.domain.book.service.dto.response.BookResponse;
import toy.bookchat.bookchat.domain.book.service.dto.response.BookSearchResponse;
import toy.bookchat.bookchat.domain.user.ReadingTaste;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.exception.book.BookNotFoundException;
import toy.bookchat.bookchat.security.SecurityConfig;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPrincipal;

@WebMvcTest(controllers = BookController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        SecurityConfig.class}))
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "bookchat.link", uriPort = 443)
class BookControllerTest extends AuthenticationTestExtension {

    @MockBean
    BookSearchService bookSearchService;
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

    private BookResponse getBookResponse(String isbn, String title, String datetime,
        List<String> author) {
        BookResponse bookResponse = BookResponse.builder()
            .isbn(isbn)
            .title(title)
            .datetime(datetime.substring(0, 10))
            .author(author)
            .publisher("testPublisher")
            .bookCoverImageUrl("bookCoverImageUrl")
            .build();
        return bookResponse;
    }

    private String getTestToken()
        throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
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
    void 로그인하지_않은_사용자_요청_401() throws Exception {
        mockMvc.perform(get("/v1/api/books")
                .param("isbn", "234134"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void 외부api_검색_요청_실패시_404() throws Exception {
        when(bookSearchService.searchByQuery(any(BookSearchRequest.class))).thenThrow(
            BookNotFoundException.class);

        mockMvc.perform(get("/v1/api/books")
                .header("Authorization", "Bearer " + getTestToken())
                .param("query", "effectiveJava")
                .with(user(getUserPrincipal())))
            .andExpect(status().isNotFound());
    }

    @Test
    void 올바르지않은_요청으로_외부api_검색_요청시_400() throws Exception {
        mockMvc.perform(get("/v1/api/books")
                .header("Authorization", "Bearer " + getTestToken())
                .param("query", " ")
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());
    }

    @Test
    void 사용자가_isbn_검색시_paging_성공() throws Exception {
        List<BookResponse> bookResponses = new ArrayList<>();
        bookResponses.add(
            getBookResponse("213123", "effectiveJava", "2014-11-17T00:00:00.000+09:00",
                List.of("Joshua")));

        Meta meta = Meta.builder()
            .total_count(5)
            .pageable_count(5)
            .is_end(false)
            .build();

        BookSearchResponse bookSearchResponse = BookSearchResponse.builder()
            .bookResponses(bookResponses)
            .meta(meta)
            .build();

        when(bookSearchService.searchByQuery(any(BookSearchRequest.class))).thenReturn(
            bookSearchResponse);

        String result = objectMapper.writeValueAsString(bookSearchResponse);

        MvcResult mvcResult = mockMvc.perform(get("/v1/api/books")
                .header("Authorization", "Bearer " + getTestToken())
                .param("query", "1231513")
                .param("size", "5")
                .param("page", "1")
                .param("sort", "ACCURACY")
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(document("book-search-paging",
                requestHeaders(
                    headerWithName("Authorization").description("Bearer [JWT token]")),
                requestParameters(parameterWithName("query").description("검색 키워드 [ISBN, 도서명, 저자명]"),
                    parameterWithName("size").description("한 번에 조회할 책의 수 - page 당 size"),
                    parameterWithName("page").description("한 번에 조회할 page 수"),
                    parameterWithName("sort").description("조회시 정렬 옵션")),
                responseFields(
                    fieldWithPath("bookResponses[].isbn").type(STRING).description("ISBN"),
                    fieldWithPath("bookResponses[].title").type(STRING).description("제목"),
                    fieldWithPath("bookResponses[].datetime").type(STRING).description("출간일"),
                    fieldWithPath("bookResponses[].author[]").type(ARRAY).description("저자"),
                    fieldWithPath("bookResponses[].publisher").type(STRING).description("출판사"),
                    fieldWithPath("bookResponses[].bookCoverImageUrl").type(STRING)
                        .description("책 표지 이미지"),
                    fieldWithPath("meta.is_end").type(BOOLEAN).description("마지막 페이지 여부"),
                    fieldWithPath("meta.pageable_count").type(NUMBER).description("가져온 페이지 수"),
                    fieldWithPath("meta.total_count").type(NUMBER).description("총 페이지 수")
                ))).andReturn();

        verify(bookSearchService).searchByQuery(any(BookSearchRequest.class));
        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo(result);

    }
}
