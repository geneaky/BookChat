package toy.bookchat.bookchat.domain.chatroom.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.toSlice;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.GUEST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.HOST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.SUBHOST;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import toy.bookchat.bookchat.db_module.book.BookEntity;
import toy.bookchat.bookchat.db_module.book.repository.BookRepository;
import toy.bookchat.bookchat.db_module.chat.ChatEntity;
import toy.bookchat.bookchat.db_module.chat.repository.ChatRepository;
import toy.bookchat.bookchat.db_module.chatroom.repository.ChatRoomBlockedUserRepository;
import toy.bookchat.bookchat.db_module.chatroom.repository.ChatRoomHashTagRepository;
import toy.bookchat.bookchat.db_module.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.db_module.chatroom.repository.HashTagRepository;
import toy.bookchat.bookchat.domain.RepositoryTest;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomBlockedUserEntity;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomEntity;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomHashTagEntity;
import toy.bookchat.bookchat.db_module.chatroom.HashTagEntity;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.response.ChatRoomResponse;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.response.UserChatRoomResponse;
import toy.bookchat.bookchat.domain.chatroom.service.dto.request.ChatRoomRequest;
import toy.bookchat.bookchat.db_module.participant.ParticipantEntity;
import toy.bookchat.bookchat.db_module.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.participant.service.dto.response.ChatRoomDetails;
import toy.bookchat.bookchat.db_module.user.UserEntity;
import toy.bookchat.bookchat.db_module.user.repository.UserRepository;
import toy.bookchat.bookchat.exception.notfound.pariticipant.ParticipantNotFoundException;

class ChatRoomEntityRepositoryTest extends RepositoryTest {

    @Autowired
    ChatRepository chatRepository;
    @Autowired
    ChatRoomRepository chatRoomRepository;
    @Autowired
    HashTagRepository hashTagRepository;
    @Autowired
    ChatRoomHashTagRepository chatRoomHashTagRepository;
    @Autowired
    ChatRoomBlockedUserRepository chatRoomBlockedUserRepository;
    @Autowired
    ParticipantRepository participantRepository;
    @Autowired
    BookRepository bookRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    void 채팅방_저장_성공() throws Exception {
        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
            .roomName("test room")
            .roomSize(5)
            .roomSid("test sid")
            .defaultRoomImageType(1)
            .build();

        ChatRoomEntity findChatRoomEntity = chatRoomRepository.save(chatRoomEntity);

        assertThat(findChatRoomEntity).isEqualTo(chatRoomEntity);
    }

