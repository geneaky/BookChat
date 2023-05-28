package toy.bookchat.bookchat.domain.scrap.api;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import toy.bookchat.bookchat.domain.ControllerTestExtension;
import toy.bookchat.bookchat.domain.scrap.service.dto.request.CreateScrapRequest;


@ScrapPresentationTest
class ScrapControllerTest extends ControllerTestExtension {

    public final String JWT_TOKEN = getTestToken();
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void scrap_저장_성공() throws Exception {

        CreateScrapRequest createScrapRequest = CreateScrapRequest.builder()
            .scrapContent("스크랩할 내용")
            .build();

        mockMvc.perform(post("/v1/api/scraps")
                .header(AUTHORIZATION, JWT_TOKEN)
                .content(objectMapper.writeValueAsString(createScrapRequest))
                .contentType(APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(document("post-scrap",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                requestFields(
                    fieldWithPath("scrapContent").type(STRING).description("스크랩 내용")
                )));
    }
}