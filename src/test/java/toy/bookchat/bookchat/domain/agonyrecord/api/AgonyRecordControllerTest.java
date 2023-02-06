package toy.bookchat.bookchat.domain.agonyrecord.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
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
import toy.bookchat.bookchat.domain.agony.service.dto.request.CreateAgonyRecordRequest;
import toy.bookchat.bookchat.domain.agony.service.dto.request.ReviseAgonyRecordRequest;
import toy.bookchat.bookchat.domain.agony.service.dto.response.SliceOfAgonyRecordsResponse;
import toy.bookchat.bookchat.domain.agonyrecord.AgonyRecord;
import toy.bookchat.bookchat.domain.agonyrecord.service.AgonyRecordService;

@AgonyRecordPresentationTest
class AgonyRecordControllerTest extends ControllerTestExtension {

    public final String JWT_TOKEN = getTestToken();

    @MockBean
    AgonyRecordService agonyRecordService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void 생성된_고민에_고민기록_추가_성공() throws Exception {
        CreateAgonyRecordRequest createAgonyRecordRequest = new CreateAgonyRecordRequest(
            "title", "blabla");

        mockMvc.perform(post("/v1/api/bookshelves/{bookShelfId}/agonies/{agonyId}/records", 1, 1)
                .header(AUTHORIZATION, JWT_TOKEN)
                .with(user(getUserPrincipal()))
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAgonyRecordRequest)))
            .andExpect(status().isOk())
            .andDo(document("post-agonyrecord",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                pathParameters(
                    parameterWithName("bookShelfId").description("BookShelf Id"),
                    parameterWithName("agonyId").description("Agony Id")
                ),
                requestFields(
                    fieldWithPath("title").description("고민기록의 제목"),
                    fieldWithPath("content").optional().description("고민기록의 내용")
                )));

        verify(agonyRecordService).storeAgonyRecord(any(), any(), any(), any());
    }

    @Test
    void 고민_기록_조회_성공() throws Exception {
        AgonyRecord agonyRecord1 = AgonyRecord.builder()
            .id(2L)
            .title("title1")
            .content("content1")
            .build();
        agonyRecord1.setCreatedAt(LocalDateTime.now());
        AgonyRecord agonyRecord2 = AgonyRecord.builder()
            .id(3L)
            .title("title2")
            .content("content2")
            .build();
        agonyRecord2.setCreatedAt(LocalDateTime.now());

        List<AgonyRecord> list = List.of(agonyRecord1, agonyRecord2);
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").ascending());
        Slice<AgonyRecord> slice = new SliceImpl<>(list, pageRequest, false);
        SliceOfAgonyRecordsResponse pageOfAgonyRecordsResponse = new SliceOfAgonyRecordsResponse(
            slice);

        when(agonyRecordService.searchPageOfAgonyRecords(any(), any(), any(), any(),
            any())).thenReturn(
            pageOfAgonyRecordsResponse);
        mockMvc.perform(get("/v1/api/bookshelves/{bookShelfId}/agonies/{agonyId}/records", 1, 1)
                .header(AUTHORIZATION, JWT_TOKEN)
                .with(user(getUserPrincipal()))
                .queryParam("postCursorId", "1")
                .queryParam("size", "2")
                .queryParam("sort", "id,ASC"))
            .andExpect(status().isOk())
            .andDo(document("get-agonies-records",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                pathParameters(
                    parameterWithName("bookShelfId").description("BookShelf Id"),
                    parameterWithName("agonyId").description("Agony Id")
                ),
                requestParameters(
                    parameterWithName("postCursorId").optional()
                        .description("마지막 커서 ID"),
                    parameterWithName("size").description("page당 size"),
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
                        .description("고민 가장 최근 수정시간")
                ).and(getCursorField())));
    }

    @Test
    void 고민기록_삭제_성공() throws Exception {
        mockMvc.perform(
                delete("/v1/api/bookshelves/{bookShelfId}/agonies/{agonyId}/records/{recordId}", 1L, 1L,
                    1L)
                    .header(AUTHORIZATION, JWT_TOKEN)
                    .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(document("delete-agony-record",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                pathParameters(
                    parameterWithName("bookShelfId").description("BookShelf Id"),
                    parameterWithName("agonyId").description("Agony Id"),
                    parameterWithName("recordId").description("Record Id")
                )));

        verify(agonyRecordService).deleteAgonyRecord(any(), any(), any(), any());
    }

    @Test
    void 고민기록_수정_성공() throws Exception {
        ReviseAgonyRecordRequest reviseAgonyRecordRequest = ReviseAgonyRecordRequest.builder()
            .recordTitle("수정된 고민 기록 제목")
            .recordContent("수정된 고민 기록 내용")
            .build();

        mockMvc.perform(
                put("/v1/api/bookshelves/{bookShelfId}/agonies/{agonyId}/records/{recordId}", 1L, 1L,
                    1L)
                    .header(AUTHORIZATION, JWT_TOKEN)
                    .with(user(getUserPrincipal()))
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(reviseAgonyRecordRequest)))
            .andExpect(status().isOk())
            .andDo(document("put-agony-record",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                pathParameters(
                    parameterWithName("bookShelfId").description("BookShelf Id"),
                    parameterWithName("agonyId").description("Agony Id"),
                    parameterWithName("recordId").description("Record Id")
                ),
                requestFields(
                    fieldWithPath("recordTitle").type(STRING).description("고민 기록 제목"),
                    fieldWithPath("recordContent").type(STRING).description("고민 기록 내용")
                )));

        verify(agonyRecordService).reviseAgonyRecord(any(), any(), any(), any(), any());
    }
}