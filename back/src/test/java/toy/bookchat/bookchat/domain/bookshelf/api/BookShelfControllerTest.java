package toy.bookchat.bookchat.domain.bookshelf.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static toy.bookchat.bookchat.domain.user.ROLE.USER;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

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

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Base64Utils;
import toy.bookchat.bookchat.config.OpenIdTokenConfig;
import toy.bookchat.bookchat.domain.AuthenticationTestExtension;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.Star;
import toy.bookchat.bookchat.domain.bookshelf.service.BookShelfService;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.BookShelfRequestDto;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.BookShelfSearchResponseDto;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.security.SecurityConfig;
import toy.bookchat.bookchat.security.openid.OpenIdTestUtil;
import toy.bookchat.bookchat.security.openid.OpenIdTokenManager;
import toy.bookchat.bookchat.security.user.UserPrincipal;

@WebMvcTest(controllers = BookShelfController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        SecurityConfig.class}))
@AutoConfigureRestDocs
public class BookShelfControllerTest extends AuthenticationTestExtension {

    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    OpenIdTokenManager openIdTokenManager;
    @MockBean
    OpenIdTokenConfig openIdTokenConfig;
    @MockBean
    UserRepository userRepository;
    @MockBean
    BookShelfService bookShelfService;
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
        User user = User.builder()
            .email("test@gmail.com")
            .name("testUser")
            .profileImageUrl("somethingImageUrl@naver.com")
            .build();