    @Test
    void 사용자의_채팅방_커서없는경우_최근채팅방순_성공() throws Exception {
        UserEntity userEntity1 = UserEntity.builder().build();
        UserEntity userEntity2 = UserEntity.builder().build();
        userRepository.save(userEntity1);
        userRepository.save(userEntity2);

        BookEntity bookEntity1 = BookEntity.builder()
            .isbn("12342")
            .authors(List.of("author1", "author2", "author3"))
            .publishAt(LocalDate.now())
            .build();
        BookEntity bookEntity2 = BookEntity.builder()
            .isbn("123426")
            .authors(List.of("author4", "author5", "author6"))
            .publishAt(LocalDate.now())
            .build();
        bookRepository.save(bookEntity1);
        bookRepository.save(bookEntity2);

        ChatRoomEntity chatRoomEntity1 = ChatRoomEntity.builder()
            .bookEntity(bookEntity1)
            .host(userEntity1)
            .roomSid("KlV8")
            .roomSize(576)
            .defaultRoomImageType(1)
            .build();
        ChatRoomEntity chatRoomEntity2 = ChatRoomEntity.builder()
            .bookEntity(bookEntity2)
            .host(userEntity1)
            .roomSid("IwZrRxR5")
            .roomSize(110)
            .defaultRoomImageType(2)
            .build();
        ChatRoomEntity chatRoomEntity3 = ChatRoomEntity.builder()
            .bookEntity(bookEntity1)
            .host(userEntity2)
            .roomSid("Gmw9yDI4")
            .roomSize(591)
            .defaultRoomImageType(3)
            .build();
        chatRoomRepository.save(chatRoomEntity1);
        chatRoomRepository.save(chatRoomEntity2);
        chatRoomRepository.save(chatRoomEntity3);

        ChatRoomBlockedUserEntity chatRoomBlockedUserEntity = ChatRoomBlockedUserEntity.builder()
            .userEntity(userEntity1)
            .chatRoomEntity(chatRoomEntity3)
            .build();
        chatRoomBlockedUserRepository.save(chatRoomBlockedUserEntity);

        ParticipantEntity participantEntity1 = ParticipantEntity.builder().userEntity(userEntity1).chatRoomEntity(chatRoomEntity1)
            .participantStatus(HOST).build();
        ParticipantEntity participantEntity2 = ParticipantEntity.builder().userEntity(userEntity1).chatRoomEntity(chatRoomEntity2)
            .participantStatus(SUBHOST).build();
        ParticipantEntity participantEntity3 = ParticipantEntity.builder().userEntity(userEntity1).chatRoomEntity(chatRoomEntity3)
            .participantStatus(GUEST).build();
        ParticipantEntity participantEntity4 = ParticipantEntity.builder().userEntity(userEntity2).chatRoomEntity(chatRoomEntity3)
            .participantStatus(GUEST).build();
        participantRepository.save(participantEntity1);
        participantRepository.save(participantEntity2);
        participantRepository.save(participantEntity3);
        participantRepository.save(participantEntity4);

        ChatEntity chatEntity1 = ChatEntity.builder()
            .chatRoomEntity(chatRoomEntity1)
            .userEntity(userEntity1)
            .message("first chat in chatRoom1")
            .build();
        ChatEntity chatEntity2 = ChatEntity.builder()
            .chatRoomEntity(chatRoomEntity2)
            .userEntity(userEntity1)
            .message("first chat in chatRoom2")
            .build();
        ChatEntity chatEntity3 = ChatEntity.builder()
            .chatRoomEntity(chatRoomEntity3)
            .userEntity(userEntity1)
            .message("first chat in chatRoom3")
            .build();
        ChatEntity chatEntity4 = ChatEntity.builder()
            .chatRoomEntity(chatRoomEntity3)
            .userEntity(userEntity2)
            .message("second chat in chatRoom3")
            .build();
        chatRepository.save(chatEntity1);
        chatRepository.save(chatEntity2);
        chatRepository.save(chatEntity3);
        chatRepository.save(chatEntity4);

        UserChatRoomResponse userChatRoomResponse1 = UserChatRoomResponse.builder()
            .roomId(chatRoomEntity3.getId())
            .defaultRoomImageType(chatRoomEntity3.getDefaultRoomImageType())
            .roomSid(chatRoomEntity3.getRoomSid())
            .roomMemberCount(2L)
            .hostId(userEntity2.getId())
            .hostNickname(userEntity2.getNickname())
            .hostProfileImageUrl(userEntity2.getProfileImageUrl())
            .hostDefaultProfileImageType(userEntity2.getDefaultProfileImageType())
            .bookTitle(chatRoomEntity3.getBookTitle())
            .bookCoverImageUrl(chatRoomEntity3.getBookCoverImageUrl())
            .bookAuthors(chatRoomEntity3.getBookAuthors())
            .senderId(chatEntity4.getUserId())
            .senderNickname(chatEntity4.getUserNickname())
            .senderProfileImageUrl(chatEntity4.getUserProfileImageUrl())
            .senderDefaultProfileImageType(chatEntity4.getUserDefaultProfileImageType())
            .lastChatId(chatEntity4.getId())
            .lastChatContent(chatEntity4.getMessage())
            .lastChatDispatchTime(chatEntity4.getCreatedAt())
            .build();

        UserChatRoomResponse userChatRoomResponse2 = UserChatRoomResponse.builder()
            .roomId(chatRoomEntity2.getId())
            .defaultRoomImageType(chatRoomEntity2.getDefaultRoomImageType())
            .roomSid(chatRoomEntity2.getRoomSid())
            .roomMemberCount(1L)
            .hostId(userEntity1.getId())
            .hostNickname(userEntity1.getNickname())
            .hostProfileImageUrl(userEntity1.getProfileImageUrl())
            .hostDefaultProfileImageType(userEntity1.getDefaultProfileImageType())
            .bookTitle(chatRoomEntity2.getBookTitle())
            .bookCoverImageUrl(chatRoomEntity2.getBookCoverImageUrl())
            .bookAuthors(chatRoomEntity2.getBookAuthors())
            .senderId(chatEntity2.getUserId())
            .senderNickname(chatEntity2.getUserNickname())
            .senderProfileImageUrl(chatEntity2.getUserProfileImageUrl())
            .senderDefaultProfileImageType(chatEntity2.getUserDefaultProfileImageType())
            .lastChatId(chatEntity2.getId())
            .lastChatContent(chatEntity2.getMessage())
            .lastChatDispatchTime(chatEntity2.getCreatedAt())
            .build();

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").descending());
        List<UserChatRoomResponse> contents = List.of(userChatRoomResponse1, userChatRoomResponse2);
        Slice<UserChatRoomResponse> result = toSlice(contents, pageRequest);

        Slice<UserChatRoomResponse> slice = chatRoomRepository.findUserChatRoomsWithLastChat(pageRequest, null, null, userEntity1.getId());

        assertThat(slice.getContent()).usingRecursiveComparison()
            .ignoringFieldsOfTypes(LocalDateTime.class)
            .isEqualTo(result.getContent());
    }

