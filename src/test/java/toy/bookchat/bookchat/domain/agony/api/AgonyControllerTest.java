package toy.bookchat.bookchat.domain.agony.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;
import toy.bookchat.bookchat.domain.ControllerTestExtension;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.agony.service.AgonyService;
import toy.bookchat.bookchat.domain.agony.service.dto.request.CreateBookAgonyRequest;
import toy.bookchat.bookchat.domain.agony.service.dto.request.ReviseAgonyRequest;
import toy.bookchat.bookchat.domain.agony.service.dto.response.AgonyResponse;
import toy.bookchat.bookchat.domain.agony.service.dto.response.SliceOfAgoniesResponse;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;

@AgonyPresentationTest
class AgonyControllerTest extends ControllerTestExtension {

    public final String JWT_TOKEN = getTestToken();

    @MockBean
    AgonyService agonyService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

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
        CreateBookAgonyRequest createBookAgonyRequest = CreateBookAgonyRequest.builder()
            .title("title")
            .hexColorCode("#062498")
            .build();
        given(agonyService.storeBookShelfAgony(any(), any(), any())).willReturn(1L);

        mockMvc.perform(post("/v1/api/bookshelves/{bookShelfId}/agonies", 1)
                .header(AUTHORIZATION, JWT_TOKEN)
                .with(user(getUserPrincipal()))
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createBookAgonyRequest)))
            .andExpect(status().isCreated())
            .andDo(document("post-agony",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                pathParameters(
                    parameterWithName("bookShelfId").description("BookShelf Id")
                ),
                requestFields(
                    fieldWithPath("title").type(STRING).description("고민 제목"),
                    fieldWithPath("hexColorCode").type(STRING).description("고민 폴더 색상")
                ),
                responseHeaders(
                    headerWithName(LOCATION).description("생성된 고민 폴더 URI")
                )
            ));

        verify(agonyService).storeBookShelfAgony(any(), any(), any());
    }

    @Test
    void 고민_조회_단_건_성공() throws Exception {
        AgonyResponse response = AgonyResponse.builder()
            .agonyId(1L)
            .title("고민1")
            .hexColorCode("빨강")
            .build();
        given(agonyService.searchAgony(any(), any(), any())).willReturn(response);

        mockMvc.perform(get("/v1/api/bookshelves/{bookShelfId}/agonies/{agonyId}", 1, 1)
                .header(AUTHORIZATION, JWT_TOKEN)
                .with(user(getUserPrincipal()))
                .contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("get-one-agony",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                pathParameters(
                    parameterWithName("bookShelfId").description("BookShelf Id"),
                    parameterWithName("agonyId").description("Agony Id")
                ),
                responseFields(
                    fieldWithPath("agonyId").type(NUMBER).description("고민 Id"),
                    fieldWithPath("title").type(STRING).description("고민 제목"),
                    fieldWithPath("hexColorCode").type(STRING).description("16진수 색상 코드")
                )));
    }

    @Test
    void 고민_조회_성공() throws Exception {
        List<Agony> agonies = getAgonies();
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").descending());
        Slice<Agony> slice = new SliceImpl<>(agonies, pageRequest, true);
        SliceOfAgoniesResponse pageOfAgoniesResponse = new SliceOfAgoniesResponse(slice);
        when(agonyService.searchSliceOfAgonies(any(), any(), any(), any())).thenReturn(
            pageOfAgoniesResponse);
        mockMvc.perform(get("/v1/api/bookshelves/{bookShelfId}/agonies", 1)
                .header(AUTHORIZATION, JWT_TOKEN)
                .with(user(getUserPrincipal()))
                .queryParam("size", "2")
                .queryParam("sort", "id,DESC")
                .queryParam("postCursorId", "1"))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("get-agonies",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                pathParameters(
                    parameterWithName("bookShelfId").description("BookShelf Id")
                ),
                requestParameters(
                    parameterWithName("size").description("page 당 size"),
                    parameterWithName("sort").description("[최신순] - id,DESC | [등록순] - id,ASC"),
                    parameterWithName("postCursorId").optional()
                        .description("마지막 커서 ID")
                ),
                responseFields(
                    fieldWithPath("agonyResponseList[].agonyId").type(NUMBER).description("고민 Id"),
                    fieldWithPath("agonyResponseList[].title").type(STRING).description("고민 제목"),
                    fieldWithPath("agonyResponseList[].hexColorCode").type(STRING)
                        .description("16진수 색상 코드")
                ).and(getCursorField())
            ));
    }

    @Test
    void 고민_폴더_삭제_성공() throws Exception {
        mockMvc.perform(delete("/v1/api/bookshelves/{bookShelfId}/agonies/{agoniesIds}", 1, "1,2,3")
                .header(AUTHORIZATION, JWT_TOKEN)
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(document("delete-agony",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                pathParameters(
                    parameterWithName("bookShelfId").description("BookShelf Id"),
                    parameterWithName("agoniesIds").description("삭제할 고민폴더 ID")
                )));

        verify(agonyService).deleteAgony(any(), any(), any());
    }

    @Test
    void 고민_폴더_수정_성공() throws Exception {
        ReviseAgonyRequest reviseAgonyRequest = ReviseAgonyRequest.builder()
            .title("고민 바꾸기")
            .hexColorCode("보라색")
            .build();

        mockMvc.perform(put("/v1/api/bookshelves/{bookShelfId}/agonies/{agonyId}", 1L, 1L)
                .header(AUTHORIZATION, JWT_TOKEN)
                .with(user(getUserPrincipal()))
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviseAgonyRequest)))
            .andExpect(status().isOk())
            .andDo(document("put-agony",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                pathParameters(
                    parameterWithName("bookShelfId").description("BookShelf Id"),
                    parameterWithName("agonyId").description("Agony Id")
                ),
                requestFields(
                    fieldWithPath("title").type(STRING).description("고민 폴더 이름"),
                    fieldWithPath("hexColorCode").type(STRING).description("고민 폴더 색")
                )));

        verify(agonyService).reviseAgony(any(), any(), any(), any());
    }
}