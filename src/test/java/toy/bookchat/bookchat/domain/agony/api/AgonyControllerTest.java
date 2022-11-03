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
import org.springframework.test.web.servlet.MockMvc;
import toy.bookchat.bookchat.domain.AuthenticationTestExtension;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.agony.AgonyRecord;
import toy.bookchat.bookchat.domain.agony.service.AgonyRecordService;
import toy.bookchat.bookchat.domain.agony.service.AgonyService;
import toy.bookchat.bookchat.domain.agony.service.dto.request.CreateAgonyRecordRequestDto;
import toy.bookchat.bookchat.domain.agony.service.dto.request.CreateBookAgonyRequestDto;
import toy.bookchat.bookchat.domain.agony.service.dto.response.PageOfAgoniesResponse;
import toy.bookchat.bookchat.domain.agony.service.dto.response.PageOfAgonyRecordsResponse;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.user.ReadingTaste;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.security.SecurityConfig;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.user.TokenPayload;
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

    private List<Agony> getAgonies() {
        Agony agony1 = Agony.builder()
            .id(1L)
            .title("고민1")
            .hexColorCode("빨강")
            .bookShelf(mock(BookShelf.class))
            .build();

        Agony agony2 = Agony.builder()
            .id(2L)
            .title("고민2")
            .hexColorCode("파랑")
            .bookShelf(mock(BookShelf.class))
            .build();

        return List.of(agony1, agony2);
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
            "title", "blabla");

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
                    fieldWithPath("content").optional().description("고민기록의 내용")
                )));

        verify(agonyRecordService).storeAgonyRecord(any(), any(), any(), any());
    }

    @Test
    void 고민_조회_성공() throws Exception {

        List<Agony> agonies = getAgonies();
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

    @Test
    void 고민_기록_조회_성공() throws Exception {

        AgonyRecord agonyRecord1 = mock(AgonyRecord.class);
        AgonyRecord agonyRecord2 = mock(AgonyRecord.class);

        when(agonyRecord1.getId()).thenReturn(1L);
        when(agonyRecord1.getTitle()).thenReturn("title1");
        when(agonyRecord1.getContent()).thenReturn("content1");
        when(agonyRecord1.getCreateTimeInYearMonthDayFormat()).thenReturn("2022-11-01");
        when(agonyRecord2.getId()).thenReturn(2L);
        when(agonyRecord2.getTitle()).thenReturn("title2");
        when(agonyRecord2.getContent()).thenReturn("content2");
        when(agonyRecord2.getCreateTimeInYearMonthDayFormat()).thenReturn("2022-11-01");

        List<AgonyRecord> list = List.of(agonyRecord1, agonyRecord2);
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by("id").descending());
        Page<AgonyRecord> page = new PageImpl<>(list, pageRequest, list.size());
        PageOfAgonyRecordsResponse pageOfAgonyRecordsResponse = new PageOfAgonyRecordsResponse(
            page);

        when(agonyRecordService.searchPageOfAgonyRecords(any(), any(), any(), any())).thenReturn(
            pageOfAgonyRecordsResponse);
        mockMvc.perform(get("/v1/api/bookshelf/books/{bookId}/agonies/{agonyId}/records", 1, 1)
                .header("Authorization", "Bearer " + getTestToken())
                .with(user(getUserPrincipal()))
                .queryParam("size", "3")
                .queryParam("page", "0")
                .queryParam("sort", "id,DESC"))
            .andExpect(status().isOk())
            .andDo(document("get-agonies-records",
                requestHeaders(
                    headerWithName("Authorization").description("Bearer [JWT token]")
                ),
                pathParameters(
                    parameterWithName("bookId").description("Book Id"),
                    parameterWithName("agonyId").description("Agony Id")
                ),
                requestParameters(
                    parameterWithName("size").description("page당 size"),
                    parameterWithName("page").description("출력을 시작할 페이지 번호"),
                    parameterWithName("sort").description("[최신순] - id,DESC | [등록순] - id,ASC")
                ),
                responseFields(
                    fieldWithPath("agonyRecordResponseList[].agonyRecordId").type(NUMBER)
                        .description("고민 기록 Id"),
                    fieldWithPath("agonyRecordResponseList[].agonyRecordTitle").type(STRING)
                        .description("고민 기록 제목"),
                    fieldWithPath("agonyRecordResponseList[].agonyRecordContent").type(STRING)
                        .description("고민 기록 내용"),
                    fieldWithPath("agonyRecordResponseList[].createdAt").type(STRING)
                        .description("고민 가장 최근 수정시간"),
                    fieldWithPath("totalElements").type(NUMBER).description("전체 ROW 수"),
                    fieldWithPath("totalPages").type(NUMBER).description("총 페이지 수"),
                    fieldWithPath("pageSize").type(NUMBER).description("요청한 페이지 사이즈"),
                    fieldWithPath("pageNumber").type(NUMBER).description("현재 페이지 번호"),
                    fieldWithPath("offset").type(NUMBER).description("ROW 시작 번호"),
                    fieldWithPath("first").type(BOOLEAN).description("시작 페이지 여부"),
                    fieldWithPath("last").type(BOOLEAN).description("마지막 페이지 여부"),
                    fieldWithPath("empty").type(BOOLEAN).description("content 비어있는지 여부")
                )));
    }


}