    @Test
    void 사용자의_채팅방_커서있는경우_최근채팅순_조회_성공() throws Exception {
        UserEntity userEntity1 = UserEntity.builder().build();
        UserEntity userEntity2 = UserEntity.builder().build();
        userRepository.save(userEntity1);
        userRepository.save(userEntity2);

        BookEntity bookEntity1 = BookEntity.builder()
            .isbn("12342")
            .authors(List.of("author1", "author2", "author3"))
            .publishAt(LocalDate.now())
            .build();
        BookEntity bookEntity2 = BookEntity.builder()
            .isbn("123426")
            .authors(List.of("author4", "author5", "author6"))
            .publishAt(LocalDate.now())
            .build();
        bookRepository.save(bookEntity1);
        bookRepository.save(bookEntity2);

        ChatRoomEntity chatRoomEntity1 = ChatRoomEntity.builder()
            .bookEntity(bookEntity1)
            .host(userEntity1)
            .roomSid("KlV8")
            .roomSize(576)
            .defaultRoomImageType(1)
            .build();
        ChatRoomEntity chatRoomEntity2 = ChatRoomEntity.builder()
            .bookEntity(bookEntity2)
            .host(userEntity1)
            .roomSid("IwZrRxR5")
            .roomSize(110)
            .defaultRoomImageType(2)
            .build();
        ChatRoomEntity chatRoomEntity3 = ChatRoomEntity.builder()
            .bookEntity(bookEntity1)
            .host(userEntity2)
            .roomSid("Gmw9yDI4")
            .roomSize(591)
            .defaultRoomImageType(3)
            .build();
        chatRoomRepository.save(chatRoomEntity1);
        chatRoomRepository.save(chatRoomEntity2);
        chatRoomRepository.save(chatRoomEntity3);

        ParticipantEntity participantEntity1 = ParticipantEntity.builder().userEntity(userEntity1).chatRoomEntity(chatRoomEntity1)
            .participantStatus(HOST).build();
        ParticipantEntity participantEntity2 = ParticipantEntity.builder().userEntity(userEntity1).chatRoomEntity(chatRoomEntity2)
            .participantStatus(SUBHOST).build();
        ParticipantEntity participantEntity3 = ParticipantEntity.builder().userEntity(userEntity1).chatRoomEntity(chatRoomEntity3)
            .participantStatus(GUEST).build();
        ParticipantEntity participantEntity4 = ParticipantEntity.builder().userEntity(userEntity2).chatRoomEntity(chatRoomEntity3)
            .participantStatus(GUEST).build();
        participantRepository.save(participantEntity1);
        participantRepository.save(participantEntity2);
        participantRepository.save(participantEntity3);
        participantRepository.save(participantEntity4);

        ChatEntity chatEntity1 = ChatEntity.builder()
            .chatRoomEntity(chatRoomEntity1)
            .userEntity(userEntity1)
            .message("first chat in chatRoom1")
            .build();
        ChatEntity chatEntity2 = ChatEntity.builder()
            .chatRoomEntity(chatRoomEntity2)
            .userEntity(userEntity1)
            .message("first chat in chatRoom2")
            .build();
        ChatEntity chatEntity3 = ChatEntity.builder()
            .chatRoomEntity(chatRoomEntity3)
            .userEntity(userEntity1)
            .message("first chat in chatRoom3")
            .build();
        ChatEntity chatEntity4 = ChatEntity.builder()
            .chatRoomEntity(chatRoomEntity3)
            .userEntity(userEntity2)
            .message("second chat in chatRoom3")
            .build();
        chatRepository.save(chatEntity1);
        chatRepository.save(chatEntity2);
        chatRepository.save(chatEntity3);
        chatRepository.save(chatEntity4);

        UserChatRoomResponse userChatRoomResponse1 = UserChatRoomResponse.builder()
            .roomId(chatRoomEntity2.getId())
            .defaultRoomImageType(chatRoomEntity2.getDefaultRoomImageType())
            .roomSid(chatRoomEntity2.getRoomSid())
            .roomMemberCount(1L)
            .hostId(userEntity1.getId())
            .hostNickname(userEntity1.getNickname())
            .hostProfileImageUrl(userEntity1.getProfileImageUrl())
            .hostDefaultProfileImageType(userEntity1.getDefaultProfileImageType())
            .bookTitle(chatRoomEntity2.getBookTitle())
            .bookCoverImageUrl(chatRoomEntity2.getBookCoverImageUrl())
            .bookAuthors(chatRoomEntity2.getBookAuthors())
            .senderId(chatEntity2.getUserId())
            .senderNickname(chatEntity2.getUserNickname())
            .senderProfileImageUrl(chatEntity2.getUserProfileImageUrl())
            .senderDefaultProfileImageType(chatEntity2.getUserDefaultProfileImageType())
            .lastChatId(chatEntity2.getId())
            .lastChatContent(chatEntity2.getMessage())
            .lastChatDispatchTime(chatEntity2.getCreatedAt())
            .build();

        UserChatRoomResponse userChatRoomResponse2 = UserChatRoomResponse.builder()
            .roomId(chatRoomEntity1.getId())
            .defaultRoomImageType(chatRoomEntity1.getDefaultRoomImageType())
            .roomSid(chatRoomEntity1.getRoomSid())
            .roomMemberCount(1L)
            .hostId(userEntity1.getId())
            .hostNickname(userEntity1.getNickname())
            .hostProfileImageUrl(userEntity1.getProfileImageUrl())
            .hostDefaultProfileImageType(userEntity1.getDefaultProfileImageType())
            .bookTitle(chatRoomEntity1.getBookTitle())
            .bookCoverImageUrl(chatRoomEntity1.getBookCoverImageUrl())
            .bookAuthors(chatRoomEntity1.getBookAuthors())
            .senderId(chatEntity1.getUserId())
            .senderNickname(chatEntity1.getUserNickname())
            .senderProfileImageUrl(chatEntity1.getUserProfileImageUrl())
            .senderDefaultProfileImageType(chatEntity1.getUserDefaultProfileImageType())
            .lastChatId(chatEntity1.getId())
            .lastChatContent(chatEntity1.getMessage())
            .lastChatDispatchTime(chatEntity1.getCreatedAt())
            .build();

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").descending());
        List<UserChatRoomResponse> contents = List.of(userChatRoomResponse1, userChatRoomResponse2);

        Slice<UserChatRoomResponse> slice = chatRoomRepository.findUserChatRoomsWithLastChat(pageRequest, null, chatRoomEntity3.getId(), userEntity1.getId());

        assertThat(slice.getContent()).usingRecursiveComparison()
            .ignoringFieldsOfTypes(LocalDateTime.class)
            .isEqualTo(contents);
    }

