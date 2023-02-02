package toy.bookchat.bookchat.domain.bookshelf.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static toy.bookchat.bookchat.domain.bookshelf.ReadingStatus.COMPLETE;
import static toy.bookchat.bookchat.domain.bookshelf.ReadingStatus.READING;
import static toy.bookchat.bookchat.domain.bookshelf.ReadingStatus.WISH;
import static toy.bookchat.bookchat.domain.bookshelf.Star.FOUR;
import static toy.bookchat.bookchat.domain.bookshelf.Star.FOUR_HALF;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;
import toy.bookchat.bookchat.domain.ControllerTestExtension;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.service.BookShelfService;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.BookRequest;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.BookShelfRequest;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.ReviseBookShelfRequest;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.response.ExistenceBookOnBookShelfResponse;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.response.SearchBookShelfByReadingStatus;
import toy.bookchat.bookchat.domain.user.User;

@BookShelfPresentationTest
class BookShelfControllerTest extends ControllerTestExtension {

    public final String JWT_TOKEN = getTestToken();

    @MockBean
    BookShelfService bookShelfService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    private BookRequest getBookRequest() {
        return BookRequest.builder()
            .isbn("124151214")
            .title("effectiveJava")
            .authors(List.of("Joshua"))
            .publisher("oreilly")
            .bookCoverImageUrl("bookCoverImage.com")
            .publishAt(LocalDate.now())
            .build();
    }

    private BookShelfRequest getBookShelfRequest(ReadingStatus readingStatus) {
        if (readingStatus == COMPLETE) {
            return BookShelfRequest.builder()
                .bookRequest(getBookRequest())
                .readingStatus(readingStatus)
                .star(FOUR)
                .build();
        }
        return BookShelfRequest.builder()
            .bookRequest(getBookRequest())
            .readingStatus(readingStatus)
            .build();
    }

