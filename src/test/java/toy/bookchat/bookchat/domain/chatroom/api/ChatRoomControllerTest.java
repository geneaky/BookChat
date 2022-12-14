package toy.bookchat.bookchat.domain.chatroom.api;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static toy.bookchat.bookchat.domain.common.AuthConstants.BEARER;
import static toy.bookchat.bookchat.domain.user.ROLE.USER;
import static toy.bookchat.bookchat.domain.user.ReadingTaste.ART;
import static toy.bookchat.bookchat.domain.user.ReadingTaste.DEVELOPMENT;
import static toy.bookchat.bookchat.security.oauth.OAuth2Provider.GOOGLE;
import static toy.bookchat.bookchat.security.oauth.OAuth2Provider.KAKAO;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import toy.bookchat.bookchat.domain.ControllerTestExtension;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.BookRequest;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.repository.query.dto.response.ChatRoomResponse;
import toy.bookchat.bookchat.domain.chatroom.repository.query.dto.response.ChatRoomsResponseSlice;
import toy.bookchat.bookchat.domain.chatroom.service.ChatRoomService;
import toy.bookchat.bookchat.domain.chatroom.service.dto.request.CreateChatRoomRequest;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPrincipal;

@ChatRoomPresentationTest
class ChatRoomControllerTest extends ControllerTestExtension {

    public static final String JWT_TOKEN = BEARER + getTestToken();