    @Test
    void 사용자_채팅방_조회_책_id_있는경우_연관된_채팅방만_조회_성공() throws Exception {
        UserEntity userEntity1 = UserEntity.builder().build();
        UserEntity userEntity2 = UserEntity.builder().build();
        userRepository.saveAll(List.of(userEntity1, userEntity2));

        BookEntity bookEntity1 = BookEntity.builder()
            .isbn("12329763345")
            .publishAt(LocalDate.now())
            .build();
        BookEntity bookEntity2 = BookEntity.builder()
            .isbn("12329724525")
            .publishAt(LocalDate.now())
            .build();
        bookRepository.saveAll(List.of(bookEntity1, bookEntity2));

        ChatRoomEntity chatRoomEntity1 = ChatRoomEntity.builder()
            .bookEntity(bookEntity1)
            .host(userEntity1)
            .roomSid("4SyVX")
            .roomSize(77)
            .defaultRoomImageType(1)
            .build();
        ChatRoomEntity chatRoomEntity2 = ChatRoomEntity.builder()
            .bookEntity(bookEntity2)
            .host(userEntity1)
            .roomSid("1Y2j9RlN")
            .roomSize(573)
            .defaultRoomImageType(2)
            .build();
        ChatRoomEntity chatRoomEntity3 = ChatRoomEntity.builder()
            .bookEntity(bookEntity2)
            .host(userEntity2)
            .roomSid("r7xr")
            .roomSize(38)
            .defaultRoomImageType(3)
            .build();
        chatRoomRepository.saveAll(List.of(chatRoomEntity1, chatRoomEntity2, chatRoomEntity3));

        ChatRoomBlockedUserEntity chatRoomBlockedUserEntity = ChatRoomBlockedUserEntity.builder()
            .chatRoomEntity(chatRoomEntity1)
            .userEntity(userEntity1)
            .build();
        chatRoomBlockedUserRepository.save(chatRoomBlockedUserEntity);

        ParticipantEntity participantEntity1 = ParticipantEntity.builder().userEntity(userEntity1).chatRoomEntity(chatRoomEntity1)
            .participantStatus(HOST).build();
        ParticipantEntity participantEntity2 = ParticipantEntity.builder().userEntity(userEntity1).chatRoomEntity(chatRoomEntity2)
            .participantStatus(GUEST).build();
        ParticipantEntity participantEntity3 = ParticipantEntity.builder().userEntity(userEntity1).chatRoomEntity(chatRoomEntity3)
            .participantStatus(SUBHOST).build();
        ParticipantEntity participantEntity4 = ParticipantEntity.builder().userEntity(userEntity2).chatRoomEntity(chatRoomEntity3)
            .participantStatus(GUEST).build();
        participantRepository.saveAll(List.of(participantEntity1, participantEntity2, participantEntity3, participantEntity4));

        UserChatRoomResponse userChatRoomResponse1 = UserChatRoomResponse.builder()
            .roomId(chatRoomEntity1.getId())
            .roomSid(chatRoomEntity1.getRoomSid())
            .defaultRoomImageType(chatRoomEntity1.getDefaultRoomImageType())
            .roomMemberCount(1L)
            .hostId(userEntity1.getId())
            .hostNickname(userEntity1.getNickname())
            .hostProfileImageUrl(userEntity1.getProfileImageUrl())
            .hostDefaultProfileImageType(userEntity1.getDefaultProfileImageType())
            .build();

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").ascending());
        Slice<UserChatRoomResponse> slice = chatRoomRepository.findUserChatRoomsWithLastChat(pageRequest, bookEntity1.getId(), chatRoomEntity3.getId(), userEntity1.getId());

        assertThat(slice.getContent()).containsOnly(userChatRoomResponse1);
    }

    @Test
    void 채팅방_조회_성공() throws Exception {
        UserEntity userEntity1 = UserEntity.builder()
            .nickname("nickname1")
            .profileImageUrl("profileImageUrl1")
            .defaultProfileImageType(1)
            .build();
        UserEntity userEntity2 = UserEntity.builder()
            .nickname("nickname2")
            .profileImageUrl("profileImageUrl2")
            .defaultProfileImageType(2)
            .build();
        UserEntity userEntity3 = UserEntity.builder().build();
        userRepository.save(userEntity1);
        userRepository.save(userEntity2);
        userRepository.save(userEntity3);

        BookEntity bookEntity = BookEntity.builder()
            .title("가나다 라마 바사")
            .isbn("773898468")
            .authors(List.of("author1", "author2"))
            .bookCoverImageUrl("bookCoverImage")
            .publishAt(LocalDate.now())
            .build();
        bookRepository.save(bookEntity);

        ChatRoomEntity chatRoomEntity1 = ChatRoomEntity.builder()
            .bookEntity(bookEntity)
            .host(userEntity1)
            .roomName("chatRoom1")
            .roomSid("chatRoom1")
            .roomSize(77)
            .defaultRoomImageType(1)
            .build();

        ChatRoomEntity chatRoomEntity2 = ChatRoomEntity.builder()
            .bookEntity(bookEntity)
            .host(userEntity2)
            .roomName("chatRoom2")
            .roomSid("chatRoom2")
            .roomSize(77)
            .defaultRoomImageType(1)
            .build();

        chatRoomRepository.save(chatRoomEntity1);
        chatRoomRepository.save(chatRoomEntity2);

        HashTagEntity tag1 = HashTagEntity.of("hashTag1");
        HashTagEntity tag2 = HashTagEntity.of("hashTag2");
        HashTagEntity tag3 = HashTagEntity.of("hashTag3");
        hashTagRepository.save(tag1);
        hashTagRepository.save(tag2);
        hashTagRepository.save(tag3);

        chatRoomHashTagRepository.save(ChatRoomHashTagEntity.of(chatRoomEntity1, tag1));
        chatRoomHashTagRepository.save(ChatRoomHashTagEntity.of(chatRoomEntity2, tag2));
        chatRoomHashTagRepository.save(ChatRoomHashTagEntity.of(chatRoomEntity1, tag3));

        ParticipantEntity participantEntity1 = ParticipantEntity.builder().userEntity(userEntity1).chatRoomEntity(chatRoomEntity1)
            .participantStatus(HOST).build();
        ParticipantEntity participantEntity2 = ParticipantEntity.builder().userEntity(userEntity2).chatRoomEntity(chatRoomEntity2)
            .participantStatus(HOST).build();
        ParticipantEntity participantEntity3 = ParticipantEntity.builder().userEntity(userEntity2).chatRoomEntity(chatRoomEntity1)
            .participantStatus(GUEST).build();
        ParticipantEntity participantEntity4 = ParticipantEntity.builder().userEntity(userEntity3).chatRoomEntity(chatRoomEntity2)
            .participantStatus(SUBHOST).build();
        participantRepository.save(participantEntity1);
        participantRepository.save(participantEntity2);
        participantRepository.save(participantEntity3);
        participantRepository.save(participantEntity4);

        ChatEntity chatEntity1 = ChatEntity.builder()
            .chatRoomEntity(chatRoomEntity1)
            .userEntity(userEntity1)
            .build();
        ChatEntity chatEntity2 = ChatEntity.builder()
            .chatRoomEntity(chatRoomEntity1)
            .userEntity(userEntity2)
            .build();
        ChatEntity chatEntity3 = ChatEntity.builder()
            .chatRoomEntity(chatRoomEntity2)
            .userEntity(userEntity2)
            .build();
        ChatEntity chatEntity4 = ChatEntity.builder()
            .chatRoomEntity(chatRoomEntity2)
            .userEntity(userEntity3)
            .build();
        ChatEntity chatEntity5 = ChatEntity.builder()
            .chatRoomEntity(chatRoomEntity1)
            .userEntity(userEntity1)
            .message("test chat5")
            .build();
        chatRepository.save(chatEntity1);
        chatRepository.save(chatEntity2);
        chatRepository.save(chatEntity3);
        chatRepository.save(chatEntity4);
        chatRepository.save(chatEntity5);

        PageRequest pageable = PageRequest.of(0, 1);
        ChatRoomRequest chatRoomRequest = ChatRoomRequest.builder()
            .postCursorId(500L)
            .tags(List.of("hashTag1"))
            .build();

        Slice<ChatRoomResponse> result = chatRoomRepository.findChatRooms(userEntity1.getId(), chatRoomRequest, pageable);

        ChatRoomResponse expect = ChatRoomResponse.builder()
            .roomId(chatRoomEntity1.getId())
            .roomName(chatRoomEntity1.getRoomName())
            .roomSid(chatRoomEntity1.getRoomSid())
            .roomImageUri(chatRoomEntity1.getRoomImageUri())
            .roomMemberCount(2L)
            .roomSize(chatRoomEntity1.getRoomSize())
            .defaultRoomImageType(chatRoomEntity1.getDefaultRoomImageType())
            .bookTitle(bookEntity.getTitle())
            .bookCoverImageUri(bookEntity.getBookCoverImageUrl())
            .bookAuthors(bookEntity.getAuthors())
            .hostId(userEntity1.getId())
            .hostName(userEntity1.getNickname())
            .hostDefaultProfileImageType(userEntity1.getDefaultProfileImageType())
            .hostProfileImageUri(userEntity1.getProfileImageUrl())
            .tags("hashTag1,hashTag3")
            .lastChatSenderId(chatEntity5.getUserId())
            .lastChatId(chatEntity5.getId())
            .lastChatMessage(chatEntity5.getMessage())
            .lastChatDispatchTime(chatEntity5.getCreatedAt())
            .build();

        assertThat(result.getContent()).isEqualTo(List.of(expect));
    }

