package toy.bookchat.bookchat.domain.book.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
import toy.bookchat.bookchat.domain.book.dto.BookSearchRequestDto;
import toy.bookchat.bookchat.domain.book.dto.BookSearchResponseDto;
import toy.bookchat.bookchat.domain.book.dto.Meta;
import toy.bookchat.bookchat.domain.book.exception.BookNotFoundException;
import toy.bookchat.bookchat.domain.book.service.BookSearchService;
import toy.bookchat.bookchat.domain.user.User;
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

        User user = User.builder()
            .email("test@gmail.com")
            .password("password")
            .name("testUser")
            .profileImageUrl("somethingImageUrl@naver.com")
            .build();

        return new UserPrincipal(1L, user.getEmail(), user.getPassword(),
            user.getName(), user.getProfileImageUrl(), authorities, user);

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
    public void ???????????????_??????_?????????_??????_401() throws Exception {
        mockMvc.perform(get("/v1/api/books")
                .param("isbn", "234134"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void ????????????_isbn??????_???_??????_?????????_??????() throws Exception {
        List<BookDto> bookDtos = new ArrayList<>();
        bookDtos.add(getBookDto("213123", "effectiveJava", List.of("Joshua")));
        BookSearchResponseDto bookSearchResponseDto = BookSearchResponseDto.builder()
            .bookDtos(bookDtos)
            .build();

        when(bookSearchService.searchByIsbn(any(BookSearchRequestDto.class))).thenReturn(
            bookSearchResponseDto);

        String result = objectMapper.writeValueAsString(bookSearchResponseDto);

        MvcResult mvcResult = mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/api/books")
                .param("isbn", "1231513")
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(document("book-search-isbn",
                requestParameters(parameterWithName("isbn").description("isbn  ??????"))
            ))
            .andReturn();

        verify(bookSearchService).searchByIsbn(any(BookSearchRequestDto.class));
        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo(result);
    }

    @Test
    public void ????????????_?????????_isbn??????_???_??????_?????????_??????() throws Exception {
        mockMvc.perform(get("/v1/api/books")
                .param("isbn", "")
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void ??????api_isbn_??????_??????_?????????_404() throws Exception {
        when(bookSearchService.searchByIsbn(any(BookSearchRequestDto.class))).thenThrow(
            BookNotFoundException.class);

        mockMvc.perform(get("/v1/api/books")
                .param("isbn", "123456")
                .with(user(getUserPrincipal())))
            .andExpect(status().isNotFound());
    }

    @Test
    public void ????????????_?????????_??????_?????????_??????() throws Exception {
        List<BookDto> bookDtos = new ArrayList<>();
        bookDtos.add(getBookDto("213123", "effectiveJava", List.of("Joshua")));

        BookSearchResponseDto bookSearchResponseDto = BookSearchResponseDto.builder()
            .bookDtos(bookDtos)
            .build();

        when(bookSearchService.searchByTitle(any(BookSearchRequestDto.class))).thenReturn(
            bookSearchResponseDto);

        String result = objectMapper.writeValueAsString(bookSearchResponseDto);

        MvcResult mvcResult = mockMvc.perform(get("/v1/api/books")
                .param("title", "effectiveJava")
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(document("book-search-title",
                requestParameters(parameterWithName("title").description("?????? ??????"))))
            .andReturn();

        verify(bookSearchService).searchByTitle(any(BookSearchRequestDto.class));
        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo(result);
    }

    @Test
    public void ????????????_???_?????????_??????_?????????_??????() throws Exception {
        mockMvc.perform(get("/v1/api/books")
                .param("title", "")
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void ??????api_?????????_??????_??????_?????????_404() throws Exception {
        when(bookSearchService.searchByTitle(any(BookSearchRequestDto.class))).thenThrow(
            BookNotFoundException.class);

        mockMvc.perform(get("/v1/api/books")
                .param("title", "effectiveJava")
                .with(user(getUserPrincipal())))
            .andExpect(status().isNotFound());
    }

    @Test
    public void ????????????_?????????_??????_?????????_??????() throws Exception {
        List<BookDto> bookDtos = new ArrayList<>();
        bookDtos.add(getBookDto("213123", "effectiveJava", List.of("Joshua")));

        BookSearchResponseDto bookSearchResponseDto = BookSearchResponseDto.builder()
            .bookDtos(bookDtos)
            .build();

        when(bookSearchService.searchByAuthor(any(BookSearchRequestDto.class))).thenReturn(
            bookSearchResponseDto);

        String result = objectMapper.writeValueAsString(bookSearchResponseDto);

        MvcResult mvcResult = mockMvc.perform(get("/v1/api/books")
                .param("author", "Joshua")
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(document("book-search-author",
                requestParameters(parameterWithName("author").description("??????"))))
            .andReturn();

        verify(bookSearchService).searchByAuthor(any(BookSearchRequestDto.class));
        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo(result);
    }

    @Test
    public void ????????????_???_?????????_??????_?????????_??????() throws Exception {
        mockMvc.perform(get("/v1/api/books")
                .param("author", "")
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void ??????api_?????????_??????_??????_?????????_404() throws Exception {
        when(bookSearchService.searchByAuthor(any(BookSearchRequestDto.class))).thenThrow(
            BookNotFoundException.class);

        mockMvc.perform(get("/v1/api/books")
                .param("author", "Joshua")
                .with(user(getUserPrincipal())))
            .andExpect(status().isNotFound());
    }

    @Test
    public void ????????????_isbn_?????????_paging_??????() throws Exception {
        List<BookDto> bookDtos = new ArrayList<>();
        bookDtos.add(getBookDto("213123", "effectiveJava", List.of("Joshua")));

        Meta meta = Meta.builder()
            .total_count(5)
            .pageable_count(5)
            .is_end(false)
            .build();

        BookSearchResponseDto bookSearchResponseDto = BookSearchResponseDto.builder()
            .bookDtos(bookDtos)
            .meta(meta)
            .build();

        when(bookSearchService.searchByIsbn(any(BookSearchRequestDto.class))).thenReturn(
            bookSearchResponseDto);

        String result = objectMapper.writeValueAsString(bookSearchResponseDto);

        MvcResult mvcResult = mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/api/books")
                .param("isbn", "1231513")
                .param("size", "5")
                .param("page", "1")
                .param("sort", "ACCURACY")
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(document("book-search-isbn-paging",
                requestParameters(parameterWithName("isbn").description("isbn  ??????"),
                    parameterWithName("size").description("??? ?????? ????????? ?????? ??? - page ??? size"),
                    parameterWithName("page").description("??? ?????? ????????? page ???"),
                    parameterWithName("sort").description("????????? ?????? ??????"))
            )).andReturn();

        verify(bookSearchService).searchByIsbn(any(BookSearchRequestDto.class));
        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo(result);

    }

    @Test
    public void ????????????_?????????_?????????_paging_??????() throws Exception {
        List<BookDto> bookDtos = new ArrayList<>();
        bookDtos.add(getBookDto("213123", "effectiveJava", List.of("Joshua")));

        Meta meta = Meta.builder()
            .total_count(5)
            .pageable_count(5)
            .is_end(false)
            .build();

        BookSearchResponseDto bookSearchResponseDto = BookSearchResponseDto.builder()
            .bookDtos(bookDtos)
            .meta(meta)
            .build();

        when(bookSearchService.searchByTitle(any(BookSearchRequestDto.class))).thenReturn(
            bookSearchResponseDto);

        String result = objectMapper.writeValueAsString(bookSearchResponseDto);

        MvcResult mvcResult = mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/api/books")
                .param("title", "effectiveJava")
                .param("size", "5")
                .param("page", "1")
                .param("sort", "LATEST")
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(document("book-search-title-paging",
                requestParameters(parameterWithName("title").description("?????????"),
                    parameterWithName("size").description("??? ?????? ????????? ?????? ??? - page ??? size"),
                    parameterWithName("page").description("??? ?????? ????????? page ???"),
                    parameterWithName("sort").description("????????? ?????? ??????"))
            )).andReturn();

        verify(bookSearchService).searchByTitle(any(BookSearchRequestDto.class));
        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo(result);

    }

    @Test
    public void ????????????_?????????_?????????_paging_??????() throws Exception {
        List<BookDto> bookDtos = new ArrayList<>();
        bookDtos.add(getBookDto("213123", "effectiveJava", List.of("Joshua")));

        Meta meta = Meta.builder()
            .total_count(5)
            .pageable_count(5)
            .is_end(false)
            .build();

        BookSearchResponseDto bookSearchResponseDto = BookSearchResponseDto.builder()
            .bookDtos(bookDtos)
            .meta(meta)
            .build();

        when(bookSearchService.searchByAuthor(any(BookSearchRequestDto.class))).thenReturn(
            bookSearchResponseDto);

        String result = objectMapper.writeValueAsString(bookSearchResponseDto);

        MvcResult mvcResult = mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/api/books")
                .param("author", "Joshua")
                .param("size", "5")
                .param("page", "1")
                .param("sort", "LATEST")
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(document("book-search-author-paging",
                requestParameters(parameterWithName("author").description("?????????"),
                    parameterWithName("size").description("??? ?????? ????????? ?????? ??? - page ??? size"),
                    parameterWithName("page").description("??? ?????? ????????? page ???"),
                    parameterWithName("sort").description("????????? ?????? ??????"))
            )).andReturn();

        verify(bookSearchService).searchByAuthor(any(BookSearchRequestDto.class));
        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo(result);

    }
}