    @Test
    void 로그인하지_않은_사용자_요청_401() throws Exception {
        mockMvc.perform(post("/v1/api/bookshelf/books")
                .content(objectMapper.writeValueAsString(getBookShelfRequest(READING)))
                .contentType(APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void 읽고_있는_책_등록_성공() throws Exception {
        mockMvc.perform(post("/v1/api/bookshelf/books")
                .header(AUTHORIZATION, JWT_TOKEN)
                .content(objectMapper.writeValueAsString(getBookShelfRequest(READING)))
                .contentType(APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(document("bookshelf-reading",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")),
                requestFields(fieldWithPath("bookRequest.isbn").description("ISBN"),
                    fieldWithPath("bookRequest.title").type(STRING).description("제목"),
                    fieldWithPath("bookRequest.authors[]").type(ARRAY).description("저자"),
                    fieldWithPath("bookRequest.publisher").type(STRING).description("출판사"),
                    fieldWithPath("bookRequest.bookCoverImageUrl").type(STRING).optional()
                        .description("책 커버 이미지 URI"),
                    fieldWithPath("bookRequest.publishAt").type(STRING).description("출판일"),
                    fieldWithPath("readingStatus").type(STRING).description("READING"),
                    fieldWithPath("star").ignored())));

        verify(bookShelfService).putBookOnBookShelf(any(BookShelfRequest.class), any());
    }

    @Test
    void 읽은_책_등록_성공() throws Exception {
        mockMvc.perform(post("/v1/api/bookshelf/books")
                .header(AUTHORIZATION, JWT_TOKEN)
                .content(
                    objectMapper.writeValueAsString(getBookShelfRequest(COMPLETE)))
                .contentType(APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(document("bookshelf-complete",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                requestFields(fieldWithPath("bookRequest.isbn").type(STRING).description("ISBN"),
                    fieldWithPath("bookRequest.title").type(STRING).description("제목"),
                    fieldWithPath("bookRequest.authors[]").type(ARRAY).description("저자"),
                    fieldWithPath("bookRequest.publisher").type(STRING).description("출판사"),
                    fieldWithPath("bookRequest.bookCoverImageUrl").type(STRING).optional()
                        .description("책 커버 이미지 URI"),
                    fieldWithPath("bookRequest.publishAt").type(STRING).description("출판일"),
                    fieldWithPath("readingStatus").type(STRING).description("COMPLETE"),
                    fieldWithPath("star").type(STRING).description("평점"))));

        verify(bookShelfService).putBookOnBookShelf(any(BookShelfRequest.class),
            any());
    }

    @Test
    void 읽을_책_등록_성공() throws Exception {
        mockMvc.perform(post("/v1/api/bookshelf/books")
                .header(AUTHORIZATION, JWT_TOKEN)
                .content(objectMapper.writeValueAsString(getBookShelfRequest(WISH)))
                .contentType(APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(document("bookshelf-wish",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [openid token]")
                ),
                requestFields(fieldWithPath("bookRequest.isbn").type(STRING).description("ISBN"),
                    fieldWithPath("bookRequest.title").type(STRING).description("제목"),
                    fieldWithPath("bookRequest.authors[]").type(ARRAY).description("저자"),
                    fieldWithPath("bookRequest.publisher").type(STRING).description("출판사"),
                    fieldWithPath("bookRequest.bookCoverImageUrl").type(STRING).optional()
                        .description("책 커버 이미지 URI"),
                    fieldWithPath("bookRequest.publishAt").type(STRING).description("출판일"),
                    fieldWithPath("readingStatus").type(STRING).description("WISH"),
                    fieldWithPath("star").ignored())));

        verify(bookShelfService).putBookOnBookShelf(any(BookShelfRequest.class),
            any());
    }

    @Test
    void null로_요청_실패() throws Exception {
        mockMvc.perform(post("/v1/api/bookshelf/books")
                .header(AUTHORIZATION, JWT_TOKEN)
                .contentType(APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());
    }

    @Test
    void readingStatus_없이_요청_실패() throws Exception {
        BookShelfRequest bookShelfRequest = BookShelfRequest.builder()
            .bookRequest(getBookRequest())
            .build();

        mockMvc.perform(post("/v1/api/bookshelf/books")
                .header(AUTHORIZATION, JWT_TOKEN)
                .content(objectMapper.writeValueAsString(bookShelfRequest))
                .contentType(APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());

    }

    @Test
    void isbn_없이_책_등록_요청_실패() throws Exception {
        BookRequest bookRequest = BookRequest.builder()
            .title("effectiveJava")
            .authors(List.of("Joshua"))
            .publisher("oreilly")
            .bookCoverImageUrl("bookCoverImage.com")
            .build();
        BookShelfRequest bookShelfRequest = BookShelfRequest.builder()
            .bookRequest(bookRequest)
            .readingStatus(WISH)
            .build();

        mockMvc.perform(post("/v1/api/bookshelf/books")
                .header(AUTHORIZATION, JWT_TOKEN)
                .content(objectMapper.writeValueAsString(bookShelfRequest))
                .contentType(APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());
    }

    @Test
    void isbn_빈_문자열_요청_실패() throws Exception {
        BookRequest bookRequest = BookRequest.builder()
            .isbn("")
            .title("effectiveJava")
            .authors(List.of("Joshua"))
            .publisher("oreilly")
            .bookCoverImageUrl("bookCoverImage.com")
            .build();
        BookShelfRequest bookShelfRequest = BookShelfRequest.builder()
            .bookRequest(bookRequest)
            .readingStatus(WISH)
            .build();

        mockMvc.perform(post("/v1/api/bookshelf/books")
                .header(AUTHORIZATION, JWT_TOKEN)
                .content(objectMapper.writeValueAsString(bookShelfRequest))
                .contentType(APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());
    }

    @Test
    void 제목_없이_요청_실패() throws Exception {
        BookRequest bookRequest = BookRequest.builder()
            .isbn("124151214")
            .authors(List.of("Joshua"))
            .publisher("oreilly")
            .bookCoverImageUrl("bookCoverImage.com")
            .build();
        BookShelfRequest bookShelfRequest = BookShelfRequest.builder()
            .bookRequest(bookRequest)
            .readingStatus(WISH)
            .build();

        mockMvc.perform(post("/v1/api/bookshelf/books")
                .header(AUTHORIZATION, JWT_TOKEN)
                .content(objectMapper.writeValueAsString(bookShelfRequest))
                .contentType(APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());
    }

    @Test
    void 제목_빈_문자열_요청_실패() throws Exception {
        BookRequest bookRequest = BookRequest.builder()
            .isbn("124151214")
            .title("")
            .authors(List.of("Joshua"))
            .publisher("oreilly")
            .bookCoverImageUrl("bookCoverImage.com")
            .build();

        BookShelfRequest bookShelfRequest = BookShelfRequest.builder()
            .bookRequest(bookRequest)
            .readingStatus(WISH)
            .build();

        mockMvc.perform(post("/v1/api/bookshelf/books")
                .header(AUTHORIZATION, JWT_TOKEN)
                .content(objectMapper.writeValueAsString(bookShelfRequest))
                .contentType(APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());
    }

    @Test
    void 작가명_없이_요청_실패() throws Exception {
        BookRequest bookRequest = BookRequest.builder()
            .isbn("124151214")
            .title("effectiveJava")
            .publisher("oreilly")
            .bookCoverImageUrl("bookCoverImage.com")
            .build();

        BookShelfRequest bookShelfRequest = BookShelfRequest.builder()
            .bookRequest(bookRequest)
            .readingStatus(WISH)
            .build();

        mockMvc.perform(post("/v1/api/bookshelf/books")
                .header(AUTHORIZATION, JWT_TOKEN)
                .content(objectMapper.writeValueAsString(bookShelfRequest))
                .contentType(APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());
    }

    @Test
    void 작가명_빈_문자열_요청_실패() throws Exception {
        BookRequest bookRequest = BookRequest.builder()
            .isbn("124151214")
            .title("effectiveJava")
            .authors(List.of(""))
            .publisher("oreilly")
            .bookCoverImageUrl("bookCoverImage.com")
            .build();

        BookShelfRequest bookShelfRequest = BookShelfRequest.builder()
            .bookRequest(bookRequest)
            .readingStatus(WISH)
            .build();

        mockMvc.perform(post("/v1/api/bookshelf/books")
                .header(AUTHORIZATION, JWT_TOKEN)
                .content(objectMapper.writeValueAsString(bookShelfRequest))
                .contentType(APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());
    }

    @Test
    void 출판사_없이_요청_실패() throws Exception {
        BookRequest bookRequest = BookRequest.builder()
            .isbn("124151214")
            .title("effectiveJava")
            .authors(List.of("Joshua"))
            .bookCoverImageUrl("bookCoverImage.com")
            .build();

        BookShelfRequest bookShelfRequest = BookShelfRequest.builder()
            .bookRequest(bookRequest)
            .readingStatus(WISH)
            .build();

        mockMvc.perform(post("/v1/api/bookshelf/books")
                .header(AUTHORIZATION, JWT_TOKEN)
                .content(objectMapper.writeValueAsString(bookShelfRequest))
                .contentType(APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());
    }

    @Test
    void 출판사_빈_문자열_요청_실패() throws Exception {
        BookRequest bookRequest = BookRequest.builder()
            .isbn("124151214")
            .title("effectiveJava")
            .authors(List.of("Joshua"))
            .publisher("")
            .bookCoverImageUrl("bookCoverImage.com")
            .build();

        BookShelfRequest bookShelfRequest = BookShelfRequest.builder()
            .bookRequest(bookRequest)
            .readingStatus(WISH)
            .build();

        mockMvc.perform(post("/v1/api/bookshelf/books")
                .header(AUTHORIZATION, JWT_TOKEN)
                .content(objectMapper.writeValueAsString(bookShelfRequest))
                .contentType(APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());
    }

    @Test
    void 읽고있는_책_조회_성공() throws Exception {
        List<BookShelf> result = new ArrayList<>();

        User user = User.builder().build();
        Book book = Book.builder()
            .id(1L)
            .isbn("12345")
            .title("effectiveJava")
            .authors(List.of("joshua"))
            .publisher("jpub")
            .bookCoverImageUrl("testBookCoverImageUrl")
            .publishAt(LocalDate.now())
            .build();

        BookShelf bookShelf = BookShelf.builder()
            .book(book)
            .user(user)
            .pages(152)
            .readingStatus(READING)
            .build();

        result.add(bookShelf);

        PageRequest pageRequest = PageRequest.of(0, 1, Sort.by("id").descending());
        PageImpl<BookShelf> bookShelves = new PageImpl<>(result, pageRequest, 1);
        SearchBookShelfByReadingStatus searchBookShelfByReadingStatus = new SearchBookShelfByReadingStatus(
            bookShelves);

        when(bookShelfService.takeBooksOutOfBookShelf(any(ReadingStatus.class), any(Pageable.class),
            any())).thenReturn(searchBookShelfByReadingStatus);

        mockMvc.perform(get("/v1/api/bookshelf/books")
                .header(AUTHORIZATION, JWT_TOKEN)
                .queryParam("readingStatus", "READING")
                .queryParam("size", "5")
                .queryParam("page", "1")
                .queryParam("sort", "id,DESC")
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("get-bookshelf-reading",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                requestParameters(
                    parameterWithName("readingStatus").description("READING"),
                    parameterWithName("size").description("page 당 size"),
                    parameterWithName("page").description("한번에 조회할 page수"),
                    parameterWithName("sort").description("등록순-id")
                ),
                responseFields(
                    fieldWithPath("contents[].bookId").type(NUMBER).description("Book Id"),
                    fieldWithPath("contents[].title").type(STRING).description("제목"),
                    fieldWithPath("contents[].isbn").type(STRING).description("ISBN"),
                    fieldWithPath("contents[].bookCoverImageUrl").type(STRING).optional()
                        .description("책 커버 이미지 URI"),
                    fieldWithPath("contents[].authors[]").type(ARRAY).description("저자"),
                    fieldWithPath("contents[].publisher").type(STRING).description("출판사"),
                    fieldWithPath("contents[].publishAt").type(STRING).description("출판일자"),
                    fieldWithPath("contents[].pages").type(NUMBER).description("현재 읽고 있는 페이지 번호"),
                    fieldWithPath("contents[].star").type(STRING).optional().description("평점"),
                    fieldWithPath("contents[].singleLineAssessment").type(STRING).optional()
                        .description("한 줄 평"))
                    .and(getPageField()))
            );

        verify(bookShelfService).takeBooksOutOfBookShelf(any(ReadingStatus.class),
            any(Pageable.class), any());
    }

    @Test
    void 읽은_책_조회_성공() throws Exception {
        List<BookShelf> result = new ArrayList<>();

        User user = User.builder().build();

        Book book = Book.builder()
            .id(1L)
            .isbn("12345")
            .title("effectiveJava")
            .authors(List.of("joshua"))
            .publisher("jpub")
            .bookCoverImageUrl("testBookCoverImageUrl")
            .publishAt(LocalDate.now())
            .build();

        BookShelf bookShelf = BookShelf.builder()
            .book(book)
            .user(user)
            .pages(0)
            .readingStatus(READING)
            .star(FOUR_HALF)
            .build();

        result.add(bookShelf);

        PageRequest pageRequest = PageRequest.of(0, 1, Sort.by("id").descending());
        PageImpl<BookShelf> bookShelves = new PageImpl<>(result, pageRequest, 1);
        SearchBookShelfByReadingStatus searchBookShelfByReadingStatus = new SearchBookShelfByReadingStatus(
            bookShelves);

        when(bookShelfService.takeBooksOutOfBookShelf(any(ReadingStatus.class), any(Pageable.class),
            any())).thenReturn(searchBookShelfByReadingStatus);

        mockMvc.perform(get("/v1/api/bookshelf/books")
                .header(AUTHORIZATION, JWT_TOKEN)
                .queryParam("readingStatus", "COMPLETE")
                .queryParam("size", "5")
                .queryParam("page", "1")
                .queryParam("sort", "id,DESC")
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("get-bookshelf-complete",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                requestParameters(
                    parameterWithName("readingStatus").description("COMPLETE"),
                    parameterWithName("size").description("page 당 size"),
                    parameterWithName("page").description("한번에 조회할 page수"),
                    parameterWithName("sort").description("등록순-id")
                ),
                responseFields(
                    fieldWithPath("contents[].bookId").type(NUMBER).description("Book Id"),
                    fieldWithPath("contents[].title").type(STRING).description("제목"),
                    fieldWithPath("contents[].isbn").type(STRING).description("ISBN"),
                    fieldWithPath("contents[].bookCoverImageUrl").type(STRING).optional()
                        .description("책 커버 이미지 URI"),
                    fieldWithPath("contents[].authors[]").type(ARRAY).description("저자"),
                    fieldWithPath("contents[].publisher").type(STRING).description("출판사"),
                    fieldWithPath("contents[].publishAt").type(STRING).description("출판일자"),
                    fieldWithPath("contents[].pages").type(NUMBER).description("현재 읽고 있는 페이지 번호"),
                    fieldWithPath("contents[].star").type(STRING).description("평점"))
                    .and(getPageField()))
            );

        verify(bookShelfService).takeBooksOutOfBookShelf(any(ReadingStatus.class),
            any(Pageable.class), any());
    }

    @Test
    void 읽을_책_조회_성공() throws Exception {
        List<BookShelf> result = new ArrayList<>();

        User user = User.builder().build();
        Book book = Book.builder()
            .id(1L)
            .isbn("12345")
            .title("effectiveJava")
            .authors(List.of("joshua"))
            .publisher("jpub")
            .bookCoverImageUrl("testBookCoverImageUrl")
            .publishAt(LocalDate.now())
            .build();

        BookShelf bookShelf = BookShelf.builder()
            .book(book)
            .user(user)
            .pages(0)
            .readingStatus(WISH)
            .star(null)
            .build();

        result.add(bookShelf);

        PageRequest pageRequest = PageRequest.of(0, 1, Sort.by("id").descending());
        PageImpl<BookShelf> bookShelves = new PageImpl<>(result, pageRequest, 1);
        SearchBookShelfByReadingStatus searchBookShelfByReadingStatus = new SearchBookShelfByReadingStatus(
            bookShelves);

        when(bookShelfService.takeBooksOutOfBookShelf(any(ReadingStatus.class), any(Pageable.class),
            any())).thenReturn(searchBookShelfByReadingStatus);

        mockMvc.perform(get("/v1/api/bookshelf/books")
                .header(AUTHORIZATION, JWT_TOKEN)
                .queryParam("readingStatus", "WISH")
                .queryParam("size", "5")
                .queryParam("page", "1")
                .queryParam("sort", "id,DESC")
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("get-bookshelf-wish",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                requestParameters(
                    parameterWithName("readingStatus").description("WISH"),
                    parameterWithName("size").description("page 당 size"),
                    parameterWithName("page").description("한번에 조회할 page수"),
                    parameterWithName("sort").description("등록순-id")
                ),
                responseFields(
                    fieldWithPath("contents[].bookId").type(NUMBER).description("Book Id"),
                    fieldWithPath("contents[].title").type(STRING).description("제목"),
                    fieldWithPath("contents[].isbn").type(STRING).description("ISBN"),
                    fieldWithPath("contents[].bookCoverImageUrl").type(STRING).optional()
                        .description("책 커버 이미지 URI"),
                    fieldWithPath("contents[].authors[]").type(ARRAY).description("저자"),
                    fieldWithPath("contents[].publisher").type(STRING).description("출판사"),
                    fieldWithPath("contents[].publishAt").type(STRING).description("출판일자"),
                    fieldWithPath("contents[].pages").type(NUMBER).description("현재 읽고 있는 페이지 번호"),
                    fieldWithPath("contents[].star").type(STRING).optional().description("평점"),
                    fieldWithPath("contents[].singleLineAssessment").type(STRING).optional()
                        .description("한 줄 평"))
                    .and(getPageField()))
            );

        verify(bookShelfService).takeBooksOutOfBookShelf(any(ReadingStatus.class),
            any(Pageable.class), any());
    }

    @Test
    void 현재_읽고있는_페이지_등록() throws Exception {
        ReviseBookShelfRequest reviseBookShelfRequest = ReviseBookShelfRequest.builder()
            .pages(Optional.of(137))
            .star(null)
            .readingStatus(READING)
            .build();

        mockMvc.perform(put("/v1/api/bookshelf/books/{bookId}", 1L)
                .header(AUTHORIZATION, JWT_TOKEN)
                .with(user(getUserPrincipal()))
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviseBookShelfRequest)))
            .andExpect(status().isOk())
            .andDo(document("put-bookshelf-pages",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                pathParameters(
                    parameterWithName("bookId").description("Book Id")
                ),
                requestFields(
                    fieldWithPath("pages").type(NUMBER).optional().description("현재 읽고 있는 페이지 번호"),
                    fieldWithPath("star").type(STRING).optional().description("현재 책의 별점"),
                    fieldWithPath("readingStatus").type(STRING).description("현재 책의 독서상태")
                )));

        verify(bookShelfService).reviseBookShelf(any(), any(), any());
    }

    @Test
    void 서재에_넣어둔_책_삭제_성공() throws Exception {
        mockMvc.perform(delete("/v1/api/bookshelf/books/{bookId}", 1)
                .header(AUTHORIZATION, JWT_TOKEN)
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(document("delete-bookshelf-books",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                pathParameters(
                    parameterWithName("bookId").description("Book Id")
                )
            ));

        verify(bookShelfService).deleteBookOnBookShelf(any(), any());
    }

    @Test
    void 독서예정_책_독서중으로_변경_성공() throws Exception {
        ReviseBookShelfRequest reviseBookShelfRequest = ReviseBookShelfRequest.builder()
            .pages(null)
            .star(null)
            .readingStatus(READING)
            .build();

        mockMvc.perform(put("/v1/api/bookshelf/books/{bookId}", 1)
                .header(AUTHORIZATION, JWT_TOKEN)
                .with(user(getUserPrincipal()))
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviseBookShelfRequest)))
            .andExpect(status().isOk())
            .andDo(document("put-bookshelf-status1",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                pathParameters(
                    parameterWithName("bookId").description("Book Id")
                ),
                requestFields(
                    fieldWithPath("pages").type(NUMBER).optional().description("현재 읽고 있는 페이지 번호"),
                    fieldWithPath("star").type(STRING).optional().description("현재 책의 별점"),
                    fieldWithPath("readingStatus").type(STRING).description("변경할 독서상태")
                )));

        verify(bookShelfService).reviseBookShelf(any(), any(), any());
    }

    @Test
    void 독서중_책_독서완료로_변경_성공() throws Exception {
        ReviseBookShelfRequest reviseBookShelfRequest = ReviseBookShelfRequest.builder()
            .pages(null)
            .star(Optional.of(FOUR_HALF))
            .readingStatus(COMPLETE)
            .build();

        mockMvc.perform(put("/v1/api/bookshelf/books/{bookId}", 1L)
                .header(AUTHORIZATION, JWT_TOKEN)
                .with(user(getUserPrincipal()))
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviseBookShelfRequest)))
            .andExpect(status().isOk())
            .andDo(document("put-bookshelf-status2",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                pathParameters(
                    parameterWithName("bookId").description("Book Id")
                ),
                requestFields(
                    fieldWithPath("pages").optional().description("현재 읽고 있는 페이지 번호"),
                    fieldWithPath("star").optional().description("변경할 별점"),
                    fieldWithPath("readingStatus").description("변경할 독서상태")
                )));

        verify(bookShelfService).reviseBookShelf(any(), any(), any());
    }

    @Test
    void 독서완료_책_별점_수정_성공() throws Exception {
        ReviseBookShelfRequest reviseBookShelfRequest = ReviseBookShelfRequest.builder()
            .pages(null)
            .star(Optional.of(FOUR_HALF))
            .readingStatus(COMPLETE)
            .build();

        mockMvc.perform(put("/v1/api/bookshelf/books/{bookId}", 1L)
                .header(AUTHORIZATION, JWT_TOKEN)
                .with(user(getUserPrincipal()))
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviseBookShelfRequest)))
            .andExpect(status().isOk())
            .andDo(document("put-bookshelf-star",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                pathParameters(
                    parameterWithName("bookId").description("Book Id")
                ),
                requestFields(
                    fieldWithPath("pages").optional().description("현재 읽고 있는 페이지 번호"),
                    fieldWithPath("star").optional().description("변경할 별점"),
                    fieldWithPath("readingStatus").description("현재 책의 독서상태")
                )));

        verify(bookShelfService).reviseBookShelf(any(), any(), any());
    }

    @Test
    void isbn과_출판일자로_서재에_책이_등록되었는지_조회_성공() throws Exception {
        ExistenceBookOnBookShelfResponse existenceBookOnBookShelfResponse = ExistenceBookOnBookShelfResponse.builder()
            .bookShelfId(1L)
            .bookId(1L)
            .readingStatus(WISH)
            .build();

        when(bookShelfService.getBookIfExisted(any(), any(), any())).thenReturn(
            existenceBookOnBookShelfResponse);
        mockMvc.perform(get("/v1/api/bookshelf/books/existence")
                .header(AUTHORIZATION, JWT_TOKEN)
                .param("isbn", "1234567891011 0123456789")
                .param("publishAt", LocalDate.now().toString()))
            .andExpect(status().isOk())
            .andDo(document("get-bookshelf-book-existence",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                requestParameters(
                    parameterWithName("isbn").description("ISBN 번호"),
                    parameterWithName("publishAt").description("출판일")
                ),
                responseFields(
                    fieldWithPath("bookShelfId").type(NUMBER).description("책이 등록된 서재 ID"),
                    fieldWithPath("bookId").type(NUMBER).description("책 ID"),
                    fieldWithPath("readingStatus").type(STRING).description("서재에 등록된 책의 현재 상태")
                )));

        verify(bookShelfService).getBookIfExisted(any(), any(), any());
    }
}