    @Test
    void 참여하지_않은_채팅방_조회_성공() throws Exception {
        UserEntity userEntity1 = UserEntity.builder()
            .nickname("nickname1")
            .profileImageUrl("profileImageUrl1")
            .defaultProfileImageType(1)
            .build();
        UserEntity userEntity2 = UserEntity.builder()
            .nickname("nickname2")
            .profileImageUrl("profileImageUrl2")
            .defaultProfileImageType(2)
            .build();
        UserEntity userEntity3 = UserEntity.builder()
            .nickname("nickname3")
            .profileImageUrl("profileImageUrl3")
            .defaultProfileImageType(2)
            .build();
        userRepository.save(userEntity1);
        userRepository.save(userEntity2);
        userRepository.save(userEntity3);

        BookEntity bookEntity = BookEntity.builder()
            .title("가나다 라마 바사")
            .isbn("773898468")
            .authors(List.of("author1", "author2"))
            .bookCoverImageUrl("bookCoverImage")
            .publishAt(LocalDate.now())
            .build();
        bookRepository.save(bookEntity);

        ChatRoomEntity chatRoomEntity1 = ChatRoomEntity.builder()
            .bookEntity(bookEntity)
            .host(userEntity1)
            .roomName("chatRoom1")
            .roomSid("chatRoom1")
            .roomSize(77)
            .defaultRoomImageType(1)
            .build();

        ChatRoomEntity chatRoomEntity2 = ChatRoomEntity.builder()
            .bookEntity(bookEntity)
            .host(userEntity2)
            .roomName("chatRoom2")
            .roomSid("chatRoom2")
            .roomSize(77)
            .defaultRoomImageType(1)
            .build();

        chatRoomRepository.save(chatRoomEntity1);
        chatRoomRepository.save(chatRoomEntity2);

        HashTagEntity tag1 = HashTagEntity.of("hashTag1");
        HashTagEntity tag2 = HashTagEntity.of("hashTag2");
        HashTagEntity tag3 = HashTagEntity.of("hashTag3");
        hashTagRepository.save(tag1);
        hashTagRepository.save(tag2);
        hashTagRepository.save(tag3);

        chatRoomHashTagRepository.save(ChatRoomHashTagEntity.of(chatRoomEntity1, tag1));
        chatRoomHashTagRepository.save(ChatRoomHashTagEntity.of(chatRoomEntity2, tag2));
        chatRoomHashTagRepository.save(ChatRoomHashTagEntity.of(chatRoomEntity1, tag3));

        ParticipantEntity participantEntity1 = ParticipantEntity.builder().userEntity(userEntity1).chatRoomEntity(chatRoomEntity1)
            .participantStatus(HOST).build();
        ParticipantEntity participantEntity2 = ParticipantEntity.builder().userEntity(userEntity2).chatRoomEntity(chatRoomEntity2)
            .participantStatus(HOST).build();
        ParticipantEntity participantEntity3 = ParticipantEntity.builder().userEntity(userEntity2).chatRoomEntity(chatRoomEntity1)
            .participantStatus(GUEST).build();
        participantRepository.save(participantEntity1);
        participantRepository.save(participantEntity2);
        participantRepository.save(participantEntity3);

        ChatEntity chatEntity1 = ChatEntity.builder()
            .chatRoomEntity(chatRoomEntity1)
            .userEntity(userEntity1)
            .build();
        ChatEntity chatEntity2 = ChatEntity.builder()
            .chatRoomEntity(chatRoomEntity1)
            .userEntity(userEntity2)
            .build();
        ChatEntity chatEntity3 = ChatEntity.builder()
            .chatRoomEntity(chatRoomEntity2)
            .userEntity(userEntity2)
            .build();
        ChatEntity chatEntity4 = ChatEntity.builder()
            .chatRoomEntity(chatRoomEntity1)
            .userEntity(userEntity1)
            .message("test chat5")
            .build();
        chatRepository.save(chatEntity1);
        chatRepository.save(chatEntity2);
        chatRepository.save(chatEntity3);
        chatRepository.save(chatEntity4);

        PageRequest pageable = PageRequest.of(0, 1);
        ChatRoomRequest chatRoomRequest = ChatRoomRequest.builder()
            .postCursorId(500L)
            .tags(List.of("hashTag1"))
            .build();

        Slice<ChatRoomResponse> result = chatRoomRepository.findChatRooms(userEntity3.getId(), chatRoomRequest, pageable);

        ChatRoomResponse expect = ChatRoomResponse.builder()
            .roomId(chatRoomEntity1.getId())
            .roomName(chatRoomEntity1.getRoomName())
            .roomSid(chatRoomEntity1.getRoomSid())
            .roomImageUri(chatRoomEntity1.getRoomImageUri())
            .roomMemberCount(2L)
            .roomSize(chatRoomEntity1.getRoomSize())
            .defaultRoomImageType(chatRoomEntity1.getDefaultRoomImageType())
            .bookTitle(bookEntity.getTitle())
            .bookCoverImageUri(bookEntity.getBookCoverImageUrl())
            .bookAuthors(bookEntity.getAuthors())
            .hostId(userEntity1.getId())
            .hostName(userEntity1.getNickname())
            .hostDefaultProfileImageType(userEntity1.getDefaultProfileImageType())
            .hostProfileImageUri(userEntity1.getProfileImageUrl())
            .tags("hashTag1,hashTag3")
            .lastChatSenderId(chatEntity4.getUserId())
            .lastChatId(chatEntity4.getId())
            .lastChatMessage(chatEntity4.getMessage())
            .lastChatDispatchTime(chatEntity4.getCreatedAt())
            .build();

        assertThat(result.getContent()).isEqualTo(List.of(expect));

    }

