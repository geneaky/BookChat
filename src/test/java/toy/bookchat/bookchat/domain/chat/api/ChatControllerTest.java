package toy.bookchat.bookchat.domain.chat.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;
import toy.bookchat.bookchat.db_module.chat.ChatEntity;
import toy.bookchat.bookchat.domain.ControllerTestExtension;
import toy.bookchat.bookchat.domain.chat.api.dto.response.ChatDetailResponse;
import toy.bookchat.bookchat.domain.chat.api.dto.response.ChatSender;
import toy.bookchat.bookchat.domain.chat.service.ChatService;
import toy.bookchat.bookchat.domain.chat.service.dto.response.ChatRoomChatsResponse;
import toy.bookchat.bookchat.db_module.user.UserEntity;

@ChatPresentationTest
class ChatControllerTest extends ControllerTestExtension {

    public final String JWT_TOKEN = getTestToken();
    @MockBean
    private ChatService chatService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void 현재_채팅방_채팅내역_조회_성공() throws Exception {

        UserEntity aUserEntity = UserEntity.builder()
            .id(1L)
            .build();
        UserEntity bUserEntity = UserEntity.builder()
            .id(2L)
            .build();

        ChatEntity chatEntity1 = ChatEntity.builder()
            .id(1L)
            .userEntity(aUserEntity)
            .message("first chat")
            .build();
        chatEntity1.setCreatedAt(LocalDateTime.now());
        ChatEntity chatEntity2 = ChatEntity.builder()
            .id(2L)
            .userEntity(bUserEntity)
            .message("second chat")
            .build();
        chatEntity2.setCreatedAt(LocalDateTime.now());
        ChatEntity chatEntity3 = ChatEntity.builder()
            .id(3L)
            .userEntity(aUserEntity)
            .message("welcome")
            .build();
        chatEntity3.setCreatedAt(LocalDateTime.now());
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by("id").descending());
        SliceImpl<ChatEntity> chatSlice = new SliceImpl<>(List.of(chatEntity1, chatEntity2, chatEntity3), pageRequest,
            true);
        ChatRoomChatsResponse chatRoomChatsResponse = new ChatRoomChatsResponse(chatSlice);
        when(chatService.getChatRoomChats(any(), any(), any(), any())).thenReturn(
            chatRoomChatsResponse);
        mockMvc.perform(get("/v1/api/chatrooms/{roomId}/chats", 1)
                .header(AUTHORIZATION, JWT_TOKEN)
                .with(user(getUserPrincipal()))
                .queryParam("size", "2")
                .queryParam("sort", "id,DESC")
                .queryParam("postCursorId", "3"))
            .andExpect(status().isOk())
            .andDo(document("get-chatroom-chats",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                pathParameters(
                    parameterWithName("roomId").description("Room Id")
                ),
                requestParameters(
                    parameterWithName("size").description("contents 개수"),
                    parameterWithName("sort").description("[최신순] - id,DESC | [입력순] - id,ASC"),
                    parameterWithName("postCursorId").optional().description("마지막 커서 Id")
                ),
                responseFields(
                    fieldWithPath("chatResponseList[].chatId").type(NUMBER).description("채팅 Id"),
                    fieldWithPath("chatResponseList[].senderId").type(NUMBER).description("송신자 Id"),
                    fieldWithPath("chatResponseList[].message").type(STRING).description("메세지 내용"),
                    fieldWithPath("chatResponseList[].dispatchTime").type(STRING)
                        .description("메세지 발송 시간")
                ).and(getCursorField())
            ));
    }

    @Test
    void 채팅_상세_정보_조회() throws Exception {
        given(chatService.getChatDetail(1L, 1L)).willReturn(ChatDetailResponse.builder()
            .chatId(1L)
            .chatRoomId(1L)
            .message("first chat")
            .dispatchTime(LocalDateTime.now())
            .sender(ChatSender.builder()
                .id(1L)
                .nickname("test")
                .profileImageUrl("test-image-url.com")
                .defaultProfileImageType(1)
                .build())
            .build());

        mockMvc.perform(get("/v1/api/chats/{chatId}", 1)
                .header(AUTHORIZATION, JWT_TOKEN)
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(document("get-chats-detail",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                pathParameters(
                    parameterWithName("chatId").description("Chat Id")
                ),
                responseFields(
                    fieldWithPath("chatId").type(NUMBER).description("채팅 Id"),
                    fieldWithPath("chatRoomId").type(NUMBER).description("채팅방 Id"),
                    fieldWithPath("message").type(STRING).description("메세지 내용"),
                    fieldWithPath("dispatchTime").type(STRING).description("메세지 발송 시간"),
                    fieldWithPath("sender.id").type(NUMBER).description("송신자 Id"),
                    fieldWithPath("sender.nickname").type(STRING).description("송신자 닉네임"),
                    fieldWithPath("sender.profileImageUrl").optional().type(STRING).description("송신자 프로필 이미지 Url"),
                    fieldWithPath("sender.defaultProfileImageType").type(NUMBER).description("송신자 기본 프로필 이미지 타입")
                )
            ));
    }
}