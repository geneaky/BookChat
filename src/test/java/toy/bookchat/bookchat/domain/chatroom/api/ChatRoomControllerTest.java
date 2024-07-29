package toy.bookchat.bookchat.domain.chatroom.api;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import toy.bookchat.bookchat.db_module.book.BookEntity;
import toy.bookchat.bookchat.db_module.chat.ChatEntity;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomEntity;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.response.ChatRoomResponse;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.response.ChatRoomsResponseSlice;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.response.UserChatRoomResponse;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.response.UserChatRoomsResponseSlice;
import toy.bookchat.bookchat.domain.ControllerTestExtension;
import toy.bookchat.bookchat.domain.bookshelf.api.v1.request.BookRequest;
import toy.bookchat.bookchat.domain.chatroom.api.dto.response.UserChatRoomDetailResponse;
import toy.bookchat.bookchat.domain.chatroom.service.ChatRoomService;
import toy.bookchat.bookchat.domain.chatroom.service.dto.request.CreateChatRoomRequest;
import toy.bookchat.bookchat.domain.chatroom.service.dto.request.ReviseChatRoomRequest;
import toy.bookchat.bookchat.domain.chatroom.service.dto.response.CreatedChatRoomDto;
import toy.bookchat.bookchat.domain.participant.service.dto.RoomGuest;
import toy.bookchat.bookchat.domain.participant.service.dto.RoomHost;
import toy.bookchat.bookchat.domain.participant.service.dto.RoomSubHost;
import toy.bookchat.bookchat.domain.participant.service.dto.response.ChatRoomDetails;

@ChatRoomPresentationTest
class ChatRoomControllerTest extends ControllerTestExtension {

    public final String JWT_TOKEN = getTestToken();
    @MockBean
    private ChatRoomService chatRoomService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

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

        CreatedChatRoomDto createdChatRoomDto = CreatedChatRoomDto.builder()
            .roomId("1")
            .roomSid(UUID.randomUUID().toString())
            .roomImageUri("roomImage@s3.com")
            .build();

        when(chatRoomService.createChatRoom(any(), any(), any())).thenReturn(createdChatRoomDto);

