package toy.bookchat.bookchat.domain.chatroom.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static toy.bookchat.bookchat.domain.common.Status.ACTIVE;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import toy.bookchat.bookchat.db_module.book.BookEntity;
import toy.bookchat.bookchat.db_module.book.repository.BookRepository;
import toy.bookchat.bookchat.db_module.chat.ChatEntity;
import toy.bookchat.bookchat.db_module.chat.repository.ChatRepository;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.BookRequest;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomBlockedUserEntity;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomEntity;
import toy.bookchat.bookchat.domain.chatroom.api.dto.response.UserChatRoomDetailResponse;
import toy.bookchat.bookchat.db_module.chatroom.repository.ChatRoomBlockedUserRepository;
import toy.bookchat.bookchat.db_module.chatroom.repository.ChatRoomHashTagRepository;
import toy.bookchat.bookchat.db_module.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.db_module.chatroom.repository.HashTagRepository;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.response.ChatRoomResponse;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.response.ChatRoomsResponseSlice;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.response.UserChatRoomResponse;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.response.UserChatRoomsResponseSlice;
import toy.bookchat.bookchat.domain.chatroom.service.dto.request.ChatRoomRequest;
import toy.bookchat.bookchat.domain.chatroom.service.dto.request.CreateChatRoomRequest;
import toy.bookchat.bookchat.domain.chatroom.service.dto.request.ReviseChatRoomRequest;
import toy.bookchat.bookchat.db_module.participant.ParticipantEntity;
import toy.bookchat.bookchat.db_module.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.storage.ChatRoomStorageService;
import toy.bookchat.bookchat.db_module.user.UserEntity;
import toy.bookchat.bookchat.domain.user.service.UserReader;
import toy.bookchat.bookchat.exception.badrequest.chatroom.ChatRoomIsFullException;
import toy.bookchat.bookchat.exception.badrequest.chatroom.NotEnoughRoomSizeException;
import toy.bookchat.bookchat.exception.badrequest.participant.AlreadyParticipateException;
import toy.bookchat.bookchat.exception.forbidden.chatroom.BlockedUserInChatRoomException;
import toy.bookchat.bookchat.exception.notfound.chatroom.ChatRoomNotFoundException;
import toy.bookchat.bookchat.infrastructure.broker.MessagePublisher;
import toy.bookchat.bookchat.infrastructure.broker.message.NotificationMessage;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

    @Mock
    ChatRepository chatRepository;
    @Mock
    ChatRoomRepository chatRoomRepository;
    @Mock
    ChatRoomBlockedUserRepository chatRoomBlockedUserRepository;
    @Mock
    HashTagRepository hashTagRepository;
    @Mock
    ChatRoomHashTagRepository chatRoomHashTagRepository;
    @Mock
    BookRepository bookRepository;
    @Mock
    UserReader userReader;
    @Mock
    ParticipantRepository participantRepository;
    @Mock
    ChatRoomStorageService storageService;
    @Mock
    MessagePublisher messagePublisher;
    @InjectMocks
    ChatRoomService chatRoomService;

    private static BookRequest getBookRequest() {
        return BookRequest.builder()
            .isbn("123124")
            .title("effective java")
            .authors(List.of("joshua"))
            .publishAt(LocalDate.now())
            .build();
    }

    private static CreateChatRoomRequest getCreateChatRoomRequest(BookRequest bookRequest) {
        return CreateChatRoomRequest.builder()
            .roomSize(3)
            .roomName("test room")
            .hashTags(List.of("java", "joshua"))
            .bookRequest(bookRequest)
            .build();
    }

    @Test
    void 채팅방_이미지가_없을시_채팅방_생성_성공() throws Exception {
        BookRequest bookRequest = getBookRequest();
        CreateChatRoomRequest createChatRoomRequest = getCreateChatRoomRequest(bookRequest);

        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
            .id(1L)
            .roomSid("7D6")
            .roomImageUri("3wVp")
            .build();

        when(bookRepository.findByIsbnAndPublishAt(any(), any())).thenReturn(
            Optional.ofNullable(mock(BookEntity.class)));
        when(userReader.readUser(anyLong())).thenReturn(mock(UserEntity.class));
        when(chatRoomRepository.save(any())).thenReturn(chatRoomEntity);

        chatRoomService.createChatRoom(createChatRoomRequest, null, 1L);

        verify(chatRoomRepository).save(any());
        verify(hashTagRepository, times(2)).save(any());
        verify(chatRoomHashTagRepository, times(2)).save(any());
    }

    @Test
    void 채팅방_이미지가_있을시_채팅방_생성_성공() throws Exception {
        BookRequest bookRequest = getBookRequest();
        CreateChatRoomRequest createChatRoomRequest = getCreateChatRoomRequest(bookRequest);
        MultipartFile image = mock(MultipartFile.class);

        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
            .id(1L)
            .roomSid("7D6")
            .roomImageUri("3wVp")
            .build();

        when(bookRepository.findByIsbnAndPublishAt(any(), any())).thenReturn(
            Optional.ofNullable(mock(BookEntity.class)));
        when(userReader.readUser(anyLong())).thenReturn(mock(UserEntity.class));
        when(chatRoomRepository.save(any())).thenReturn(chatRoomEntity);

        chatRoomService.createChatRoom(createChatRoomRequest, image, 1L);

        verify(chatRoomRepository).save(any());
        verify(hashTagRepository, times(2)).save(any());
        verify(chatRoomHashTagRepository, times(2)).save(any());
        verify(storageService).upload(any(), any(), any());

    }

    @Test
    void 등록되지_않은_책으로_채팅방_생성시_책을_등록_후_생성한다() throws Exception {
        BookRequest bookRequest = getBookRequest();
        CreateChatRoomRequest createChatRoomRequest = getCreateChatRoomRequest(bookRequest);

        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
            .id(1L)
            .roomSid("7D6")
            .roomImageUri("3wVp")
            .build();

        when(userReader.readUser(anyLong())).thenReturn(mock(UserEntity.class));
        when(chatRoomRepository.save(any())).thenReturn(chatRoomEntity);

        chatRoomService.createChatRoom(createChatRoomRequest, null, 1L);

        verify(bookRepository).save(any());
        verify(chatRoomRepository).save(any());
        verify(hashTagRepository, times(2)).save(any());
        verify(chatRoomHashTagRepository, times(2)).save(any());
    }

    @Test
    void 사용자_채팅방_조회_성공() throws Exception {
        ChatRoomEntity chatRoomEntity1 = ChatRoomEntity.builder()
            .id(1L)
            .roomName("이펙티브 자바 부수는 방")
            .roomSid("secret1")
            .roomSize(100)
            .defaultRoomImageType(1)
            .roomImageUri(null)
            .build();
        chatRoomEntity1.setCreatedAt(LocalDateTime.now());
        ChatEntity chatEntity1 = ChatEntity.builder()
            .id(1L)
            .message("안녕")
            .chatRoomEntity(chatRoomEntity1)
            .build();
        chatEntity1.setCreatedAt(LocalDateTime.now());
        UserChatRoomResponse userChatRoomResponse = UserChatRoomResponse.builder()
            .roomId(chatRoomEntity1.getId())
            .roomSid(chatRoomEntity1.getRoomSid())
            .roomName(chatRoomEntity1.getRoomName())
            .roomMemberCount(1L)
            .defaultRoomImageType(chatRoomEntity1.getDefaultRoomImageType())
            .build();
        List<UserChatRoomResponse> result = List.of(userChatRoomResponse);
        PageRequest pageRequest = PageRequest.of(0, 1, Sort.by("id").descending());
        Slice<UserChatRoomResponse> slice = new SliceImpl<>(result, pageRequest, true);
        when(chatRoomRepository.findUserChatRoomsWithLastChat(any(), any(), any(), any())).thenReturn(slice);
        UserChatRoomsResponseSlice userChatRoomsResponseSlice = chatRoomService.getUserChatRooms(any(), any(), any(), any());

        assertThat(userChatRoomsResponseSlice).usingRecursiveComparison().isEqualTo(UserChatRoomsResponseSlice.of(slice));
    }

    @Test
    void 채팅방_조회_성공() throws Exception {
        ChatRoomResponse chatRoomResponse1 = ChatRoomResponse.builder()
            .roomId(1L)
            .roomSid("Dhb")
            .roomName("WLMRXZ")
            .roomMemberCount(3L)
            .roomImageUri("n8QpVmc")
            .bookTitle("book1")
            .bookAuthors(List.of("author1", "author2", "author3"))
            .bookCoverImageUri("book1CoverImage@s3")
            .hostId(1L)
            .hostName("host1")
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
            .roomMemberCount(100L)
            .bookTitle("book2")
            .bookAuthors(List.of("author4", "author5", "author6"))
            .bookCoverImageUri("book2CoverImage@s3")
            .hostId(2L)
            .hostName("host2")
            .hostProfileImageUri("host2ProfileImage@s3")
            .defaultRoomImageType(3)
            .lastChatSenderId(2L)
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
            .roomImageUri("sUzZNOV")
            .bookTitle("book3")
            .bookAuthors(List.of("author7", "author8", "author9"))
            .bookCoverImageUri("book3CoverImage@s3")
            .hostId(3L)
            .hostName("host3")
            .hostProfileImageUri("host3ProfileImage@s3")
            .defaultRoomImageType(2)
            .lastChatSenderId(3L)
            .lastChatId(4L)
            .lastChatMessage("lastChatMessage3")
            .tags("tag1,tag5,tag6")
            .lastChatDispatchTime(LocalDateTime.now())
            .build();

        List<ChatRoomResponse> contents = List.of(chatRoomResponse1, chatRoomResponse2,
            chatRoomResponse3);

        Pageable pageable = PageRequest.of(0, 3);

        Slice<ChatRoomResponse> chatRoomResponses = new SliceImpl<>(contents, pageable, true);
        when(chatRoomRepository.findChatRooms(any(), any(), any())).thenReturn(chatRoomResponses);
        ChatRoomsResponseSlice result = chatRoomService.getChatRooms(1L, mock(ChatRoomRequest.class), mock(Pageable.class));

        assertThat(result).isEqualTo(ChatRoomsResponseSlice.of(chatRoomResponses));
    }

    @Test
    void 차단당한_사용자라면_세부정보_조회_실패() throws Exception {
        given(chatRoomBlockedUserRepository.findByUserIdAndChatRoomId(any(), any())).willReturn(Optional.ofNullable(mock(ChatRoomBlockedUserEntity.class)));
        assertThatThrownBy(() -> chatRoomService.getChatRoomDetails(1L, 1L)).isInstanceOf(BlockedUserInChatRoomException.class);
    }

    @Test
    void 이미_폭파된_방의_세부정보_조회_실패() throws Exception {
        assertThatThrownBy(() -> chatRoomService.getChatRoomDetails(1L, 1L)).isInstanceOf(ChatRoomNotFoundException.class);
    }

    @Test
    void 채팅방_세부정보_조회_성공() throws Exception {
        given(chatRoomRepository.findById(any())).willReturn(Optional.ofNullable(mock(ChatRoomEntity.class)));
        chatRoomService.getChatRoomDetails(1L, 1L);
        verify(chatRoomRepository).findChatRoomDetails(any(), any());
    }

    @Test
    void 채팅방_이미지를_포함한_정보_수정() throws Exception {
        ReviseChatRoomRequest reviseChatRoomRequest = ChatRoomServiceTestFixture.createReviseChatRoomRequest(
            100);
        ChatRoomEntity chatRoomEntity = ChatRoomServiceTestFixture.getChatRoom();
        MockMultipartFile chatRoomImage = new MockMultipartFile("newImageFile", "newImageFile",
            "image/webp", "content".getBytes());

        when(chatRoomRepository.findChatRoomByIdAndHostId(any(), any())).thenReturn(
            Optional.of(chatRoomEntity));
        when(storageService.upload(eq(chatRoomImage), anyString(), anyString())).thenReturn(
            "newRoomImageUri");

        chatRoomService.reviseChatRoom(reviseChatRoomRequest, chatRoomImage, 763L);

        assertThat(chatRoomEntity.getRoomImageUri()).isEqualTo("newRoomImageUri");
    }

    @Test
    void 채팅방_이미지를_제외한_정보_수정() throws Exception {
        ReviseChatRoomRequest chatRoomRequest = ChatRoomServiceTestFixture.createReviseChatRoomRequest(
            100);

        ChatRoomEntity chatRoomEntity = ChatRoomServiceTestFixture.getChatRoom();

        when(chatRoomRepository.findChatRoomByIdAndHostId(any(), any())).thenReturn(
            Optional.of(chatRoomEntity));

        chatRoomService.reviseChatRoom(chatRoomRequest, null, 125L);

        assertThat(chatRoomEntity).extracting(ChatRoomEntity::getRoomName, ChatRoomEntity::getRoomSize)
            .containsExactly(chatRoomRequest.getRoomName(), chatRoomRequest.getRoomSize());
    }

    @Test
    void 채팅방_수정시_현재_방_크기보다_작은_크기로_변경시_예외발생() throws Exception {
        ReviseChatRoomRequest chatRoomRequest = ChatRoomServiceTestFixture.createReviseChatRoomRequest(
            50);

        ChatRoomEntity chatRoomEntity = ChatRoomServiceTestFixture.getChatRoom();

        when(chatRoomRepository.findChatRoomByIdAndHostId(any(), any())).thenReturn(
            Optional.of(chatRoomEntity));

        assertThatThrownBy(() -> {
            chatRoomService.reviseChatRoom(chatRoomRequest, null, 125L);
        }).isInstanceOf(NotEnoughRoomSizeException.class);
    }

    @Test
    void 차단된_사용자가_입장시도시_예외발생() throws Exception {
        UserEntity userEntity = mock(UserEntity.class);
        ChatRoomEntity chatRoomEntity = mock(ChatRoomEntity.class);
        ChatRoomBlockedUserEntity blockedUser = mock(ChatRoomBlockedUserEntity.class);

        when(userReader.readUser(anyLong())).thenReturn(userEntity);
        when(chatRoomRepository.findById(any())).thenReturn(Optional.ofNullable(chatRoomEntity));
        when(chatRoomBlockedUserRepository.findByUserIdAndChatRoomId(any(), any())).thenReturn(
            Optional.ofNullable(blockedUser));

        assertThatThrownBy(() -> {
            chatRoomService.enterChatRoom(69L, 988L);
        }).isInstanceOf(BlockedUserInChatRoomException.class);
    }

    @Test
    void 채팅방_인원수_가득_찼을_경우_예외발생() throws Exception {
        UserEntity userEntity = UserEntity.builder()
            .id(1L)
            .nickname("testNick")
            .profileImageUrl("testImage")
            .defaultProfileImageType(1)
            .status(ACTIVE)
            .build();

        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
            .id(1L)
            .roomSid("testRoomSid")
            .roomSize(3)
            .build();

        ChatEntity chatEntity = ChatEntity.builder()
            .userEntity(userEntity)
            .chatRoomEntity(chatRoomEntity)
            .message("test")
            .build();
        chatEntity.setCreatedAt(LocalDateTime.now());

        when(userReader.readUser(anyLong())).thenReturn(userEntity);
        when(chatRoomRepository.findById(any())).thenReturn(Optional.ofNullable(chatRoomEntity));
        when(participantRepository.findWithPessimisticLockByChatRoomEntity(any())).thenReturn(
            List.of(mock(ParticipantEntity.class), mock(ParticipantEntity.class), mock(ParticipantEntity.class)));
        assertThatThrownBy(() -> {
            chatRoomService.enterChatRoom(userEntity.getId(), chatRoomEntity.getId());
        }).isInstanceOf(ChatRoomIsFullException.class);
    }

    @Test
    void 이미_입장한_사용자는_중복_입장_실패() throws Exception {
        given(userReader.readUser(anyLong())).willReturn(mock(UserEntity.class));
        given(chatRoomRepository.findById(any())).willReturn(Optional.ofNullable(mock(ChatRoomEntity.class)));
        given(participantRepository.findByUserIdAndChatRoomId(any(), any())).willReturn(Optional.ofNullable(mock(ParticipantEntity.class)));

        assertThatThrownBy(() -> chatRoomService.enterChatRoom(1L, 1L)).isInstanceOf(AlreadyParticipateException.class);
    }

    @Test
    void 채팅방_입장_성공() throws Exception {
        UserEntity userEntity = UserEntity.builder()
            .id(1L)
            .nickname("testNick")
            .profileImageUrl("testImage")
            .defaultProfileImageType(1)
            .build();

        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
            .id(1L)
            .roomSid("testRoomSid")
            .roomSize(3)
            .build();

        ChatEntity chatEntity = ChatEntity.builder()
            .userEntity(userEntity)
            .chatRoomEntity(chatRoomEntity)
            .message("test")
            .build();
        chatEntity.setCreatedAt(LocalDateTime.now());

        when(userReader.readUser(anyLong())).thenReturn(userEntity);
        when(chatRoomRepository.findById(any())).thenReturn(Optional.ofNullable(chatRoomEntity));
        when(participantRepository.findWithPessimisticLockByChatRoomEntity(any())).thenReturn(
            new ArrayList<>());
        when(chatRepository.save(any())).thenReturn(chatEntity);
        chatRoomService.enterChatRoom(userEntity.getId(), chatRoomEntity.getId());

        verify(participantRepository).save(any());
        verify(chatRepository).save(any());
        verify(messagePublisher).sendNotificationMessage(anyString(),
            any(NotificationMessage.class));
    }

    @Test
    void 방장이아닌_참가자_채팅방_퇴장_성공() throws Exception {
        UserEntity userEntity = UserEntity.builder()
            .id(1L)
            .nickname("testNick")
            .profileImageUrl("testImage")
            .defaultProfileImageType(1)
            .build();

        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
            .id(1L)
            .roomSid("testRoomSid")
            .build();

        ParticipantEntity participantEntity = ParticipantEntity.builder()
            .id(1L)
            .chatRoomEntity(chatRoomEntity)
            .userEntity(userEntity)
            .build();

        ChatEntity chatEntity = ChatEntity.builder()
            .id(1L)
            .message("퇴장")
            .build();
        chatEntity.setCreatedAt(LocalDateTime.now());

        given(chatRoomRepository.findById(any())).willReturn(Optional.ofNullable(chatRoomEntity));
        when(participantRepository.findByUserIdAndChatRoomId(any(), any())).thenReturn(Optional.ofNullable(participantEntity));
        when(chatRepository.save(any())).thenReturn(chatEntity);
        chatRoomService.exitChatRoom(userEntity.getId(), chatRoomEntity.getId());

        verify(chatRepository).save(any());
        verify(participantRepository).delete(any());
        verify(messagePublisher).sendNotificationMessage(anyString(),
            any(NotificationMessage.class));
    }

    @Test
    void 방장의_채팅방_퇴장_성공() throws Exception {
        UserEntity userEntity = UserEntity.builder()
            .id(1L)
            .nickname("testNick")
            .profileImageUrl("testImage")
            .defaultProfileImageType(1)
            .build();

        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
            .id(1L)
            .roomSid("testRoomSid")
            .host(userEntity)
            .build();

        ParticipantEntity participantEntity = ParticipantEntity.builder()
            .userEntity(userEntity)
            .chatRoomEntity(chatRoomEntity)
            .build();

        ChatEntity chatEntity = ChatEntity.builder()
            .id(1L)
            .message("퇴장")
            .build();
        chatEntity.setCreatedAt(LocalDateTime.now());

        given(chatRoomRepository.findById(any())).willReturn(Optional.ofNullable(chatRoomEntity));
        when(participantRepository.findByUserIdAndChatRoomId(any(), any())).thenReturn(Optional.ofNullable(participantEntity));
        given(chatRepository.save(any())).willReturn(chatEntity);
        chatRoomService.exitChatRoom(userEntity.getId(), chatRoomEntity.getId());

        verify(chatRepository).save(any());
        verify(messagePublisher).sendNotificationMessage(anyString(), any(NotificationMessage.class));
    }

    @Test
    void 존재하지_않는_채팅방에서_나가기_실패() throws Exception {
        assertThatThrownBy(() -> chatRoomService.exitChatRoom(1L, 1L)).isInstanceOf(ChatRoomNotFoundException.class);
    }

    @Test
    void 사용자가_채팅방에_참여자가_아닌경우_채팅방_상세정보_조회_실패() throws Exception {
        assertThatThrownBy(() -> chatRoomService.getUserChatRoomDetails(1L, 1L)).isInstanceOf(ChatRoomNotFoundException.class);
    }

    @Test
    void 사용자_채팅방_상세정보_조회_성공() throws Exception {
        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
            .id(1L)
            .roomSid("WGmILQSqkZ")
            .roomName("Xo07yxaT")
            .roomImageUri("oTsF1I1bAR")
            .defaultRoomImageType(1)
            .build();

        given(chatRoomRepository.findUserChatRoom(any(), any())).willReturn(Optional.of(chatRoomEntity));
        given(participantRepository.countByChatRoomEntity(any())).willReturn(10L);

        UserChatRoomDetailResponse userChatRoomDetailResponse = chatRoomService.getUserChatRoomDetails(1L, 1L);

        assertThat(userChatRoomDetailResponse).extracting(
            UserChatRoomDetailResponse::getRoomId,
            UserChatRoomDetailResponse::getRoomName,
            UserChatRoomDetailResponse::getRoomSid,
            UserChatRoomDetailResponse::getRoomMemberCount,
            UserChatRoomDetailResponse::getRoomImageUri,
            UserChatRoomDetailResponse::getDefaultRoomImageType
        ).containsExactly(
            chatRoomEntity.getId(),
            chatRoomEntity.getRoomName(),
            chatRoomEntity.getRoomSid(),
            10L,
            chatRoomEntity.getRoomImageUri(),
            chatRoomEntity.getDefaultRoomImageType()
        );
    }

    private static class ChatRoomServiceTestFixture {

        public static ReviseChatRoomRequest createReviseChatRoomRequest(Integer roomSize) {
            return ReviseChatRoomRequest.builder()
                .roomId(1L)
                .roomName("afterRoomName")
                .roomSize(roomSize)
                .tags(List.of("tag4", "tag5"))
                .build();
        }

        public static ChatRoomEntity getChatRoom() {
            return ChatRoomEntity.builder()
                .roomName("beforeRoomName")
                .roomSize(100)
                .build();
        }
    }
}