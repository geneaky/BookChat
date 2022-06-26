package toy.bookchat.bookchat.domain.bookshelf.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import toy.bookchat.bookchat.domain.AuthenticationTestExtension;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.dto.BookShelfRequestDto;
import toy.bookchat.bookchat.domain.bookshelf.service.BookShelfService;
import toy.bookchat.bookchat.security.user.UserPrincipal;

@WebMvcTest(controllers = BookShelfController.class,
    includeFilters = @ComponentScan.Filter(classes = {EnableWebSecurity.class}))
@AutoConfigureRestDocs
public class BookShelfControllerTest extends AuthenticationTestExtension {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BookShelfService bookShelfService;

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

    private BookShelfRequestDto getBookShelfRequestDto(ReadingStatus readingStatus) {
        BookShelfRequestDto bookShelfRequestDto = BookShelfRequestDto.builder()
            .isbn("124151214")
            .title("effectiveJava")
            .author(List.of("Joshua"))
            .publisher("oreilly")
            .bookCoverImageUrl("bookCoverImage.com")
            .readingStatus(readingStatus)
            .build();
        return bookShelfRequestDto;
    }

    @Test
    public void 로그인하지_않은_사용자_요청_401() throws Exception {
        mockMvc.perform(post("/v1/api/bookshelf/books")
                .content(objectMapper.writeValueAsString(getBookShelfRequestDto(ReadingStatus.READING)))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void 읽고_있는_책_등록_성공() throws Exception {
        mockMvc.perform(post("/v1/api/bookshelf/books")
                .content(objectMapper.writeValueAsString(getBookShelfRequestDto(ReadingStatus.READING)))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isCreated())
            .andDo(document("bookshelf",
                requestFields(fieldWithPath("isbn").description("isbn"),
                    fieldWithPath("title").description("title"),
                    fieldWithPath("author").description("author"),
                    fieldWithPath("publisher").description("publisher"),
                    fieldWithPath("bookCoverImageUrl").description("bookCoverImageUrl"),
                    fieldWithPath("readingStatus").description("READING"))));

        verify(bookShelfService).putBookOnBookShelf(any(BookShelfRequestDto.class),
            getUserPrincipal().getId());
    }

    @Test
    public void 읽은_책_등록_성공() throws Exception {
        mockMvc.perform(post("/v1/api/bookshelf/books")
                .content(
                    objectMapper.writeValueAsString(getBookShelfRequestDto(ReadingStatus.COMPLETE)))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isCreated())
            .andDo(document("bookshelf",
                requestFields(fieldWithPath("isbn").description("isbn"),
                    fieldWithPath("title").description("title"),
                    fieldWithPath("author").description("author"),
                    fieldWithPath("publisher").description("publisher"),
                    fieldWithPath("bookCoverImageUrl").description("bookCoverImageUrl"),
                    fieldWithPath("readingStatus").description("COMPLETE"))));

        verify(bookShelfService).putBookOnBookShelf(any(BookShelfRequestDto.class),
            getUserPrincipal().getId());
    }

    @Test
    public void 읽을_책_등록_성공() throws Exception {
        mockMvc.perform(post("/v1/api/bookshelf/books")
                .content(objectMapper.writeValueAsString(getBookShelfRequestDto(ReadingStatus.READY)))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isCreated())
            .andDo(document("bookshelf",
                requestFields(fieldWithPath("isbn").description("isbn"),
                    fieldWithPath("title").description("title"),
                    fieldWithPath("author").description("author"),
                    fieldWithPath("publisher").description("publisher"),
                    fieldWithPath("bookCoverImageUrl").description("bookCoverImageUrl"),
                    fieldWithPath("readingStatus").description("READY"))));

        verify(bookShelfService).putBookOnBookShelf(any(BookShelfRequestDto.class),
            getUserPrincipal().getId());
    }

    @Test
    public void null로_요청_실패() throws Exception {
        mockMvc.perform(post("/v1/api/bookshelf/books")
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void 존재하지않은_readingstatus_책_등록_실패() throws Exception {
        BookShelfTestRequestDto bookShelfTestRequestDto = new BookShelfTestRequestDto("124151214",
            "effectiveJava", List.of("Joshua"), "oreilly",
            "bookCoverImage.com", "NOT_EXISTED_READING_STATUS");

        mockMvc.perform(post("/v1/api/bookshelf/books")
                .content(objectMapper.writeValueAsString(bookShelfTestRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void readingStatus_없이_요청_실패() throws Exception {
        BookShelfRequestDto bookShelfRequestDto = BookShelfRequestDto.builder()
            .isbn("135135414")
            .title("effectiveJava")
            .author(List.of("Joshua"))
            .publisher("oreilly")
            .bookCoverImageUrl("bookCoverImage.com")
            .build();

        mockMvc.perform(post("/v1/api/bookshelf/books")
                .content(objectMapper.writeValueAsString(bookShelfRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());

    }

    @Test
    public void isbn_없이_책_등록_요청_실패() throws Exception {
        BookShelfRequestDto bookShelfRequestDto = BookShelfRequestDto.builder()
            .title("effectiveJava")
            .author(List.of("Joshua"))
            .publisher("oreilly")
            .bookCoverImageUrl("bookCoverImage.com")
            .readingStatus(ReadingStatus.READY)
            .build();

        mockMvc.perform(post("/v1/api/bookshelf/books")
                .content(objectMapper.writeValueAsString(bookShelfRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void isbn_빈_문자열_요청_실패() throws Exception {
        BookShelfRequestDto bookShelfRequestDto = BookShelfRequestDto.builder()
            .isbn("")
            .title("effectiveJava")
            .author(List.of("Joshua"))
            .publisher("oreilly")
            .bookCoverImageUrl("bookCoverImage.com")
            .readingStatus(ReadingStatus.READY)
            .build();

        mockMvc.perform(post("/v1/api/bookshelf/books")
                .content(objectMapper.writeValueAsString(bookShelfRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void 제목_없이_요청_실패() throws Exception {
        BookShelfRequestDto bookShelfRequestDto = BookShelfRequestDto.builder()
            .isbn("124151214")
            .author(List.of("Joshua"))
            .publisher("oreilly")
            .bookCoverImageUrl("bookCoverImage.com")
            .readingStatus(ReadingStatus.READY)
            .build();

        mockMvc.perform(post("/v1/api/bookshelf/books")
                .content(objectMapper.writeValueAsString(bookShelfRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void 제목_빈_문자열_요청_실패() throws Exception {
        BookShelfRequestDto bookShelfRequestDto = BookShelfRequestDto.builder()
            .isbn("124151214")
            .title("")
            .author(List.of("Joshua"))
            .publisher("oreilly")
            .bookCoverImageUrl("bookCoverImage.com")
            .readingStatus(ReadingStatus.READY)
            .build();

        mockMvc.perform(post("/v1/api/bookshelf/books")
                .content(objectMapper.writeValueAsString(bookShelfRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());

    }

    @Test
    public void 작가명_없이_요청_실패() throws Exception {
        BookShelfRequestDto bookShelfRequestDto = BookShelfRequestDto.builder()
            .isbn("124151214")
            .title("effectiveJava")
            .publisher("oreilly")
            .bookCoverImageUrl("bookCoverImage.com")
            .readingStatus(ReadingStatus.READY)
            .build();

        mockMvc.perform(post("/v1/api/bookshelf/books")
                .content(objectMapper.writeValueAsString(bookShelfRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());

    }

    @Test
    public void 작가명_빈_문자열_요청_실패() throws Exception {
        BookShelfRequestDto bookShelfRequestDto = BookShelfRequestDto.builder()
            .isbn("124151214")
            .title("effectiveJava")
            .author(List.of(""))
            .publisher("oreilly")
            .bookCoverImageUrl("bookCoverImage.com")
            .readingStatus(ReadingStatus.READY)
            .build();

        mockMvc.perform(post("/v1/api/bookshelf/books")
                .content(objectMapper.writeValueAsString(bookShelfRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());

    }

    @Test
    public void 출판사_없이_요청_실패() throws Exception {
        BookShelfRequestDto bookShelfRequestDto = BookShelfRequestDto.builder()
            .isbn("124151214")
            .title("effectiveJava")
            .author(List.of("Joshua"))
            .bookCoverImageUrl("bookCoverImage.com")
            .readingStatus(ReadingStatus.READY)
            .build();

        mockMvc.perform(post("/v1/api/bookshelf/books")
                .content(objectMapper.writeValueAsString(bookShelfRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());

    }

    @Test
    public void 출판사_빈_문자열_요청_실패() throws Exception {
        BookShelfRequestDto bookShelfRequestDto = BookShelfRequestDto.builder()
            .isbn("124151214")
            .title("effectiveJava")
            .author(List.of("Joshua"))
            .publisher("")
            .bookCoverImageUrl("bookCoverImage.com")
            .readingStatus(ReadingStatus.READY)
            .build();

        mockMvc.perform(post("/v1/api/bookshelf/books")
                .content(objectMapper.writeValueAsString(bookShelfRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());

    }

    static class BookShelfTestRequestDto {

        @JsonProperty
        String isbn;
        @JsonProperty
        String title;
        @JsonProperty
        List<String> author;
        @JsonProperty
        String publisher;
        @JsonProperty
        String bookCoverImageUrl;
        @JsonProperty
        String readingStatus;

        public BookShelfTestRequestDto(String isbn, String title, List<String> author,
            String publisher, String bookCoverImageUrl, String readingStatus) {
            this.isbn = isbn;
            this.title = title;
            this.author = author;
            this.publisher = publisher;
            this.bookCoverImageUrl = bookCoverImageUrl;
            this.readingStatus = readingStatus;
        }
    }

}
