package toy.bookchat.bookchat.domain.agony.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static toy.bookchat.bookchat.domain.user.ROLE.USER;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import toy.bookchat.bookchat.domain.AuthenticationTestExtension;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.agony.service.AgonyRecordService;
import toy.bookchat.bookchat.domain.agony.service.AgonyService;
import toy.bookchat.bookchat.domain.agony.service.dto.CreateAgonyRecordRequestDto;
import toy.bookchat.bookchat.domain.agony.service.dto.CreateBookAgonyRequestDto;
import toy.bookchat.bookchat.domain.agony.service.dto.PageOfAgoniesResponse;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.security.SecurityConfig;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.user.UserPrincipal;

@WebMvcTest(controllers = AgonyController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        SecurityConfig.class}))
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "bookchat.link", uriPort = 443)
class AgonyControllerTest extends AuthenticationTestExtension {

    @MockBean
    AgonyService agonyService;
    @MockBean
    AgonyRecordService agonyRecordService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

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
    void 고민_생성_성공() throws Exception {
        CreateBookAgonyRequestDto createBookAgonyRequestDto = new CreateBookAgonyRequestDto("title",
            "#062498");
        mockMvc.perform(post("/v1/api/bookshelf/books/{bookId}/agonies", 1)
                .header("Authorization", "Bearer " + getTestToken())
                .with(user(getUserPrincipal()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createBookAgonyRequestDto)))
            .andExpect(status().isOk())
            .andDo(document("post-agony",
                requestHeaders(
                    headerWithName("Authorization").description("Bearer [JWT token]")
                ),
                pathParameters(
                    parameterWithName("bookId").description("Book Id")
                ),
                requestFields(
                    fieldWithPath("title").type(STRING).description("고민 제목"),
                    fieldWithPath("hexColorCode").type(STRING).description("고민 폴더 색상")
                )));

        verify(agonyService).storeBookAgony(any(), any(), any());
    }

    @Test
    void 생성된_고민에_고민기록_추가_성공() throws Exception {
        CreateAgonyRecordRequestDto createAgonyRecordRequestDto = new CreateAgonyRecordRequestDto(
            "title", "blabla", "#456234");

        mockMvc.perform(post("/v1/api/bookshelf/books/{bookId}/agonies/{agonyId}/records", 1, 1)
                .header("Authorization", "Bearer " + getTestToken())
                .with(user(getUserPrincipal()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAgonyRecordRequestDto)))
            .andExpect(status().isOk())
            .andDo(document("post-agonyrecord",
                requestHeaders(
                    headerWithName("Authorization").description("Bearer [JWT token]")
                ),
                pathParameters(
                    parameterWithName("bookId").description("Book Id"),
                    parameterWithName("agonyId").description("Agony Id")
                ),
                requestFields(
                    fieldWithPath("title").description("고민기록의 제목"),
                    fieldWithPath("content").optional().description("고민기록의 내용"),
                    fieldWithPath("hexColorCode").description("고민기록 색상코드")
                )));

        verify(agonyRecordService).storeAgonyRecord(any(), any(), any(), any());
    }

    @Test
    void 고민_조회_성공() throws Exception {

        List<Agony> agonies = List.of(new Agony(1L, "고민1", "빨강", mock(BookShelf.class)),
            new Agony(2L, "고민2", "파랑", mock(BookShelf.class)));
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").descending());
        Page<Agony> page = new PageImpl<>(agonies, pageRequest, 1);
        PageOfAgoniesResponse pageOfAgoniesResponse = new PageOfAgoniesResponse(page);
        when(agonyService.searchPageOfAgonies(any(), any(), any())).thenReturn(
            pageOfAgoniesResponse);
        mockMvc.perform(get("/v1/api/bookshelf/books/{bookId}/agonies", 1)
                .header("Authorization", "Bearer " + getTestToken())
                .with(user(getUserPrincipal()))
                .queryParam("size", "2")
                .queryParam("page", "0")
                .queryParam("sort", "id,DESC"))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("get-agonies",
                requestHeaders(
                    headerWithName("Authorization").description("Bearer [JWT token]")
                ),
                pathParameters(
                    parameterWithName("bookId").description("책 Id")
                ),
                requestParameters(
                    parameterWithName("size").description("page 당 size"),
                    parameterWithName("page").description("한번에 조회할 page수"),
                    parameterWithName("sort").description("[최신순] - id,DESC | [등록순] - id,ASC")
                ),
                responseFields(
                    fieldWithPath("agonyResponseList[].agonyId").type(NUMBER).description("고민 Id"),
                    fieldWithPath("agonyResponseList[].title").type(STRING).description("고민 제목"),
                    fieldWithPath("agonyResponseList[].hexColorCode").type(STRING)
                        .description("16진수 색상 코드"),
                    fieldWithPath("totalElements").type(NUMBER).description("전체 ROW 수"),
                    fieldWithPath("totalPages").type(NUMBER).description("총 페이지 수"),
                    fieldWithPath("pageSize").type(NUMBER).description("요청한 페이지 사이즈"),
                    fieldWithPath("pageNumber").type(NUMBER).description("현재 페이지 번호"),
                    fieldWithPath("offset").type(NUMBER).description("ROW 시작 번호"),
                    fieldWithPath("first").type(BOOLEAN).description("시작 페이지 여부"),
                    fieldWithPath("last").type(BOOLEAN).description("마지막 페이지 여부"),
                    fieldWithPath("empty").type(BOOLEAN).description("content 비어있는지 여부")
                )
            ));
    }
}