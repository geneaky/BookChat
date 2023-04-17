package toy.bookchat.bookchat.domain.chatroom.service;

import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.HOST;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.ChatRoomHashTag;
import toy.bookchat.bookchat.domain.chatroom.HashTag;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomHashTagRepository;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.domain.chatroom.repository.HashTagRepository;
import toy.bookchat.bookchat.domain.chatroom.repository.query.dto.response.ChatRoomsResponseSlice;
import toy.bookchat.bookchat.domain.chatroom.repository.query.dto.response.UserChatRoomsResponseSlice;
import toy.bookchat.bookchat.domain.chatroom.service.dto.request.ChatRoomRequest;
import toy.bookchat.bookchat.domain.chatroom.service.dto.request.CreateChatRoomRequest;
import toy.bookchat.bookchat.domain.chatroom.service.dto.response.CreatedChatRoomDto;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.storage.StorageService;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.exception.user.UserNotFoundException;

@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ParticipantRepository participantRepository;
    private final HashTagRepository hashTagRepository;
    private final ChatRoomHashTagRepository chatRoomHashTagRepository;
    private final StorageService storageService;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public ChatRoomService(
        ChatRoomRepository chatRoomRepository,
        ParticipantRepository participantRepository,
        HashTagRepository hashTagRepository,
        ChatRoomHashTagRepository chatRoomHashTagRepository,
        @Qualifier("chatRoomStorageService") StorageService storageService,
        BookRepository bookRepository,
        UserRepository userRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.participantRepository = participantRepository;
        this.hashTagRepository = hashTagRepository;
        this.chatRoomHashTagRepository = chatRoomHashTagRepository;
        this.storageService = storageService;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CreatedChatRoomDto createChatRoom(CreateChatRoomRequest createChatRoomRequest,
        Optional<MultipartFile> chatRoomImage, Long userId) {
        Book book = bookRepository.findByIsbnAndPublishAt(createChatRoomRequest.getIsbn(),
                createChatRoomRequest.getPublishAt())
            .orElseGet(() -> bookRepository.save(createChatRoomRequest.createBook()));
        User host = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        ChatRoom chatRoom;
        if (chatRoomImage.isPresent()) {
            MultipartFile image = chatRoomImage.get();
            String prefixedUUIDFileName = storageService.createFileName(image,
                UUID.randomUUID().toString(),
                new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            String prefixedUUIDFileUrl = storageService.getFileUrl(prefixedUUIDFileName);
            chatRoom = registerChatRoom(createChatRoomRequest, book, host, prefixedUUIDFileUrl);

            storageService.upload(image, prefixedUUIDFileName);
        } else {
            chatRoom = registerChatRoom(createChatRoomRequest, book, host, null);
        }

        return CreatedChatRoomDto.builder()
            .roomId(chatRoom.getId())
            .roomSid(chatRoom.getRoomSid())
            .build();
    }

    private ChatRoom registerChatRoom(CreateChatRoomRequest createChatRoomRequest, Book book,
        User host,
        String prefixedUUIDFileUrl) {
        ChatRoom chatRoom;
        chatRoom = saveChatRoom(createChatRoomRequest, book, host, prefixedUUIDFileUrl);
        registerHashTagOnChatRoom(createChatRoomRequest, chatRoom);
        return chatRoom;
    }

    private Runnable saveChatRoomAndHashTagWithoutImage(String roomSid,
        CreateChatRoomRequest createChatRoomRequest,
        Book book, User host) {
        return () -> registerHashTagOnChatRoom(createChatRoomRequest,
            saveChatRoom(createChatRoomRequest, book, host, null));
    }

    private Consumer<MultipartFile> saveChatRoomAndHashTagWithImage(
        CreateChatRoomRequest createChatRoomRequest, Book book, User host) {
        return image -> {
            String prefixedUUIDFileName = storageService.createFileName(
                image, UUID.randomUUID().toString(),
                new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            String prefixedUUIDFileUrl = storageService.getFileUrl(prefixedUUIDFileName);
            ChatRoom chatRoom = saveChatRoom(createChatRoomRequest, book, host,
                prefixedUUIDFileUrl);
            registerHashTagOnChatRoom(createChatRoomRequest, chatRoom);
            storageService.upload(image, prefixedUUIDFileName);
        };
    }

    private ChatRoom saveChatRoom(CreateChatRoomRequest createChatRoomRequest,
        Book book, User host, String fileUrl) {
        ChatRoom chatRoom = createChatRoomRequest.makeChatRoom(book, host, fileUrl);
        chatRoomRepository.save(chatRoom);
        saveParticipantWithRoomHostAndRoom(host, chatRoom);
        return chatRoom;
    }

    private void saveParticipantWithRoomHostAndRoom(User host, ChatRoom chatRoom) {
        Participant participant = Participant.builder()
            .participantStatus(HOST)
            .chatRoom(chatRoom)
            .user(host)
            .build();
        participantRepository.save(participant);
    }

    private void registerHashTagOnChatRoom(CreateChatRoomRequest createChatRoomRequest,
        ChatRoom chatRoom) {
        createChatRoomRequest.getHashTags().stream()
            .map(tagName -> hashTagRepository.findByTagName(tagName)
                .orElseGet(() -> hashTagRepository.save(HashTag.of(tagName))))
            .forEach(
                hashTag -> chatRoomHashTagRepository.save(ChatRoomHashTag.of(chatRoom, hashTag)));
    }

    @Transactional(readOnly = true)
    public UserChatRoomsResponseSlice getUserChatRooms(Optional<Long> postCursorId,
        Pageable pageable, Long userId) {
        return UserChatRoomsResponseSlice.of(
            chatRoomRepository.findUserChatRoomsWithLastChat(pageable, postCursorId, userId));
    }

    @Transactional(readOnly = true)
    public ChatRoomsResponseSlice getChatRooms(ChatRoomRequest chatRoomRequest, Pageable pageable) {
        return ChatRoomsResponseSlice.of(
            chatRoomRepository.findChatRooms(chatRoomRequest, pageable));
    }
}
