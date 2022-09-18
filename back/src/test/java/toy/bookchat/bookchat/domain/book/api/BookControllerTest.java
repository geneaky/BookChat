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
import static toy.bookchat.bookchat.domain.user.ROLE.USER;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.Base64Utils;
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
import toy.bookchat.bookchat.security.openid.OpenIdTestUtil;
import toy.bookchat.bookchat.security.openid.OpenIdTokenManager;
import toy.bookchat.bookchat.security.user.UserPrincipal;

@WebMvcTest(controllers = BookController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        SecurityConfig.class}))
@AutoConfigureRestDocs
public class BookControllerTest extends AuthenticationTestExtension {


    @MockBean
    OpenIdTokenManager openIdTokenManager;

    @MockBean
    OpenIdTokenConfig openIdTokenConfig;
    @MockBean
    BookSearchService bookSearchService;
    @MockBean
    UserRepository userRepository;
    @Autowired
    ObjectMapper objectMapper;
    OpenIdTestUtil openIdTestUtil;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void init() throws FileNotFoundException {
        openIdTestUtil = new OpenIdTestUtil(
            "src/test/java/toy/bookchat/bookchat/security/openid/token_key.pem",
            "src/test/java/toy/bookchat/bookchat/security/openid/openidRSA256-public.pem");
    }

    private X509EncodedKeySpec getPublicPkcs8EncodedKeySpec(OpenIdTestUtil openIdTestUtil)
        throws IOException {
        String publicKey = openIdTestUtil.getPublicKey(9);
        byte[] decodePublicKey = Base64Utils.decode(publicKey.getBytes());
        return new X509EncodedKeySpec(decodePublicKey);
    }

    private PKCS8EncodedKeySpec getPrivatePkcs8EncodedKeySpec(OpenIdTestUtil openIdTestUtil)
        throws IOException {
        String privateKey = openIdTestUtil.getPrivateKey(28);
        byte[] decodePrivateKey = Base64Utils.decode(privateKey.getBytes());
        return new PKCS8EncodedKeySpec(
            decodePrivateKey);
    }

    private PublicKey getPublicKey()
        throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec = getPublicPkcs8EncodedKeySpec(openIdTestUtil);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
        return publicKey;
    }

    private PrivateKey getPrivateKey()
        throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec privateKeySpec = getPrivatePkcs8EncodedKeySpec(openIdTestUtil);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        return privateKey;
    }

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

        return new UserPrincipal(1L, user.getEmail(), user.getName(), user.getProfileImageUrl(),
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

    @Test
    public void 로그인하지_않은_사용자_요청_401() throws Exception {
        mockMvc.perform(get("/v1/api/books")
                .param("isbn", "234134"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void 사용자가_isbn으로_책_검색_요청시_성공() throws Exception {
        PrivateKey privateKey = getPrivateKey();
        PublicKey publicKey = getPublicKey();

        String testToken = Jwts.builder()
            .setSubject("test")
            .setHeaderParam("kid", "abcedf")
            .setIssuer(" https://kauth.kakao.com")
            .signWith(SignatureAlgorithm.RS256, privateKey)
            .compact();

        List<BookDto> bookDtos = new ArrayList<>();
        bookDtos.add(getBookDto("213123", "effectiveJava", List.of("Joshua")));
        BookSearchResponseDto bookSearchResponseDto = BookSearchResponseDto.builder()
            .bookDtos(bookDtos)
            .build();

        when(openIdTokenConfig.getPublicKey(any(), any())).thenReturn(publicKey);
        when(userRepository.findByName(any())).thenReturn(Optional.ofNullable(getUser()));
        when(bookSearchService.searchByIsbn(any(BookSearchRequestDto.class))).thenReturn(
            bookSearchResponseDto);

        String result = objectMapper.writeValueAsString(bookSearchResponseDto);

        MvcResult mvcResult = mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/api/books")
                .header("Authorization", "Bearer " + testToken)
                .header("provider_type", "kakao")
                .param("isbn", "1231513")
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(document("book-search-isbn",
                requestParameters(parameterWithName("isbn").description("isbn  번호"))
            ))
            .andReturn();

        verify(bookSearchService).searchByIsbn(any(BookSearchRequestDto.class));
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
        when(bookSearchService.searchByIsbn(any(BookSearchRequestDto.class))).thenThrow(
            BookNotFoundException.class);

        mockMvc.perform(get("/v1/api/books")
                .param("isbn", "123456")
                .with(user(getUserPrincipal())))
            .andExpect(status().isNotFound());
    }

    @Test
    public void 사용자가_도서명_검색_요청시_성공() throws Exception {
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
                requestParameters(parameterWithName("title").description("도서 제목"))))
            .andReturn();

        verify(bookSearchService).searchByTitle(any(BookSearchRequestDto.class));
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
        when(bookSearchService.searchByTitle(any(BookSearchRequestDto.class))).thenThrow(
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
                requestParameters(parameterWithName("author").description("작가"))))
            .andReturn();

        verify(bookSearchService).searchByAuthor(any(BookSearchRequestDto.class));
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
        when(bookSearchService.searchByAuthor(any(BookSearchRequestDto.class))).thenThrow(
            BookNotFoundException.class);

        mockMvc.perform(get("/v1/api/books")
                .param("author", "Joshua")
                .with(user(getUserPrincipal())))
            .andExpect(status().isNotFound());
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
                requestParameters(parameterWithName("isbn").description("isbn  번호"),
                    parameterWithName("size").description("한 번에 조회할 책의 수 - page 당 size"),
                    parameterWithName("page").description("한 번에 조회할 page 수"),
                    parameterWithName("sort").description("조회시 정렬 옵션"))
            )).andReturn();

        verify(bookSearchService).searchByIsbn(any(BookSearchRequestDto.class));
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
                requestParameters(parameterWithName("title").description("도서명"),
                    parameterWithName("size").description("한 번에 조회할 책의 수 - page 당 size"),
                    parameterWithName("page").description("한 번에 조회할 page 수"),
                    parameterWithName("sort").description("조회시 정렬 옵션"))
            )).andReturn();

        verify(bookSearchService).searchByTitle(any(BookSearchRequestDto.class));
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
                requestParameters(parameterWithName("author").description("작가명"),
                    parameterWithName("size").description("한 번에 조회할 책의 수 - page 당 size"),
                    parameterWithName("page").description("한 번에 조회할 page 수"),
                    parameterWithName("sort").description("조회시 정렬 옵션"))
            )).andReturn();

        verify(bookSearchService).searchByAuthor(any(BookSearchRequestDto.class));
        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo(result);

    }
}