        return new UserPrincipal(1L, user.getEmail(), user.getName(), user.getProfileImageUrl(),
            authorities, user);
    }

    private BookShelfRequestDto getBookShelfRequestDto(ReadingStatus readingStatus) {
        return BookShelfRequestDto.builder()
            .isbn("124151214")
            .title("effectiveJava")
            .authors(List.of("Joshua"))
            .publisher("oreilly")
            .bookCoverImageUrl("bookCoverImage.com")
            .readingStatus(readingStatus)
            .build();
    }

    private String getTestToken() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        PrivateKey privateKey = getPrivateKey();
        String testToken = Jwts.builder()
                .setSubject("test")
                .setHeaderParam("kid", "abcedf")
                .setIssuer(" https://kauth.kakao.com")
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
        return testToken;
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
        when(openIdTokenConfig.getPublicKey(any(), any())).thenReturn(getPublicKey());
        when(userRepository.findByName(any())).thenReturn(Optional.ofNullable(getUser()));

        mockMvc.perform(post("/v1/api/bookshelf/books")
                .header("Authorization", "Bearer " + getTestToken())
                .header("provider_type", "KAKAO")
                .content(objectMapper.writeValueAsString(getBookShelfRequestDto(ReadingStatus.READING)))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isCreated())
            .andDo(document("bookshelf_reading",
                requestFields(fieldWithPath("isbn").description("isbn"),
                    fieldWithPath("title").description("title"),
                    fieldWithPath("authors.[]").description("authors"),
                    fieldWithPath("publisher").description("publisher"),
                    fieldWithPath("bookCoverImageUrl").description("bookCoverImageUrl"),
                    fieldWithPath("readingStatus").description("READING"))));

        verify(bookShelfService).putBookOnBookShelf(any(BookShelfRequestDto.class),
            any(User.class));
    }

    @Test
    public void 읽은_책_등록_성공() throws Exception {
        when(openIdTokenConfig.getPublicKey(any(), any())).thenReturn(getPublicKey());
        when(userRepository.findByName(any())).thenReturn(Optional.ofNullable(getUser()));

        mockMvc.perform(post("/v1/api/bookshelf/books")
                .header("Authorization", "Bearer " + getTestToken())
                .header("provider_type", "KAKAO")
                .content(
                    objectMapper.writeValueAsString(getBookShelfRequestDto(ReadingStatus.COMPLETE)))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isCreated())
            .andDo(document("bookshelf_complete",
                requestFields(fieldWithPath("isbn").description("isbn"),
                    fieldWithPath("title").description("title"),
                    fieldWithPath("authors.[]").description("authors"),
                    fieldWithPath("publisher").description("publisher"),
                    fieldWithPath("bookCoverImageUrl").description("bookCoverImageUrl"),
                    fieldWithPath("readingStatus").description("COMPLETE"))));

        verify(bookShelfService).putBookOnBookShelf(any(BookShelfRequestDto.class),
            any(User.class));
    }

    @Test
    public void 읽을_책_등록_성공() throws Exception {
        when(openIdTokenConfig.getPublicKey(any(), any())).thenReturn(getPublicKey());
        when(userRepository.findByName(any())).thenReturn(Optional.ofNullable(getUser()));

        mockMvc.perform(post("/v1/api/bookshelf/books")
                .header("Authorization", "Bearer " + getTestToken())
                .header("provider_type", "KAKAO")
                .content(objectMapper.writeValueAsString(getBookShelfRequestDto(ReadingStatus.WISH)))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isCreated())
            .andDo(document("bookshelf_wish",
                requestFields(fieldWithPath("isbn").description("isbn"),
                    fieldWithPath("title").description("title"),
                    fieldWithPath("authors.[]").description("author"),
                    fieldWithPath("publisher").description("publisher"),
                    fieldWithPath("bookCoverImageUrl").description("bookCoverImageUrl"),
                    fieldWithPath("readingStatus").description("WISH"))));

        verify(bookShelfService).putBookOnBookShelf(any(BookShelfRequestDto.class),
            any(User.class));
    }

    @Test
    public void null로_요청_실패() throws Exception {
        when(openIdTokenConfig.getPublicKey(any(), any())).thenReturn(getPublicKey());
        when(userRepository.findByName(any())).thenReturn(Optional.ofNullable(getUser()));

        mockMvc.perform(post("/v1/api/bookshelf/books")
                .header("Authorization", "Bearer " + getTestToken())
                .header("provider_type", "KAKAO")
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void 존재하지않은_readingstatus_책_등록_실패() throws Exception {
        when(openIdTokenConfig.getPublicKey(any(), any())).thenReturn(getPublicKey());
        when(userRepository.findByName(any())).thenReturn(Optional.ofNullable(getUser()));

        BookShelfTestRequestDto bookShelfTestRequestDto = new BookShelfTestRequestDto("124151214",
            "effectiveJava", List.of("Joshua"), "oreilly",
            "bookCoverImage.com", "NOT_EXISTED_READING_STATUS");

        mockMvc.perform(post("/v1/api/bookshelf/books")
                .header("Authorization", "Bearer " + getTestToken())
                .header("provider_type", "KAKAO")
                .content(objectMapper.writeValueAsString(bookShelfTestRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void readingStatus_없이_요청_실패() throws Exception {
        when(openIdTokenConfig.getPublicKey(any(), any())).thenReturn(getPublicKey());
        when(userRepository.findByName(any())).thenReturn(Optional.ofNullable(getUser()));

        BookShelfRequestDto bookShelfRequestDto = BookShelfRequestDto.builder()
            .isbn("135135414")
            .title("effectiveJava")
            .authors(List.of("Joshua"))
            .publisher("oreilly")
            .bookCoverImageUrl("bookCoverImage.com")
            .build();

        mockMvc.perform(post("/v1/api/bookshelf/books")
                .header("Authorization", "Bearer " + getTestToken())
                .header("provider_type", "KAKAO")
                .content(objectMapper.writeValueAsString(bookShelfRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());

    }

    @Test
    public void isbn_없이_책_등록_요청_실패() throws Exception {
        when(openIdTokenConfig.getPublicKey(any(), any())).thenReturn(getPublicKey());
        when(userRepository.findByName(any())).thenReturn(Optional.ofNullable(getUser()));

        BookShelfRequestDto bookShelfRequestDto = BookShelfRequestDto.builder()
            .title("effectiveJava")
            .authors(List.of("Joshua"))
            .publisher("oreilly")
            .bookCoverImageUrl("bookCoverImage.com")
            .readingStatus(ReadingStatus.WISH)
            .build();

        mockMvc.perform(post("/v1/api/bookshelf/books")
                .header("Authorization", "Bearer " + getTestToken())
                .header("provider_type", "KAKAO")
                .content(objectMapper.writeValueAsString(bookShelfRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void isbn_빈_문자열_요청_실패() throws Exception {
        when(openIdTokenConfig.getPublicKey(any(), any())).thenReturn(getPublicKey());
        when(userRepository.findByName(any())).thenReturn(Optional.ofNullable(getUser()));

        BookShelfRequestDto bookShelfRequestDto = BookShelfRequestDto.builder()
            .isbn("")
            .title("effectiveJava")
            .authors(List.of("Joshua"))
            .publisher("oreilly")
            .bookCoverImageUrl("bookCoverImage.com")
            .readingStatus(ReadingStatus.WISH)
            .build();

        mockMvc.perform(post("/v1/api/bookshelf/books")
                .header("Authorization", "Bearer " + getTestToken())
                .header("provider_type", "KAKAO")
                .content(objectMapper.writeValueAsString(bookShelfRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void 제목_없이_요청_실패() throws Exception {
        when(openIdTokenConfig.getPublicKey(any(), any())).thenReturn(getPublicKey());
        when(userRepository.findByName(any())).thenReturn(Optional.ofNullable(getUser()));

        BookShelfRequestDto bookShelfRequestDto = BookShelfRequestDto.builder()
            .isbn("124151214")
            .authors(List.of("Joshua"))
            .publisher("oreilly")
            .bookCoverImageUrl("bookCoverImage.com")
            .readingStatus(ReadingStatus.WISH)
            .build();

        mockMvc.perform(post("/v1/api/bookshelf/books")
                .header("Authorization", "Bearer " + getTestToken())
                .header("provider_type", "KAKAO")
                .content(objectMapper.writeValueAsString(bookShelfRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void 제목_빈_문자열_요청_실패() throws Exception {
        when(openIdTokenConfig.getPublicKey(any(), any())).thenReturn(getPublicKey());
        when(userRepository.findByName(any())).thenReturn(Optional.ofNullable(getUser()));

        BookShelfRequestDto bookShelfRequestDto = BookShelfRequestDto.builder()
            .isbn("124151214")
            .title("")
            .authors(List.of("Joshua"))
            .publisher("oreilly")
            .bookCoverImageUrl("bookCoverImage.com")
            .readingStatus(ReadingStatus.WISH)
            .build();

        mockMvc.perform(post("/v1/api/bookshelf/books")
                .header("Authorization", "Bearer " + getTestToken())
                .header("provider_type", "KAKAO")
                .content(objectMapper.writeValueAsString(bookShelfRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());

    }

    @Test
    public void 작가명_없이_요청_실패() throws Exception {
        when(openIdTokenConfig.getPublicKey(any(), any())).thenReturn(getPublicKey());
        when(userRepository.findByName(any())).thenReturn(Optional.ofNullable(getUser()));

        BookShelfRequestDto bookShelfRequestDto = BookShelfRequestDto.builder()
            .isbn("124151214")
            .title("effectiveJava")
            .publisher("oreilly")
            .bookCoverImageUrl("bookCoverImage.com")
            .readingStatus(ReadingStatus.WISH)
            .build();

        mockMvc.perform(post("/v1/api/bookshelf/books")
                .header("Authorization", "Bearer " + getTestToken())
                .header("provider_type", "KAKAO")
                .content(objectMapper.writeValueAsString(bookShelfRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());

    }

    @Test
    public void 작가명_빈_문자열_요청_실패() throws Exception {
        when(openIdTokenConfig.getPublicKey(any(), any())).thenReturn(getPublicKey());
        when(userRepository.findByName(any())).thenReturn(Optional.ofNullable(getUser()));

        BookShelfRequestDto bookShelfRequestDto = BookShelfRequestDto.builder()
            .isbn("124151214")
            .title("effectiveJava")
            .authors(List.of(""))
            .publisher("oreilly")
            .bookCoverImageUrl("bookCoverImage.com")
            .readingStatus(ReadingStatus.WISH)
            .build();

        mockMvc.perform(post("/v1/api/bookshelf/books")
                .header("Authorization", "Bearer " + getTestToken())
                .header("provider_type", "KAKAO")
                .content(objectMapper.writeValueAsString(bookShelfRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());

    }

    @Test
    public void 출판사_없이_요청_실패() throws Exception {
        when(openIdTokenConfig.getPublicKey(any(), any())).thenReturn(getPublicKey());
        when(userRepository.findByName(any())).thenReturn(Optional.ofNullable(getUser()));

        BookShelfRequestDto bookShelfRequestDto = BookShelfRequestDto.builder()
            .isbn("124151214")
            .title("effectiveJava")
            .authors(List.of("Joshua"))
            .bookCoverImageUrl("bookCoverImage.com")
            .readingStatus(ReadingStatus.WISH)
            .build();

        mockMvc.perform(post("/v1/api/bookshelf/books")
                .header("Authorization", "Bearer " + getTestToken())
                .header("provider_type", "KAKAO")
                .content(objectMapper.writeValueAsString(bookShelfRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());

    }

    @Test
    public void 출판사_빈_문자열_요청_실패() throws Exception {
        when(openIdTokenConfig.getPublicKey(any(), any())).thenReturn(getPublicKey());
        when(userRepository.findByName(any())).thenReturn(Optional.ofNullable(getUser()));

        BookShelfRequestDto bookShelfRequestDto = BookShelfRequestDto.builder()
            .isbn("124151214")
            .title("effectiveJava")
            .authors(List.of("Joshua"))
            .publisher("")
            .bookCoverImageUrl("bookCoverImage.com")
            .readingStatus(ReadingStatus.WISH)
            .build();

        mockMvc.perform(post("/v1/api/bookshelf/books")
                .header("Authorization", "Bearer " + getTestToken())
                .header("provider_type", "KAKAO")
                .content(objectMapper.writeValueAsString(bookShelfRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isBadRequest());

    }

    @Test
    public void 읽고있는_책_조회_성공() throws Exception {
        List<BookShelfSearchResponseDto> result = new ArrayList<>();

        BookShelfSearchResponseDto bookShelfSearchResponseDto = BookShelfSearchResponseDto.builder()
            .title("effectiveJava")
            .authors(List.of("joshua"))
            .bookCoverImageUrl("testBookCoverImage@naver.com")
            .publisher("jpub")
            .star(null)
            .singleLineAssessment(null).build();

        result.add(bookShelfSearchResponseDto);

        when(openIdTokenConfig.getPublicKey(any(), any())).thenReturn(getPublicKey());
        when(userRepository.findByName(any())).thenReturn(Optional.ofNullable(getUser()));
        when(bookShelfService.takeBooksOutOfBookShelf(any(ReadingStatus.class), any(Pageable.class),
            any(User.class))).thenReturn(result);

        mockMvc.perform(get("/v1/api/bookshelf/books")
                .header("Authorization", "Bearer " + getTestToken())
                .header("provider_type", "KAKAO")
                .queryParam("readingStatus", "READING")
                .queryParam("size", "5")
                .queryParam("page", "1")
                .queryParam("sort", "id,DESC")
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("get_bookshelf_reading",
                requestParameters(
                    parameterWithName("readingStatus").description("READING"),
                    parameterWithName("size").description("page 당 size"),
                    parameterWithName("page").description("한번에 조회할 page수"),
                    parameterWithName("sort").description("등록순-id")
                ))
            );

        verify(bookShelfService).takeBooksOutOfBookShelf(any(ReadingStatus.class),
            any(Pageable.class), any(User.class));
    }

    @Test
    public void 읽은_책_조회_성공() throws Exception {
        List<BookShelfSearchResponseDto> result = new ArrayList<>();

        BookShelfSearchResponseDto bookShelfSearchResponseDto = BookShelfSearchResponseDto.builder()
            .title("effectiveJava")
            .authors(List.of("joshua"))
            .bookCoverImageUrl("testBookCoverImage@naver.com")
            .publisher("jpub")
            .star(Star.FOUR_HALF)
            .singleLineAssessment("it's is best").build();

        result.add(bookShelfSearchResponseDto);

        when(openIdTokenConfig.getPublicKey(any(), any())).thenReturn(getPublicKey());
        when(userRepository.findByName(any())).thenReturn(Optional.ofNullable(getUser()));
        when(bookShelfService.takeBooksOutOfBookShelf(any(ReadingStatus.class), any(Pageable.class),
            any(User.class))).thenReturn(result);

        mockMvc.perform(get("/v1/api/bookshelf/books")
                .header("Authorization", "Bearer " + getTestToken())
                .header("provider_type", "KAKAO")
                .queryParam("readingStatus", "COMPLETE")
                .queryParam("size", "5")
                .queryParam("page", "1")
                .queryParam("sort", "id,DESC")
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("get_bookshelf_complete",
                requestParameters(
                    parameterWithName("readingStatus").description("COMPLETE"),
                    parameterWithName("size").description("page 당 size"),
                    parameterWithName("page").description("한번에 조회할 page수"),
                    parameterWithName("sort").description("등록순-id")
                ))
            );

        verify(bookShelfService).takeBooksOutOfBookShelf(any(ReadingStatus.class),
            any(Pageable.class), any(User.class));
    }

    @Test
    public void 읽을_책_조회_성공() throws Exception {
        List<BookShelfSearchResponseDto> result = new ArrayList<>();

        BookShelfSearchResponseDto bookShelfSearchResponseDto = BookShelfSearchResponseDto.builder()
            .title("effectiveJava")
            .authors(List.of("joshua"))
            .bookCoverImageUrl("testBookCoverImage@naver.com")
            .publisher("jpub")
            .star(null)
            .singleLineAssessment(null).build();

        result.add(bookShelfSearchResponseDto);

        when(openIdTokenConfig.getPublicKey(any(), any())).thenReturn(getPublicKey());
        when(userRepository.findByName(any())).thenReturn(Optional.ofNullable(getUser()));
        when(bookShelfService.takeBooksOutOfBookShelf(any(ReadingStatus.class), any(Pageable.class),
            any(User.class))).thenReturn(result);

        mockMvc.perform(get("/v1/api/bookshelf/books")
                .header("Authorization", "Bearer " + getTestToken())
                .header("provider_type", "KAKAO")
                .queryParam("readingStatus", "WISH")
                .queryParam("size", "5")
                .queryParam("page", "1")
                .queryParam("sort", "id,DESC")
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("get_bookshelf_wish",
                requestParameters(
                    parameterWithName("readingStatus").description("WISH"),
                    parameterWithName("size").description("page 당 size"),
                    parameterWithName("page").description("한번에 조회할 page수"),
                    parameterWithName("sort").description("등록순-id")
                ))
            );

        verify(bookShelfService).takeBooksOutOfBookShelf(any(ReadingStatus.class),
            any(Pageable.class), any(User.class));
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
