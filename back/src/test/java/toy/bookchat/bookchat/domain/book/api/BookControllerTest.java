package toy.bookchat.bookchat.domain.book.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.*;
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
import java.util.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import toy.bookchat.bookchat.config.OpenIdTokenConfig;
import toy.bookchat.bookchat.domain.AuthenticationTestExtension;
import toy.bookchat.bookchat.domain.book.dto.BookDto;
import toy.bookchat.bookchat.domain.book.dto.BookSearchRequestDto;
import toy.bookchat.bookchat.domain.book.dto.BookSearchResponseDto;
import toy.bookchat.bookchat.domain.book.dto.Meta;
import toy.bookchat.bookchat.domain.book.exception.BookNotFoundException;
import toy.bookchat.bookchat.domain.book.service.BookSearchService;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.security.SecurityConfig;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenManager;
import toy.bookchat.bookchat.security.token.openid.OpenIdTokenManager;
import toy.bookchat.bookchat.security.user.UserPrincipal;

@WebMvcTest(controllers = BookController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        SecurityConfig.class}))
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "bookchat.link", uriPort = 443)
public class BookControllerTest extends AuthenticationTestExtension {


    @MockBean
    OpenIdTokenManager openIdTokenManager;

    @MockBean
    JwtTokenManager jwtTokenManager;

    @MockBean
    OpenIdTokenConfig openIdTokenConfig;
    @MockBean
    BookSearchService bookSearchService;
    @MockBean
    UserRepository userRepository;
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
            .name("testKakao")
            .role(USER)
            .profileImageUrl("somethingImageUrl@naver.com")
            .build();

