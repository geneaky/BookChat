package toy.bookchat.bookchat.domain.book.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import toy.bookchat.bookchat.domain.AuthenticationTestExtension;
import toy.bookchat.bookchat.domain.book.dto.BookDto;
import toy.bookchat.bookchat.domain.book.exception.BookNotFoundException;
import toy.bookchat.bookchat.domain.book.service.BookSearchService;
import toy.bookchat.bookchat.security.user.UserPrincipal;

@WebMvcTest(controllers = BookController.class,
    includeFilters = @ComponentScan.Filter(classes = {EnableWebSecurity.class}))
@AutoConfigureRestDocs
public class BookControllerTest extends AuthenticationTestExtension {

    @MockBean
    BookSearchService bookSearchService;

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

    private BookDto getBookDto(String isbn, String title, List<String> author) {
        BookDto bookDto = BookDto.builder()
            .isbn(isbn)
            .title(title)
            .author(author)
            .publisher("testPublisher")
            .bookCoverImageUrl("bookCoverImageUrl")
            .build();
        return bookDto;
    }

    @Test
    public void 로그인하지_않은_사용자_요청_401() throws Exception {
        mockMvc.perform(get("/v1/api/books")
                .param("isbn", "234134"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void 사용자가_isbn으로_책_검색_요청시_성공() throws Exception {
        List<BookDto> bookDtos = new ArrayList<>();
        bookDtos.add(getBookDto("213123", "effectiveJava", List.of("Joshua")));

        when(bookSearchService.searchByIsbn("1231513")).thenReturn(bookDtos);

        String result = objectMapper.writeValueAsString(bookDtos);

        MvcResult mvcResult = mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/api/books")
                .param("isbn", "1231513")
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(document("book-search-isbn",
                requestParameters(parameterWithName("isbn").description("isbn  번호"))
            ))
            .andReturn();

        verify(bookSearchService).searchByIsbn(anyString());
        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo(result);
    }

    @Test
    public void 사용자가_잘못된_isbn으로_책_검색_요청시_실패() throws Exception {
        mockMvc.perform(get("/v1/api/books")
                .param("isbn", "")
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void 외부api_isbn_검색_요청_실패시_404() throws Exception {
        when(bookSearchService.searchByIsbn("123456")).thenThrow(BookNotFoundException.class);

        mockMvc.perform(get("/v1/api/books")
                .param("isbn", "123456")
                .with(user(getUserPrincipal())))
            .andExpect(status().isNotFound());
    }

    @Test
    public void 사용자가_도서명_검색_요청시_성공() throws Exception {
        List<BookDto> bookDtos = new ArrayList<>();
        bookDtos.add(getBookDto("213123", "effectiveJava", List.of("Joshua")));

        when(bookSearchService.searchByTitle("effectiveJava")).thenReturn(bookDtos);

        String result = objectMapper.writeValueAsString(bookDtos);

        MvcResult mvcResult = mockMvc.perform(get("/v1/api/books")
                .param("title", "effectiveJava")
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(document("book-search-title",
                requestParameters(parameterWithName("title").description("도서 제목"))))
            .andReturn();

        verify(bookSearchService).searchByTitle(anyString());
        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo(result);
    }

    @Test
    public void 사용자가_빈_도서명_검색_요청시_실패() throws Exception {
        mockMvc.perform(get("/v1/api/books")
                .param("title", "")
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void 외부api_도서명_검색_요청_실패시_404() throws Exception {
        when(bookSearchService.searchByTitle("effectiveJava")).thenThrow(
            BookNotFoundException.class);

        mockMvc.perform(get("/v1/api/books")
                .param("title", "effectiveJava")
                .with(user(getUserPrincipal())))
            .andExpect(status().isNotFound());
    }

    @Test
    public void 사용자가_작가명_검색_요청시_성공() throws Exception {
        List<BookDto> bookDtos = new ArrayList<>();
        bookDtos.add(getBookDto("213123", "effectiveJava", List.of("Joshua")));

        when(bookSearchService.searchByAuthor("Joshua")).thenReturn(bookDtos);

        String result = objectMapper.writeValueAsString(bookDtos);

        MvcResult mvcResult = mockMvc.perform(get("/v1/api/books")
                .param("author", "Joshua")
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(document("book-search-author",
                requestParameters(parameterWithName("author").description("작가"))))
            .andReturn();

        verify(bookSearchService).searchByAuthor(anyString());
        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo(result);
    }

    @Test
    public void 사용자가_빈_작가명_검색_요청시_실패() throws Exception {
        mockMvc.perform(get("/v1/api/books")
                .param("author", "")
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void 외부api_작가명_검색_요청_실패시_404() throws Exception {
        when(bookSearchService.searchByAuthor("Joshua")).thenThrow(BookNotFoundException.class);

        mockMvc.perform(get("/v1/api/books")
                .param("author", "Joshua")
                .with(user(getUserPrincipal())))
            .andExpect(status().isNotFound());
    }
}