    @Test
    void 강퇴당한_채팅방_조회_성공() throws Exception {
        UserEntity userEntity1 = UserEntity.builder()
            .nickname("nickname1")
            .profileImageUrl("profileImageUrl1")
            .defaultProfileImageType(1)
            .build();
        UserEntity userEntity2 = UserEntity.builder()
            .nickname("nickname2")
            .profileImageUrl("profileImageUrl2")
            .defaultProfileImageType(2)
            .build();
        UserEntity userEntity3 = UserEntity.builder()
            .nickname("nickname3")
            .profileImageUrl("profileImageUrl3")
            .defaultProfileImageType(2)
            .build();
        userRepository.save(userEntity1);
        userRepository.save(userEntity2);
        userRepository.save(userEntity3);

        BookEntity bookEntity = BookEntity.builder()
            .title("가나다 라마 바사")
            .isbn("773898468")
            .authors(List.of("author1", "author2"))
            .bookCoverImageUrl("bookCoverImage")
            .publishAt(LocalDate.now())
            .build();
        bookRepository.save(bookEntity);

        ChatRoomEntity chatRoomEntity1 = ChatRoomEntity.builder()
            .bookEntity(bookEntity)
            .host(userEntity1)
            .roomName("chatRoom1")
            .roomSid("chatRoom1")
            .roomSize(77)
            .defaultRoomImageType(1)
            .build();

        ChatRoomEntity chatRoomEntity2 = ChatRoomEntity.builder()
            .bookEntity(bookEntity)
            .host(userEntity2)
            .roomName("chatRoom2")
            .roomSid("chatRoom2")
            .roomSize(77)
            .defaultRoomImageType(1)
            .build();

        chatRoomRepository.save(chatRoomEntity1);
        chatRoomRepository.save(chatRoomEntity2);

        ChatRoomBlockedUserEntity chatRoomBlockedUserEntity = ChatRoomBlockedUserEntity.builder()
            .chatRoomEntity(chatRoomEntity1)
            .userEntity(userEntity3)
            .build();
        chatRoomBlockedUserRepository.save(chatRoomBlockedUserEntity);

        HashTagEntity tag1 = HashTagEntity.of("hashTag1");
        HashTagEntity tag2 = HashTagEntity.of("hashTag2");
        HashTagEntity tag3 = HashTagEntity.of("hashTag3");
        hashTagRepository.save(tag1);
        hashTagRepository.save(tag2);
        hashTagRepository.save(tag3);

        chatRoomHashTagRepository.save(ChatRoomHashTagEntity.of(chatRoomEntity1, tag1));
        chatRoomHashTagRepository.save(ChatRoomHashTagEntity.of(chatRoomEntity2, tag2));
        chatRoomHashTagRepository.save(ChatRoomHashTagEntity.of(chatRoomEntity1, tag3));

        ParticipantEntity participantEntity1 = ParticipantEntity.builder().userEntity(userEntity1).chatRoomEntity(chatRoomEntity1)
            .participantStatus(HOST).build();
        ParticipantEntity participantEntity2 = ParticipantEntity.builder().userEntity(userEntity2).chatRoomEntity(chatRoomEntity2)
            .participantStatus(HOST).build();
        ParticipantEntity participantEntity3 = ParticipantEntity.builder().userEntity(userEntity2).chatRoomEntity(chatRoomEntity1)
            .participantStatus(GUEST).build();
        participantRepository.save(participantEntity1);
        participantRepository.save(participantEntity2);
        participantRepository.save(participantEntity3);

        ChatEntity chatEntity1 = ChatEntity.builder()
            .chatRoomEntity(chatRoomEntity1)
            .userEntity(userEntity1)
            .build();
        ChatEntity chatEntity2 = ChatEntity.builder()
            .chatRoomEntity(chatRoomEntity1)
            .userEntity(userEntity2)
            .build();
        ChatEntity chatEntity3 = ChatEntity.builder()
            .chatRoomEntity(chatRoomEntity2)
            .userEntity(userEntity2)
            .build();
        ChatEntity chatEntity4 = ChatEntity.builder()
            .chatRoomEntity(chatRoomEntity1)
            .userEntity(userEntity1)
            .message("test chat5")
            .build();
        chatRepository.save(chatEntity1);
        chatRepository.save(chatEntity2);
        chatRepository.save(chatEntity3);
        chatRepository.save(chatEntity4);

        PageRequest pageable = PageRequest.of(0, 1);
        ChatRoomRequest chatRoomRequest = ChatRoomRequest.builder()
            .postCursorId(500L)
            .tags(List.of("hashTag1"))
            .build();

        Slice<ChatRoomResponse> result = chatRoomRepository.findChatRooms(userEntity3.getId(), chatRoomRequest, pageable);

        ChatRoomResponse expect = ChatRoomResponse.builder()
            .roomId(chatRoomEntity1.getId())
            .roomName(chatRoomEntity1.getRoomName())
            .roomSid(chatRoomEntity1.getRoomSid())
            .roomImageUri(chatRoomEntity1.getRoomImageUri())
            .roomMemberCount(2L)
            .roomSize(chatRoomEntity1.getRoomSize())
            .defaultRoomImageType(chatRoomEntity1.getDefaultRoomImageType())
            .bookTitle(bookEntity.getTitle())
            .bookCoverImageUri(bookEntity.getBookCoverImageUrl())
            .bookAuthors(bookEntity.getAuthors())
            .hostId(userEntity1.getId())
            .hostName(userEntity1.getNickname())
            .hostDefaultProfileImageType(userEntity1.getDefaultProfileImageType())
            .hostProfileImageUri(userEntity1.getProfileImageUrl())
            .tags("hashTag1,hashTag3")
            .lastChatSenderId(chatEntity4.getUserId())
            .lastChatId(chatEntity4.getId())
            .lastChatMessage(chatEntity4.getMessage())
            .lastChatDispatchTime(chatEntity4.getCreatedAt())
            .build();

        assertThat(result.getContent()).isEqualTo(List.of(expect));
    }

