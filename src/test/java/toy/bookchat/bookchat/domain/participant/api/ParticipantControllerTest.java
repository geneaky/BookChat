package toy.bookchat.bookchat.domain.participant.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import toy.bookchat.bookchat.domain.ControllerTestExtension;
import toy.bookchat.bookchat.domain.participant.service.ParticipantService;

@ParticipantPresentationTest
class ParticipantControllerTest extends ControllerTestExtension {

    public final String JWT_TOKEN = getTestToken();
    @MockBean
    private ParticipantService participantService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void 방장의_참여자_권한_주고뺏기_성공() throws Exception {

        mockMvc.perform(patch("/v1/api/chatrooms/{roomId}/participants/{userId}", 1L, 2L)
                .header(AUTHORIZATION, JWT_TOKEN)
                .with(user(getUserPrincipal()))
                .queryParam("participantStatus", "SUBHOST"))
            .andExpect(status().isOk())
            .andDo(document("patch-participant-right",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                pathParameters(
                    parameterWithName("roomId").description("Room Id"),
                    parameterWithName("userId").description("User Id")
                ),
                requestParameters(
                    parameterWithName("participantStatus").description("[HOST | SUBHOST | GUEST]")
                )
            ));

        verify(participantService).changeParticipantRights(any(), any(), any(), any());
    }

    @Test
    void 방장_부방장이_채팅방_게스트_강퇴_성공() throws Exception {

        mockMvc.perform(delete("/v1/api/chatrooms/{roomId}/participants/{userId}", 1L, 1L)
                .header(AUTHORIZATION, JWT_TOKEN)
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(document("delete-participant",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                pathParameters(
                    parameterWithName("roomId").description("Room Id"),
                    parameterWithName("userId").description("User Id")
                )));

        verify(participantService).deleteParticipant(any(), any(), any());
    }
}