        return new UserPrincipal(1L, user.getEmail(), user.getName(), user.getNickname(), user.getProfileImageUrl(), user.getDefaultProfileImageType(),
            authorities, user);

    }

    private User getUser() {
        return User.builder()
            .email("test@gmail.com")
            .role(USER)
            .name("testUser")
            .profileImageUrl("somethingImageUrl@naver.com")
            .build();
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

    private String getTestToken() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub","test");
        claims.put("name","google123");
        claims.put("provider", OAuth2Provider.GOOGLE);
        claims.put("email","test@gmail.com");

        String testToken = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, "test")
                .compact();

        return testToken;
    }


    @Test
    public void 로그인하지_않은_사용자_요청_401() throws Exception {
        mockMvc.perform(get("/v1/api/books")
                .param("isbn", "234134"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void 외부api_검색_요청_실패시_404() throws Exception {
        when(userRepository.findByName(any())).thenReturn(Optional.ofNullable(getUser()));
        when(bookSearchService.searchByQuery(any(BookSearchRequestDto.class))).thenThrow(
            BookNotFoundException.class);

        mockMvc.perform(get("/v1/api/books")
                 .header("Authorization", "Bearer " + getTestToken())
                .param("query", "effectiveJava")
                .with(user(getUserPrincipal())))
            .andExpect(status().isNotFound());
    }

    @Test
    public void 올바르지않은_요청으로_외부api_검색_요청시_400() throws Exception {
        when(userRepository.findByName(any())).thenReturn(Optional.ofNullable(getUser()));

        mockMvc.perform(get("/v1/api/books")
                        .header("Authorization", "Bearer " + getTestToken())
                        .param("query", " ")
                        .with(user(getUserPrincipal())))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void 사용자가_isbn_검색시_paging_성공() throws Exception {

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

        when(userRepository.findByName(any())).thenReturn(Optional.ofNullable(getUser()));
        when(bookSearchService.searchByQuery(any(BookSearchRequestDto.class))).thenReturn(
            bookSearchResponseDto);

        String result = objectMapper.writeValueAsString(bookSearchResponseDto);

        MvcResult mvcResult = mockMvc.perform(get("/v1/api/books")
                .header("Authorization", "Bearer " + getTestToken())
                .param("query", "1231513")
                .param("size", "5")
                .param("page", "1")
                .param("sort", "ACCURACY")
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(document("book-search-isbn-paging",
                requestHeaders(
                    headerWithName("Authorization").description("Bearer [JWT token]")),
                requestParameters(parameterWithName("query").description("isbn  번호"),
                    parameterWithName("size").description("한 번에 조회할 책의 수 - page 당 size"),
                    parameterWithName("page").description("한 번에 조회할 page 수"),
                    parameterWithName("sort").description("조회시 정렬 옵션")),
                responseFields(
                        fieldWithPath("bookDtos[].isbn").type(STRING).description("ISBN"),
                        fieldWithPath("bookDtos[].title").type(STRING).description("제목"),
                        fieldWithPath("bookDtos[].author[]").type(ARRAY).description("저자"),
                        fieldWithPath("bookDtos[].publisher").type(STRING).description("출판사"),
                        fieldWithPath("bookDtos[].bookCoverImageUrl").type(STRING).description("책 표지 이미지"),
                        fieldWithPath("meta.is_end").type(BOOLEAN).description("마지막 페이지 여부"),
                        fieldWithPath("meta.pageable_count").type(NUMBER).description("가져온 페이지 수"),
                        fieldWithPath("meta.total_count").type(NUMBER).description("총 페이지 수")
                ))).andReturn();

        verify(bookSearchService).searchByQuery(any(BookSearchRequestDto.class));
        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo(result);

    }

    @Test
    public void 사용자가_도서명_검색시_paging_성공() throws Exception {

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

        when(userRepository.findByName(any())).thenReturn(Optional.ofNullable(getUser()));
        when(bookSearchService.searchByQuery(any(BookSearchRequestDto.class))).thenReturn(
            bookSearchResponseDto);

        String result = objectMapper.writeValueAsString(bookSearchResponseDto);

        MvcResult mvcResult = mockMvc.perform(get("/v1/api/books")
                .header("Authorization", "Bearer " + getTestToken())
                .param("query", "effectiveJava")
                .param("size", "5")
                .param("page", "1")
                .param("sort", "LATEST")
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(document("book-search-title-paging",
                requestHeaders(
                    headerWithName("Authorization").description("Bearer [JWT token]")),
                requestParameters(parameterWithName("query").description("도서명"),
                    parameterWithName("size").description("한 번에 조회할 책의 수 - page 당 size"),
                    parameterWithName("page").description("한 번에 조회할 page 수"),
                    parameterWithName("sort").description("조회시 정렬 옵션")),
                responseFields(
                    fieldWithPath("bookDtos[].isbn").type(STRING).description("ISBN"),
                    fieldWithPath("bookDtos[].title").type(STRING).description("제목"),
                    fieldWithPath("bookDtos[].author[]").type(ARRAY).description("저자"),
                    fieldWithPath("bookDtos[].publisher").type(STRING).description("출판사"),
                    fieldWithPath("bookDtos[].bookCoverImageUrl").type(STRING).description("책 표지 이미지"),
                    fieldWithPath("meta.is_end").type(BOOLEAN).description("마지막 페이지 여부"),
                    fieldWithPath("meta.pageable_count").type(NUMBER).description("가져온 페이지 수"),
                    fieldWithPath("meta.total_count").type(NUMBER).description("총 페이지 수")
                ))).andReturn();

        verify(bookSearchService).searchByQuery(any(BookSearchRequestDto.class));
        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo(result);

    }

    @Test
    public void 사용자가_작가명_검색시_paging_성공() throws Exception {
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

        when(userRepository.findByName(any())).thenReturn(Optional.ofNullable(getUser()));
        when(bookSearchService.searchByQuery(any(BookSearchRequestDto.class))).thenReturn(
            bookSearchResponseDto);

        String result = objectMapper.writeValueAsString(bookSearchResponseDto);

        MvcResult mvcResult = mockMvc.perform(get("/v1/api/books")
                .header("Authorization", "Bearer " + getTestToken())
                .param("query", "Joshua")
                .param("size", "5")
                .param("page", "1")
                .param("sort", "LATEST")
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(document("book-search-author-paging",
                requestHeaders(
                    headerWithName("Authorization").description("Bearer [JWT token]")),
                requestParameters(parameterWithName("query").description("작가명"),
                    parameterWithName("size").description("한 번에 조회할 책의 수 - page 당 size"),
                    parameterWithName("page").description("한 번에 조회할 page 수"),
                    parameterWithName("sort").description("조회시 정렬 옵션")),
                responseFields(
                    fieldWithPath("bookDtos[].isbn").type(STRING).description("ISBN"),
                    fieldWithPath("bookDtos[].title").type(STRING).description("제목"),
                    fieldWithPath("bookDtos[].author[]").type(ARRAY).description("저자"),
                    fieldWithPath("bookDtos[].publisher").type(STRING).description("출판사"),
                    fieldWithPath("bookDtos[].bookCoverImageUrl").type(STRING).description("책 표지 이미지"),
                    fieldWithPath("meta.is_end").type(BOOLEAN).description("마지막 페이지 여부"),
                    fieldWithPath("meta.pageable_count").type(NUMBER).description("가져온 페이지 수"),
                    fieldWithPath("meta.total_count").type(NUMBER).description("총 페이지 수")
                ))).andReturn();

        verify(bookSearchService).searchByQuery(any(BookSearchRequestDto.class));
        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo(result);
    }
}