    @Test
    void 폭파된_채팅방은_제외되어_조회_성공() throws Exception {
        UserEntity userEntity1 = UserEntity.builder()
            .nickname("nickname1")
            .profileImageUrl("profileImageUrl1")
            .defaultProfileImageType(1)
            .build();
        UserEntity userEntity2 = UserEntity.builder()
            .nickname("nickname2")
            .profileImageUrl("profileImageUrl2")
            .defaultProfileImageType(2)
            .build();
        UserEntity userEntity3 = UserEntity.builder()
            .nickname("nickname3")
            .profileImageUrl("profileImageUrl3")
            .defaultProfileImageType(2)
            .build();
        userRepository.save(userEntity1);
        userRepository.save(userEntity2);
        userRepository.save(userEntity3);

        BookEntity bookEntity = BookEntity.builder()
            .title("가나다 라마 바사")
            .isbn("773898468")
            .authors(List.of("author1", "author2"))
            .bookCoverImageUrl("bookCoverImage")
            .publishAt(LocalDate.now())
            .build();
        bookRepository.save(bookEntity);

        ChatRoomEntity chatRoomEntity1 = ChatRoomEntity.builder()
            .bookEntity(bookEntity)
            .host(userEntity1)
            .roomName("chatRoom1")
            .roomSid("chatRoom1")
            .roomSize(77)
            .defaultRoomImageType(1)
            .build();

        ChatRoomEntity chatRoomEntity2 = ChatRoomEntity.builder()
            .bookEntity(bookEntity)
            .host(userEntity2)
            .roomName("chatRoom2")
            .roomSid("chatRoom2")
            .roomSize(77)
            .defaultRoomImageType(1)
            .build();

        chatRoomRepository.save(chatRoomEntity1);
        chatRoomRepository.save(chatRoomEntity2);

        ChatRoomBlockedUserEntity chatRoomBlockedUserEntity = ChatRoomBlockedUserEntity.builder()
            .chatRoomEntity(chatRoomEntity2)
            .userEntity(userEntity3)
            .build();
        chatRoomBlockedUserRepository.save(chatRoomBlockedUserEntity);

        HashTagEntity tag1 = HashTagEntity.of("hashTag1");
        HashTagEntity tag2 = HashTagEntity.of("hashTag2");
        HashTagEntity tag3 = HashTagEntity.of("hashTag3");
        hashTagRepository.save(tag1);
        hashTagRepository.save(tag2);
        hashTagRepository.save(tag3);

        chatRoomHashTagRepository.save(ChatRoomHashTagEntity.of(chatRoomEntity1, tag1));
        chatRoomHashTagRepository.save(ChatRoomHashTagEntity.of(chatRoomEntity2, tag2));
        chatRoomHashTagRepository.save(ChatRoomHashTagEntity.of(chatRoomEntity1, tag3));

        ParticipantEntity participantEntity1 = ParticipantEntity.builder().userEntity(userEntity1).chatRoomEntity(chatRoomEntity1)
            .participantStatus(HOST).build();
        ParticipantEntity participantEntity2 = ParticipantEntity.builder().userEntity(userEntity2).chatRoomEntity(chatRoomEntity2)
            .participantStatus(HOST).build();
        ParticipantEntity participantEntity3 = ParticipantEntity.builder().userEntity(userEntity2).chatRoomEntity(chatRoomEntity1)
            .participantStatus(GUEST).build();
        participantRepository.save(participantEntity1);
        participantRepository.save(participantEntity2);
        participantRepository.save(participantEntity3);

        ChatEntity chatEntity1 = ChatEntity.builder()
            .chatRoomEntity(chatRoomEntity1)
            .userEntity(userEntity1)
            .build();
        ChatEntity chatEntity2 = ChatEntity.builder()
            .chatRoomEntity(chatRoomEntity1)
            .userEntity(userEntity2)
            .build();
        ChatEntity chatEntity3 = ChatEntity.builder()
            .chatRoomEntity(chatRoomEntity2)
            .userEntity(userEntity2)
            .build();
        ChatEntity chatEntity4 = ChatEntity.builder()
            .chatRoomEntity(chatRoomEntity1)
            .userEntity(userEntity1)
            .message("test chat5")
            .build();
        chatRepository.save(chatEntity1);
        chatRepository.save(chatEntity2);
        chatRepository.save(chatEntity3);
        chatRepository.save(chatEntity4);

        PageRequest pageable = PageRequest.of(0, 1);
        ChatRoomRequest chatRoomRequest = ChatRoomRequest.builder()
            .postCursorId(500L)
            .tags(List.of("hashTag2"))
            .build();

        Slice<ChatRoomResponse> result = chatRoomRepository.findChatRooms(userEntity3.getId(), chatRoomRequest, pageable);

        ChatRoomResponse expect = ChatRoomResponse.builder()
            .roomId(chatRoomEntity2.getId())
            .roomName(chatRoomEntity2.getRoomName())
            .roomSid(chatRoomEntity2.getRoomSid())
            .roomImageUri(chatRoomEntity2.getRoomImageUri())
            .roomMemberCount(1L)
            .roomSize(chatRoomEntity2.getRoomSize())
            .defaultRoomImageType(chatRoomEntity2.getDefaultRoomImageType())
            .bookTitle(bookEntity.getTitle())
            .bookCoverImageUri(bookEntity.getBookCoverImageUrl())
            .bookAuthors(bookEntity.getAuthors())
            .hostId(userEntity2.getId())
            .hostName(userEntity2.getNickname())
            .hostDefaultProfileImageType(userEntity2.getDefaultProfileImageType())
            .hostProfileImageUri(userEntity2.getProfileImageUrl())
            .tags("hashTag2")
            .lastChatSenderId(chatEntity3.getUserId())
            .lastChatId(chatEntity3.getId())
            .lastChatMessage(chatEntity3.getMessage())
            .lastChatDispatchTime(chatEntity3.getCreatedAt())
            .build();

        assertThat(result.getContent()).isEqualTo(List.of(expect));
    }

