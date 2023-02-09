package toy.bookchat.bookchat.domain.participant.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import toy.bookchat.bookchat.domain.ControllerTestExtension;
import toy.bookchat.bookchat.domain.participant.service.ParticipantService;
import toy.bookchat.bookchat.domain.participant.service.dto.ChatRoomParticipantsResponse;
import toy.bookchat.bookchat.domain.participant.service.dto.RoomGuest;
import toy.bookchat.bookchat.domain.participant.service.dto.RoomHost;
import toy.bookchat.bookchat.domain.participant.service.dto.RoomSubHost;

@ParticipantPresentationTest
class ParticipantControllerTest extends ControllerTestExtension {

    public final String JWT_TOKEN = getTestToken();
    @MockBean
    private ParticipantService participantService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void 채팅방_참여_인원_목록_조회() throws Exception {

        RoomHost roomHost = RoomHost.builder()
            .id(1L)
            .nickname("마스터")
            .profileImageUrl("test@s3.com")
            .defaultProfileImageType(1)
            .build();

        RoomSubHost roomSubHost1 = RoomSubHost.builder()
            .id(2L)
            .nickname("서브 마스터1")
            .defaultProfileImageType(2)
            .build();

        RoomSubHost roomSubHost2 = RoomSubHost.builder()
            .id(3L)
            .nickname("서브 마스터2")
            .profileImageUrl("subHost@s3.com")
            .defaultProfileImageType(3)
            .build();

        RoomGuest roomGuest1 = RoomGuest.builder()
            .id(4L)
            .nickname("게스트1")
            .defaultProfileImageType(4)
            .build();

        RoomGuest roomGuest2 = RoomGuest.builder()
            .id(5L)
            .nickname("게스트2")
            .profileImageUrl("guest@s3.com")
            .defaultProfileImageType(1)
            .build();

        ChatRoomParticipantsResponse chatRoomParticipantsResponse = ChatRoomParticipantsResponse.builder()
            .roomHost(roomHost)
            .roomSubHostList(List.of(roomSubHost1, roomSubHost2))
            .roomGuestList(List.of(roomGuest1, roomGuest2))
            .build();

        when(participantService.getChatRoomUsers(any(), any())).thenReturn(
            chatRoomParticipantsResponse);
        mockMvc.perform(get("/v1/api/chatrooms/{roomId}/participants", 1)
                .header(AUTHORIZATION, JWT_TOKEN)
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(document("get-chatroom-participants",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                pathParameters(
                    parameterWithName("roomId").description("Room Id")
                ),
                responseFields(
                    fieldWithPath("roomHost.id").type(NUMBER).description("방장 Id"),
                    fieldWithPath("roomHost.nickname").type(STRING).description("방장 닉네임"),
                    fieldWithPath("roomHost.profileImageUrl").type(STRING).optional()
                        .description("방장 프로필 이미지 url"),
                    fieldWithPath("roomHost.defaultProfileImageType").type(NUMBER)
                        .description("방장 기본 프로필 이미지 타입"),
                    fieldWithPath("roomSubHostList[].id").type(NUMBER).description("부방장 Id"),
                    fieldWithPath("roomSubHostList[].nickname").type(STRING).description("부방장 닉네임"),
                    fieldWithPath("roomSubHostList[].profileImageUrl").type(STRING).optional()
                        .description("부방장 프로필 이미지 url"),
                    fieldWithPath("roomSubHostList[].defaultProfileImageType").type(NUMBER)
                        .description("부방장 기본 프로필 이미지 타입"),
                    fieldWithPath("roomGuestList[].id").type(NUMBER).description("참여자 Id"),
                    fieldWithPath("roomGuestList[].nickname").type(STRING).description("참여자 닉네임"),
                    fieldWithPath("roomGuestList[].profileImageUrl").type(STRING).optional()
                        .description("참여자 프로필 이미지 url"),
                    fieldWithPath("roomGuestList[].defaultProfileImageType").type(NUMBER)
                        .description("참여자 기본 프로필 이미지 타입")
                )));
    }

    @Test
    void 방장의_참여자_권한_주고뺏기_성공() throws Exception {
        
    }
}