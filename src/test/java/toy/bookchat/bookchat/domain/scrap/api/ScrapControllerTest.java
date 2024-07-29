package toy.bookchat.bookchat.domain.scrap.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
import toy.bookchat.bookchat.domain.scrap.api.v1.request.CreateScrapRequest;
import toy.bookchat.bookchat.domain.scrap.api.v1.response.ScrapResponse;
import toy.bookchat.bookchat.domain.scrap.api.v1.response.ScrapResponseSlice;
import toy.bookchat.bookchat.domain.scrap.service.ScrapService;


@ScrapPresentationTest
class ScrapControllerTest extends ControllerTestExtension {

  public final String JWT_TOKEN = getTestToken();
  @MockBean
  private ScrapService scrapService;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private MockMvc mockMvc;

  @Test
  void scrap_저장_성공() throws Exception {
    CreateScrapRequest createScrapRequest = CreateScrapRequest.builder()
        .bookShelfId(1L)
        .scrapContent("스크랩할 내용")
        .build();

    mockMvc.perform(post("/v1/api/scraps")
            .header(AUTHORIZATION, JWT_TOKEN)
            .content(objectMapper.writeValueAsString(createScrapRequest))
            .contentType(APPLICATION_JSON)
            .with(user(getUserPrincipal())))
        .andExpect(status().isCreated())
        .andDo(document("post-scrap",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                requestFields(
                    fieldWithPath("bookShelfId").type(NUMBER).description("서재 ID"),
                    fieldWithPath("scrapContent").type(STRING).description("스크랩 내용")
                ),
                responseHeaders(
                    headerWithName(LOCATION).description("생성된 스크랩 URI")
                )
            )
        );
  }

  @Test
  void scrap_조회_성공() throws Exception {
    ScrapResponse scrapResponse1 = ScrapResponse.builder()
        .scrapId(1L)
        .scrapContent("content1")
        .build();
    ScrapResponse scrapResponse2 = ScrapResponse.builder()
        .scrapId(2L)
        .scrapContent("content2")
        .build();
    ScrapResponse scrapResponse3 = ScrapResponse.builder()
        .scrapId(3L)
        .scrapContent("content3")
        .build();
    PageRequest pageRequest = PageRequest.of(0, 3, Sort.by("id").ascending());
    Slice<ScrapResponse> slice = new SliceImpl<>(
        List.of(scrapResponse1, scrapResponse2, scrapResponse3), pageRequest, true);
    ScrapResponseSlice scrapResponseSlice = ScrapResponseSlice.of(slice);

    when(scrapService.getScraps(any(), any(), any(), any())).thenReturn(
        scrapResponseSlice);

    mockMvc.perform(get("/v1/api/scraps")
            .header(AUTHORIZATION, JWT_TOKEN)
            .with(user(getUserPrincipal()))
            .param("bookShelfId", "1")
            .param("postCursorId", "5")
            .param("size", "3"))
        .andExpect(status().isOk())
        .andDo(document("get-scraps",
            requestHeaders(
                headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
            ),
            requestParameters(
                parameterWithName("bookShelfId").description("서재 Id"),
                parameterWithName("postCursorId").optional().description("마지막 커서 ID"),
                parameterWithName("size").optional().description("페이지 사이즈")
            ),
            responseFields(
                fieldWithPath("scrapResponseList[].scrapId").type(NUMBER).description("scrap id"),
                fieldWithPath("scrapResponseList[].scrapContent").type(STRING).description("scrap content")
            ).and(getCursorField())));

  }

  @Test
  void scarp_단_건_조회_성공() throws Exception {
    given(scrapService.getScrap(any(), any())).willReturn(
        ScrapResponse.builder()
            .scrapId(1L)
            .scrapContent("content1")
            .build()
    );

    mockMvc.perform(get("/v1/api/scraps/{scrapId}", 1)
            .header(AUTHORIZATION, JWT_TOKEN)
            .with(user(getUserPrincipal())))
        .andExpect(status().isOk())
        .andDo(document("get-scrap",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                pathParameters(
                    parameterWithName("scrapId").description("scrap id")
                ),
                responseFields(
                    fieldWithPath("scrapId").type(NUMBER).description("scrap id"),
                    fieldWithPath("scrapContent").type(STRING).description("scrap content")
                )
            )
        );

  }

  @Test
  void scrap_삭제_성공() throws Exception {
    mockMvc.perform(delete("/v1/api/scraps/{scrapIds}", "1, 2, 3")
            .header(AUTHORIZATION, JWT_TOKEN)
            .with(user(getUserPrincipal()))
            .param("bookShelfId", "1"))
        .andExpect(status().isOk())
        .andDo(document("delete-scrap",
            requestHeaders(
                headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
            ),
            requestParameters(
                parameterWithName("bookShelfId").description("서재 Id")
            ),
            pathParameters(
                parameterWithName("scrapIds").description("scrap id")
            )));
  }
}