    @Test
    void 채팅방_세부정보_조회_성공() throws Exception {
        UserEntity aUserEntity = UserEntity.builder()
            .nickname("AUser")
            .defaultProfileImageType(1)
            .build();
        UserEntity bUserEntity = UserEntity.builder()
            .nickname("BUser")
            .profileImageUrl("testB@s3.com")
            .defaultProfileImageType(1)
            .build();
        UserEntity cUserEntity = UserEntity.builder()
            .nickname("CUser")
            .profileImageUrl("testC@s3.com")
            .defaultProfileImageType(1)
            .build();
        userRepository.saveAll(List.of(aUserEntity, bUserEntity, cUserEntity));

        BookEntity bookEntity = BookEntity.builder()
            .title("effectiveJava")
            .isbn("tXaid")
            .publishAt(LocalDate.now())
            .authors(List.of("joshua", "jcr"))
            .bookCoverImageUrl("effective@s3.com")
            .build();
        bookRepository.save(bookEntity);

        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
            .host(aUserEntity)
            .bookEntity(bookEntity)
            .roomSid("cES1Cn4N")
            .roomSize(275)
            .defaultRoomImageType(2)
            .build();
        chatRoomRepository.save(chatRoomEntity);

        ChatRoomBlockedUserEntity chatRoomBlockedUserEntity = ChatRoomBlockedUserEntity.builder()
            .chatRoomEntity(chatRoomEntity)
            .userEntity(cUserEntity)
            .build();
        chatRoomBlockedUserRepository.save(chatRoomBlockedUserEntity);

        HashTagEntity tag = HashTagEntity.of("tag1");
        hashTagRepository.save(tag);

        ChatRoomHashTagEntity chatRoomHashTagEntity = ChatRoomHashTagEntity.of(chatRoomEntity, tag);
        chatRoomHashTagRepository.save(chatRoomHashTagEntity);

        ParticipantEntity participantEntity1 = ParticipantEntity.builder()
            .userEntity(aUserEntity)
            .participantStatus(HOST)
            .chatRoomEntity(chatRoomEntity)
            .build();
        ParticipantEntity participantEntity2 = ParticipantEntity.builder()
            .userEntity(bUserEntity)
            .participantStatus(SUBHOST)
            .chatRoomEntity(chatRoomEntity)
            .build();
        ParticipantEntity participantEntity3 = ParticipantEntity.builder()
            .userEntity(cUserEntity)
            .participantStatus(GUEST)
            .chatRoomEntity(chatRoomEntity)
            .build();
        participantRepository.saveAll(List.of(participantEntity1, participantEntity2, participantEntity3));

        ChatRoomDetails real = chatRoomRepository.findChatRoomDetails(chatRoomEntity.getId(), cUserEntity.getId());

        ChatRoomDetails expect = ChatRoomDetails.from(List.of(participantEntity1, participantEntity2, participantEntity3), List.of(tag.getTagName()));

        assertThat(real).isEqualTo(expect);
    }

    @Test
    void 존재하지않는_채팅방_세부정보_조회시_예외발생() throws Exception {
        assertThatThrownBy(() -> {
            chatRoomRepository.findChatRoomDetails(53L, 606L);
        }).isInstanceOf(ParticipantNotFoundException.class);
    }

    @Test
    void 사용자가_접속한_채팅방_조회_성공() throws Exception {
        UserEntity userEntity = UserEntity.builder().build();
        userRepository.save(userEntity);

        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
            .roomSid("4SyVX")
            .roomSize(77)
            .defaultRoomImageType(1)
            .build();
        chatRoomRepository.save(chatRoomEntity);

        ParticipantEntity participantEntity = ParticipantEntity.builder()
            .userEntity(userEntity)
            .chatRoomEntity(chatRoomEntity)
            .participantStatus(GUEST)
            .build();
        participantRepository.save(participantEntity);

        Optional<ChatRoomEntity> optionalChatRoom = chatRoomRepository.findUserChatRoom(chatRoomEntity.getId(), userEntity.getId());

        assertThat(optionalChatRoom).isPresent();
    }
}