    @MockBean
    private ChatRoomService chatRoomService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    private static String getTestToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "test");
        claims.put("name", "google123");
        claims.put("provider", GOOGLE);
        claims.put("email", "test@gmail.com");

        return Jwts.builder()
            .setClaims(claims)
            .signWith(HS256, "test")
            .compact();
    }

    private User getUser() {
        return User.builder()
            .id(1L)
            .email("test@gmail.com")
            .nickname("nickname")
            .role(USER)
            .name("testUser")
            .profileImageUrl("somethingImageUrl@naver.com")
            .defaultProfileImageType(1)
            .provider(KAKAO)
            .readingTastes(List.of(DEVELOPMENT, ART))
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

    @Test
    void 채팅방_생성_성공() throws Exception {
        BookRequest bookRequest = BookRequest.builder()
            .isbn("124151214")
            .title("effective java")
            .publisher("insight")
            .bookCoverImageUrl("testImageUrl")
            .authors(List.of("joshua"))
            .publishAt(LocalDate.now())
            .build();

        CreateChatRoomRequest createChatRoomRequest = CreateChatRoomRequest.builder()
            .roomName("effective java 부수는 방")
            .roomSize(5)
            .defaultRoomImageType(1)
            .hashTags(List.of("Java", "스터디"))
            .bookRequest(bookRequest)
            .build();

        MockMultipartFile chatRoomImagePart = new MockMultipartFile("chatRoomImage", "",
            "image/webp", "test".getBytes());
        MockMultipartFile createChatRoomRequestPart = new MockMultipartFile("createChatRoomRequest",
            "", APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsString(createChatRoomRequest).getBytes(UTF_8));

        mockMvc.perform(multipart("/v1/api/chatrooms")
                .file(chatRoomImagePart)
                .file(createChatRoomRequestPart)
                .header(AUTHORIZATION, JWT_TOKEN)
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(document("post-chatroom",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                requestParts(
                    partWithName("createChatRoomRequest").description("채팅방 생성 폼"),
                    partWithName("chatRoomImage").optional().description("채팅방 이미지 [200x200].webp")
                ),
                requestPartFields("createChatRoomRequest",
                    fieldWithPath("roomName").type(STRING).description("채팅 방 이름"),
                    fieldWithPath("roomSize").type(NUMBER).description("채팅 방 인원 수"),
                    fieldWithPath("defaultRoomImageType").type(NUMBER).description("기본 이미지 타입 번호"),
                    fieldWithPath("hashTags").type(ARRAY).optional().description("해시 태그"),
                    fieldWithPath("bookRequest.isbn").type(STRING).description("ISBN 번호"),
                    fieldWithPath("bookRequest.title").type(STRING).description("책 제목"),
                    fieldWithPath("bookRequest.publisher").type(STRING).description("출판사"),
                    fieldWithPath("bookRequest.bookCoverImageUrl").type(STRING)
                        .description("책 표지 이미지 url"),
                    fieldWithPath("bookRequest.authors").type(ARRAY).description("저자 목록"),
                    fieldWithPath("bookRequest.publishAt").type(STRING).description("출판일")
                )));

        verify(chatRoomService).createChatRoom(any(), any(), any());
    }

    @Test
    void 사용자의_채팅방_목록_조회_성공() throws Exception {
        ChatRoom chatRoom1 = ChatRoom.builder()
            .id(1L)
            .roomName("이펙티브 자바 부수는 방")
            .roomSid("secret1")
            .roomSize(100)
            .defaultRoomImageType(1)
            .roomImageUri(null)
            .build();
        chatRoom1.setCreatedAt(LocalDateTime.now());
        Chat chat1 = Chat.builder()
            .id(1L)
            .message("안녕")
            .chatRoom(chatRoom1)
            .build();
        chat1.setCreatedAt(LocalDateTime.now());
        ChatRoom chatRoom2 = ChatRoom.builder()
            .id(2L)
            .roomName("이펙티브 코틀린 부수는 방")
            .roomSid("secret2")
            .roomSize(10)
            .defaultRoomImageType(4)
            .roomImageUri("testRoomImageUri")
            .build();
        chatRoom2.setCreatedAt(LocalDateTime.now());
        Chat chat2 = Chat.builder()
            .id(2L)
            .message("잘가")
            .chatRoom(chatRoom2)
            .build();
        chat2.setCreatedAt(LocalDateTime.now());
        ChatRoom chatRoom3 = ChatRoom.builder()
            .id(3L)
            .roomName("토비의 스프링 부수는 방")
            .roomSid("secret3")
            .roomSize(5)
            .defaultRoomImageType(3)
            .roomImageUri(null)
            .build();
        chatRoom3.setCreatedAt(LocalDateTime.now());
        Chat chat3 = Chat.builder()
            .id(3L)
            .message("이거 모르겠음")
            .chatRoom(chatRoom3)
            .build();
        chat3.setCreatedAt(LocalDateTime.now());
        ChatRoomResponse chatRoomResponse1 = getChatRoomResponse(chatRoom1, chat1);
        ChatRoomResponse chatRoomResponse2 = getChatRoomResponse(chatRoom2, chat2);
        ChatRoomResponse chatRoomResponse3 = getChatRoomResponse(chatRoom3, chat3);
        List<ChatRoomResponse> result = List.of(chatRoomResponse1, chatRoomResponse2,
            chatRoomResponse3);
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by("id").descending());
        Slice<ChatRoomResponse> slice = new SliceImpl<>(result, pageRequest, true);
        ChatRoomsResponseSlice response = ChatRoomsResponseSlice.of(slice);
        when(chatRoomService.getUserChatRooms(any(), any(), any())).thenReturn(response);
        mockMvc.perform(get("/v1/api/chatrooms")
                .header(AUTHORIZATION, JWT_TOKEN)
                .with(user(getUserPrincipal()))
                .param("postCursorId", "0")
                .param("size", "3"))
            .andExpect(status().isOk())
            .andDo(document("get-user-chatrooms",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                requestParameters(
                    parameterWithName("postCursorId").optional()
                        .description("마지막 커서 ID"),
                    parameterWithName("size").optional().description("페이지 사이즈")
                ),
                responseFields(
                    fieldWithPath("chatRoomResponseList[].roomId").type(NUMBER)
                        .description("채팅방 ID"),
                    fieldWithPath("chatRoomResponseList[].roomName").type(STRING)
                        .description("채팅방 이름"),
                    fieldWithPath("chatRoomResponseList[].roomSid").type(STRING)
                        .description("채팅방 SID"),
                    fieldWithPath("chatRoomResponseList[].roomMemberCount").type(NUMBER)
                        .description("채팅방 현재 인원수"),
                    fieldWithPath("chatRoomResponseList[].defaultRoomImageType").type(NUMBER)
                        .description("기본 이미지 타입 번호"),
                    fieldWithPath("chatRoomResponseList[].roomImageUri").optional().type(STRING)
                        .description("채팅방 이미지 URI"),
                    fieldWithPath("chatRoomResponseList[].lastChatId").type(NUMBER)
                        .description("마지막 채팅 ID"),
                    fieldWithPath("chatRoomResponseList[].lastActiveTime").type(STRING)
                        .description("마지막 채팅 활성 시간"),
                    fieldWithPath("chatRoomResponseList[].lastChatContent").type(STRING)
                        .description("마지막 채팅 내용")
                ).and(getCursorField())));
    }

    private ChatRoomResponse getChatRoomResponse(ChatRoom chatRoom, Chat chat) {
        return ChatRoomResponse.builder()
            .roomId(chatRoom.getId())
            .roomSid(chatRoom.getRoomSid())
            .roomName(chatRoom.getRoomName())
            .roomMemberCount(2L)
            .defaultRoomImageType(1)
            .lastChatId(chat.getId())
            .lastActiveTime(chat.getCreatedAt())
            .lastChatContent(chat.getMessage())
            .build();
    }
}