        mockMvc.perform(multipart("/v1/api/chatrooms")
                .file(chatRoomImagePart)
                .file(createChatRoomRequestPart)
                .header(AUTHORIZATION, JWT_TOKEN)
                .with(user(getUserPrincipal())))
            .andExpect(status().isCreated())
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
                    fieldWithPath("bookRequest.bookCoverImageUrl").type(STRING).description("책 표지 이미지 url"),
                    fieldWithPath("bookRequest.authors").type(ARRAY).description("저자 목록"),
                    fieldWithPath("bookRequest.publishAt").type(STRING).description("출판일")
                ),
                responseHeaders(
                    headerWithName(LOCATION).description("채팅방 접속 Connection Url")
                )));

        verify(chatRoomService).createChatRoom(any(), any(), any());
    }

    @Test
    void 사용자의_채팅방_목록_조회_성공() throws Exception {
        BookEntity bookEntity1 = BookEntity.builder()
            .id(1L)
            .title("effective java")
            .bookCoverImageUrl("effectivejava@s3.com")
            .authors(List.of("Joshua", "JJU"))
            .build();

        BookEntity bookEntity2 = BookEntity.builder()
            .id(2L)
            .title("effective kotlin")
            .bookCoverImageUrl("effectivekotlin@s3.com")
            .authors(List.of("marcin mosckala"))
            .build();

        BookEntity bookEntity3 = BookEntity.builder()
            .id(3L)
            .title("toby spring")
            .bookCoverImageUrl("tobyspring@s3.com")
            .authors(List.of("21min"))
            .build();

        ChatRoomEntity chatRoomEntity1 = ChatRoomEntity.builder()
            .id(1L)
            .bookId(bookEntity1.getId())
            .roomName("이펙티브 자바 부수는 방")
            .roomSid("secret1")
            .roomSize(100)
            .defaultRoomImageType(1)
            .roomImageUri(null)
            .build();
        chatRoomEntity1.setCreatedAt(LocalDateTime.now());
        ChatRoomEntity chatRoomEntity2 = ChatRoomEntity.builder()
            .id(2L)
            .bookId(bookEntity2.getId())
            .roomName("이펙티브 코틀린 부수는 방")
            .roomSid("secret2")
            .roomSize(10)
            .defaultRoomImageType(4)
            .roomImageUri("testRoomImageUri")
            .build();
        chatRoomEntity2.setCreatedAt(LocalDateTime.now());
        ChatRoomEntity chatRoomEntity3 = ChatRoomEntity.builder()
            .id(3L)
            .bookId(bookEntity3.getId())
            .roomName("토비의 스프링 부수는 방")
            .roomSid("secret3")
            .roomSize(5)
            .defaultRoomImageType(3)
            .roomImageUri(null)
            .build();
        chatRoomEntity3.setCreatedAt(LocalDateTime.now());

        ChatEntity chatEntity1 = ChatEntity.builder()
            .id(1L)
            .chatRoomId(chatRoomEntity1.getId())
            .message("이펙티브 자바 부수는 방 입니다.")
            .build();

        ChatEntity chatEntity2 = ChatEntity.builder()
            .id(2L)
            .chatRoomId(chatRoomEntity2.getId())
            .message("이펙티브 코틀린 부수는 방 입니다.")
            .build();

        ChatEntity chatEntity3 = ChatEntity.builder()
            .id(3L)
            .chatRoomId(chatRoomEntity3.getId())
            .message("토비의 스프링 부수는 방 입니다.")
            .build();

        List<UserChatRoomResponse> result = List.of(getChatRoomResponse(chatRoomEntity1, chatEntity1, bookEntity1),
            getChatRoomResponse(chatRoomEntity2, chatEntity2, bookEntity2), getChatRoomResponse(chatRoomEntity3, chatEntity3, bookEntity3));

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by("id").descending());
        Slice<UserChatRoomResponse> slice = new SliceImpl<>(result, pageRequest, true);
        UserChatRoomsResponseSlice response = UserChatRoomsResponseSlice.of(slice);

        when(chatRoomService.getUserChatRooms(any(), any(), any(), any())).thenReturn(response);

        mockMvc.perform(get("/v1/api/users/chatrooms")
                .header(AUTHORIZATION, JWT_TOKEN)
                .with(user(getUserPrincipal()))
                .param("bookId", "1")
                .param("postCursorId", "0")
                .param("size", "3"))
            .andExpect(status().isOk())
            .andDo(document("get-user-chatrooms",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                requestParameters(
                    parameterWithName("bookId").optional().description("책 ID"),
                    parameterWithName("postCursorId").optional()
                        .description("마지막 커서 ID"),
                    parameterWithName("size").optional().description("페이지 사이즈")
                ),
                responseFields(
                    fieldWithPath("userChatRoomResponseList[].roomId").type(NUMBER).description("채팅방 ID"),
                    fieldWithPath("userChatRoomResponseList[].roomName").type(STRING).description("채팅방 이름"),
                    fieldWithPath("userChatRoomResponseList[].roomSid").type(STRING).description("채팅방 SID"),
                    fieldWithPath("userChatRoomResponseList[].roomMemberCount").type(NUMBER).description("채팅방 현재 인원수"),
                    fieldWithPath("userChatRoomResponseList[].defaultRoomImageType").type(NUMBER).description("기본 이미지 타입 번호"),
                    fieldWithPath("userChatRoomResponseList[].roomImageUri").optional().type(STRING).description("채팅방 이미지 URI"),
                    fieldWithPath("userChatRoomResponseList[].hostId").type(NUMBER).description("방장 ID"),
                    fieldWithPath("userChatRoomResponseList[].hostNickname").type(STRING).description("방장 닉네임"),
                    fieldWithPath("userChatRoomResponseList[].hostProfileImageUrl").optional().type(STRING).description("방장 프로필 이미지"),
                    fieldWithPath("userChatRoomResponseList[].hostDefaultProfileImageType").type(NUMBER).description("방장 기본 프로필 이미지 타입"),
                    fieldWithPath("userChatRoomResponseList[].bookTitle").type(STRING).description("책 제목"),
                    fieldWithPath("userChatRoomResponseList[].bookCoverImageUrl").type(STRING).description("책 커버 이미지"),
                    fieldWithPath("userChatRoomResponseList[].bookAuthors[]").type(ARRAY).description("책 저자"),
                    fieldWithPath("userChatRoomResponseList[].senderId").type(NUMBER).description("마지막 채팅 보낸 사람 ID"),
                    fieldWithPath("userChatRoomResponseList[].senderNickname").type(STRING).description("마지막 채팅 보낸 사람 닉네임"),
                    fieldWithPath("userChatRoomResponseList[].senderProfileImageUrl").optional().type(STRING).description("마지막 채팅 보낸 사람 프로필 이미지"),
                    fieldWithPath("userChatRoomResponseList[].senderDefaultProfileImageType").type(NUMBER).description("마지막 채팅 보낸 사람 기본 프로필 이미지 타입"),
                    fieldWithPath("userChatRoomResponseList[].lastChatId").type(NUMBER).description("마지막 채팅 ID"),
                    fieldWithPath("userChatRoomResponseList[].lastChatContent").type(STRING).description("마지막 채팅 내용"),
                    fieldWithPath("userChatRoomResponseList[].lastChatDispatchTime").type(STRING).description("마지막 채팅 발송 시간")
                ).and(getCursorField())));
    }

    @Test
    void 사용자_채팅방_상세_조회_성공() throws Exception {
        given(chatRoomService.getUserChatRoomDetails(1L, 1L))
            .willReturn(UserChatRoomDetailResponse.builder()
                .roomId(1L)
                .roomName("testRoom")
                .roomSid("testSid")
                .roomMemberCount(3L)
                .roomImageUri("testImageUri")
                .defaultRoomImageType(1)
                .build());

        mockMvc.perform(get("/v1/api/users/chatrooms/{roomId}", 1)
                .header(AUTHORIZATION, JWT_TOKEN)
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(document("get-user-chatroom-detail",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                pathParameters(
                    parameterWithName("roomId").description("Room Id")
                ),
                responseFields(
                    fieldWithPath("roomId").type(NUMBER).description("채팅방 ID"),
                    fieldWithPath("roomName").type(STRING).description("채팅방 이름"),
                    fieldWithPath("roomSid").type(STRING).description("채팅방 SID"),
                    fieldWithPath("roomMemberCount").type(NUMBER).description("채팅방 현재 인원수"),
                    fieldWithPath("roomImageUri").optional().type(STRING).description("채팅방 이미지 URI"),
                    fieldWithPath("defaultRoomImageType").type(NUMBER).description("기본 이미지 타입 번호")
                )
            ));
    }

    @Test
    void 전체_채팅방_목록에서_검색_성공() throws Exception {
        ChatRoomResponse chatRoomResponse1 = ChatRoomResponse.builder()
            .roomId(1L)
            .roomSid("Dhb")
            .roomName("WLMRXZ")
            .roomMemberCount(3L)
            .roomSize(100)
            .roomImageUri("n8QpVmc")
            .bookTitle("book1")
            .bookAuthors(List.of("author1", "author2", "author3"))
            .bookCoverImageUri("book1CoverImage@s3")
            .hostId(1L)
            .hostName("host1")
            .hostDefaultProfileImageType(1)
            .hostProfileImageUri("host1ProfileImage@s3")
            .defaultRoomImageType(1)
            .lastChatSenderId(1L)
            .lastChatId(1L)
            .lastChatMessage("lastChatMessage")
            .tags("tag1,tag2,tag3")
            .lastChatDispatchTime(LocalDateTime.now())
            .build();
        ChatRoomResponse chatRoomResponse2 = ChatRoomResponse.builder()
            .roomId(2L)
            .roomSid("1vaaPp")
            .roomName("R501")
            .roomImageUri("7jutu0i0")
            .bookTitle("book2")
            .bookAuthors(List.of("author4", "author5", "author6"))
            .bookCoverImageUri("book2CoverImage@s3")
            .hostId(2L)
            .hostName("host2")
            .hostDefaultProfileImageType(2)
            .hostProfileImageUri("host2ProfileImage@s3")
            .roomMemberCount(100L)
            .roomSize(100)
            .defaultRoomImageType(3)
            .lastChatSenderId(3L)
            .lastChatId(2L)
            .lastChatMessage("lastChatMessage2")
            .tags("tag4,tag2,tag3")
            .lastChatDispatchTime(LocalDateTime.now())
            .build();
        ChatRoomResponse chatRoomResponse3 = ChatRoomResponse.builder()
            .roomId(3L)
            .roomSid("3YzLGXR7")
            .roomName("86H8735E")
            .roomMemberCount(1000L)
            .roomSize(3000)
            .roomImageUri("sUzZNOV")
            .bookTitle("book3")
            .bookAuthors(List.of("author7", "author8", "author9"))
            .bookCoverImageUri("book3CoverImage@s3")
            .hostId(203L)
            .hostName("host3")
            .hostDefaultProfileImageType(3)
            .hostProfileImageUri("host3ProfileImage@s3")
            .defaultRoomImageType(2)
            .lastChatSenderId(8391L)
            .lastChatId(4L)
            .lastChatMessage("lastChatMessage3")
            .tags("tag1,tag5,tag6")
            .lastChatDispatchTime(LocalDateTime.now())
            .build();

        List<ChatRoomResponse> contents = List.of(chatRoomResponse1, chatRoomResponse2,
            chatRoomResponse3);

        Pageable pageable = PageRequest.of(0, 3);

        Slice<ChatRoomResponse> chatRoomResponses = new SliceImpl<>(contents, pageable, true);

        ChatRoomsResponseSlice response = ChatRoomsResponseSlice.of(chatRoomResponses);
        when(chatRoomService.getChatRooms(any(), any(), any())).thenReturn(response);

        mockMvc.perform(get("/v1/api/chatrooms")
                .header(AUTHORIZATION, JWT_TOKEN)
                .with(user(getUserPrincipal()))
                .queryParam("postCursorId", "0")
                .queryParam("size", "3")
                .queryParam("roomName", "effective")
                .queryParam("title", "effectiveJava")
                .queryParam("isbn", "12314-12414")
                .queryParam("tags", "test1,test2,test3"))
            .andExpect(status().isOk())
            .andDo(document("get-chatrooms",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                requestParameters(
                    parameterWithName("postCursorId").optional().description("마지막 커서 ID"),
                    parameterWithName("size").optional().description("페이지 사이즈"),
                    parameterWithName("roomName").optional().description("채팅방 이름"),
                    parameterWithName("title").optional().description("책 제목"),
                    parameterWithName("isbn").optional().description("책 ISBN"),
                    parameterWithName("tags").optional().description("채팅방 TAG")
                ),
                responseFields(
                    fieldWithPath("chatRoomResponseList[].roomId").type(NUMBER).description("채팅방 ID"),
                    fieldWithPath("chatRoomResponseList[].roomName").type(STRING).description("채팅방 이름"),
                    fieldWithPath("chatRoomResponseList[].roomSid").type(STRING).description("채팅방 SID"),
                    fieldWithPath("chatRoomResponseList[].bookTitle").type(STRING).description("책 제목"),
                    fieldWithPath("chatRoomResponseList[].bookCoverImageUri").type(STRING).description("책 커버 이미지 URI"),
                    fieldWithPath("chatRoomResponseList[].bookAuthors[]").type(ARRAY).description("책 저자"),
                    fieldWithPath("chatRoomResponseList[].hostId").type(NUMBER).description("방장 ID"),
                    fieldWithPath("chatRoomResponseList[].hostName").type(STRING).description("방장 닉네임"),
                    fieldWithPath("chatRoomResponseList[].hostDefaultProfileImageType").type(NUMBER).description("방장 기본 프로필이미지 타입"),
                    fieldWithPath("chatRoomResponseList[].hostProfileImageUri").type(STRING).description("방장 프로필이미지"),
                    fieldWithPath("chatRoomResponseList[].roomMemberCount").type(NUMBER).description("채팅방 현재 인원수"),
                    fieldWithPath("chatRoomResponseList[].roomSize").type(NUMBER).description("채팅방 정원"),
                    fieldWithPath("chatRoomResponseList[].defaultRoomImageType").type(NUMBER).description("기본 이미지 타입 번호"),
                    fieldWithPath("chatRoomResponseList[].roomImageUri").optional().type(STRING).description("채팅방 이미지 URI"),
                    fieldWithPath("chatRoomResponseList[].tags").optional().type(STRING).description("채팅방 TAG"),
                    fieldWithPath("chatRoomResponseList[].lastChatSenderId").type(NUMBER).description("마지막 채팅 보낸 사람 ID"),
                    fieldWithPath("chatRoomResponseList[].lastChatId").type(NUMBER).description("마지막 채팅 ID"),
                    fieldWithPath("chatRoomResponseList[].lastChatMessage").type(STRING).description("마지막 채팅 내용"),
                    fieldWithPath("chatRoomResponseList[].lastChatDispatchTime").type(STRING).description("마지막 채팅 발송 시간")
                ).and(getCursorField())));
    }

    @Test
    void 채팅방_세부정보_조회() throws Exception {
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

        ChatRoomDetails chatRoomDetails = ChatRoomDetails.builder()
            .roomSize(100)
            .roomTags(List.of("개발", "DB"))
            .roomName("test room name")
            .bookTitle("RealMySQL")
            .bookCoverImageUrl("realmysql@s3.com")
            .bookAuthors(List.of("author1", "author2"))
            .roomHost(roomHost)
            .roomSubHostList(List.of(roomSubHost1, roomSubHost2))
            .roomGuestList(List.of(roomGuest1, roomGuest2))
            .build();

        when(chatRoomService.getChatRoomDetails(any(), any())).thenReturn(chatRoomDetails);

        mockMvc.perform(get("/v1/api/chatrooms/{roomId}", 1)
                .header(AUTHORIZATION, JWT_TOKEN)
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(document("get-chatroom-details",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                pathParameters(
                    parameterWithName("roomId").description("Room Id")
                ),
                responseFields(
                    fieldWithPath("roomSize").type(NUMBER).description("채팅방 인원수"),
                    fieldWithPath("roomTags").type(ARRAY).description("채팅방 태그"),
                    fieldWithPath("roomName").type(STRING).description("채팅방 이름"),
                    fieldWithPath("bookTitle").type(STRING).description("책 제목"),
                    fieldWithPath("bookCoverImageUrl").type(STRING).description("책 커버 이미지 url"),
                    fieldWithPath("bookAuthors").type(ARRAY).description("책 저자"),
                    fieldWithPath("roomHost.id").type(NUMBER).description("방장 Id"),
                    fieldWithPath("roomHost.nickname").type(STRING).description("방장 닉네임"),
                    fieldWithPath("roomHost.profileImageUrl").type(STRING).optional().description("방장 프로필 이미지 url"),
                    fieldWithPath("roomHost.defaultProfileImageType").type(NUMBER).description("방장 기본 프로필 이미지 타입"),
                    fieldWithPath("roomSubHostList[].id").type(NUMBER).description("부방장 Id"),
                    fieldWithPath("roomSubHostList[].nickname").type(STRING).description("부방장 닉네임"),
                    fieldWithPath("roomSubHostList[].profileImageUrl").type(STRING).optional().description("부방장 프로필 이미지 url"),
                    fieldWithPath("roomSubHostList[].defaultProfileImageType").type(NUMBER).description("부방장 기본 프로필 이미지 타입"),
                    fieldWithPath("roomGuestList[].id").type(NUMBER).description("참여자 Id"),
                    fieldWithPath("roomGuestList[].nickname").type(STRING).description("참여자 닉네임"),
                    fieldWithPath("roomGuestList[].profileImageUrl").type(STRING).optional().description("참여자 프로필 이미지 url"),
                    fieldWithPath("roomGuestList[].defaultProfileImageType").type(NUMBER).description("참여자 기본 프로필 이미지 타입")
                )));
    }

    @Test
    void 채팅방_정보_수정() throws Exception {
        ReviseChatRoomRequest reviseChatRoomRequest = ReviseChatRoomRequest.builder()
            .roomId(1L)
            .roomSize(200)
            .roomName("changedRoomName")
            .tags(List.of("tag5", "tag6"))
            .build();

        MockMultipartFile chatRoomImagePart = new MockMultipartFile("chatRoomImage", "",
            "image/webp", "test".getBytes());
        MockMultipartFile reviseChatRoomRequestFile = new MockMultipartFile("reviseChatRoomRequest",
            "", APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsString(reviseChatRoomRequest).getBytes(UTF_8));

        mockMvc.perform(multipart("/v1/api/chatrooms/{roomId}", 1)
                .file(reviseChatRoomRequestFile)
                .file(chatRoomImagePart)
                .header(AUTHORIZATION, JWT_TOKEN)
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(document("post-chatroom-info",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                requestParts(
                    partWithName("reviseChatRoomRequest").description("채팅방 수정 폼"),
                    partWithName("chatRoomImage").optional().description("채팅방 이미지 [200x200].webp")
                ),
                requestPartFields("reviseChatRoomRequest",
                    fieldWithPath("roomId").type(NUMBER).description("채팅방 id"),
                    fieldWithPath("roomName").type(STRING).optional().description("채팅방 이름"),
                    fieldWithPath("roomSize").type(NUMBER).optional().description("채팅방 크기"),
                    fieldWithPath("tags").type(ARRAY).optional().description("채팅방 태그"))
            ));

        verify(chatRoomService).reviseChatRoom(any(), any(), any());
    }

    @Test
    void 채팅방_입장_성공() throws Exception {
        mockMvc.perform(post("/v1/api/enter/chatrooms/{roomId}", 1)
                .header(AUTHORIZATION, JWT_TOKEN)
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(document("post-enter-chatrooms",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                pathParameters(
                    parameterWithName("roomId").description("Room id")
                )));
    }

    @Test
    void 채팅방_퇴장_성공() throws Exception {
        mockMvc.perform(delete("/v1/api/leave/chatrooms/{roomId}", 1)
                .header(AUTHORIZATION, JWT_TOKEN)
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andDo(document("delete-leave-chatrooms",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                pathParameters(
                    parameterWithName("roomId").description("Room id")
                )));
    }

    private UserChatRoomResponse getChatRoomResponse(ChatRoomEntity chatRoomEntity, ChatEntity chatEntity, BookEntity bookEntity) {
        return UserChatRoomResponse.builder()
            .roomId(chatRoomEntity.getId())
            .roomSid(chatRoomEntity.getRoomSid())
            .roomName(chatRoomEntity.getRoomName())
            .roomMemberCount(2L)
            .defaultRoomImageType(1)
            .hostId(1L)
            .hostNickname("host 별명")
            .hostProfileImageUrl("host Profile Image Url")
            .hostDefaultProfileImageType(1)
            .bookTitle(bookEntity.getTitle())
            .bookCoverImageUrl(bookEntity.getBookCoverImageUrl())
            .bookAuthors(bookEntity.getAuthors())
            .senderId(1L)
            .senderNickname("sender Nickname")
            .senderProfileImageUrl("sender Profile Image Url")
            .senderDefaultProfileImageType(3)
            .lastChatId(chatEntity.getId())
            .lastChatContent(chatEntity.getMessage())
            .lastChatDispatchTime(LocalDateTime.now())
            .build();
    }
}