package toy.bookchat.bookchat.domain.chatroom.service;

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
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.domain.chatroom.service.dto.request.CreateChatRoomRequest;
import toy.bookchat.bookchat.domain.chatroom.service.dto.response.SliceOfChatRoomsResponse;
import toy.bookchat.bookchat.domain.chatroomhashtag.ChatRoomHashTag;
import toy.bookchat.bookchat.domain.chatroomhashtag.repository.ChatRoomHashTagRepository;
import toy.bookchat.bookchat.domain.chatroomhost.ChatRoomHost;
import toy.bookchat.bookchat.domain.chatroomhost.repository.ChatRoomHostRepository;
import toy.bookchat.bookchat.domain.hashtag.HashTag;
import toy.bookchat.bookchat.domain.hashtag.repository.HashTagRepository;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.storage.StorageService;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.exception.user.UserNotFoundException;

@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomHostRepository chatRoomHostRepository;
    private final ParticipantRepository participantRepository;
    private final HashTagRepository hashTagRepository;
    private final ChatRoomHashTagRepository chatRoomHashTagRepository;
    private final StorageService storageService;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public ChatRoomService(
        ChatRoomRepository chatRoomRepository,
        ChatRoomHostRepository chatRoomHostRepository,
        ParticipantRepository participantRepository,
        HashTagRepository hashTagRepository,
        ChatRoomHashTagRepository chatRoomHashTagRepository,
        @Qualifier("chatRoomStorageService") StorageService storageService,
        BookRepository bookRepository,
        UserRepository userRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomHostRepository = chatRoomHostRepository;
        this.participantRepository = participantRepository;
        this.hashTagRepository = hashTagRepository;
        this.chatRoomHashTagRepository = chatRoomHashTagRepository;
        this.storageService = storageService;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void createChatRoom(CreateChatRoomRequest createChatRoomRequest,
        Optional<MultipartFile> chatRoomImage, Long userId) {
        Book book = bookRepository.findByIsbnAndPublishAt(createChatRoomRequest.getIsbn(),
                createChatRoomRequest.getPublishAt())
            .orElseGet(() -> bookRepository.save(createChatRoomRequest.createBook()));
        User mainHost = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        ChatRoomHost chatRoomHost = saveChatRoomHost(mainHost);
        chatRoomImage.ifPresentOrElse(
            saveChatRoomAndHashTagWithImage(createChatRoomRequest, book, chatRoomHost),
            saveChatRoomAndHashTagWithoutImage(createChatRoomRequest, book, chatRoomHost));
    }

    private Runnable saveChatRoomAndHashTagWithoutImage(CreateChatRoomRequest createChatRoomRequest,
        Book book, ChatRoomHost chatRoomHost) {
        return () -> registerHashTagOnChatRoom(createChatRoomRequest,
            saveChatRoom(createChatRoomRequest, book, chatRoomHost, null));
    }

    private Consumer<MultipartFile> saveChatRoomAndHashTagWithImage(
        CreateChatRoomRequest createChatRoomRequest, Book book, ChatRoomHost chatRoomHost) {
        return image -> {
            String prefixedUUIDFileName = storageService.createFileName(
                image, UUID.randomUUID().toString(),
                new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            String prefixedUUIDFileUrl = storageService.getFileUrl(prefixedUUIDFileName);
            ChatRoom chatRoom = saveChatRoom(createChatRoomRequest, book, chatRoomHost,
                prefixedUUIDFileUrl);
            storageService.upload(image, prefixedUUIDFileName);
            registerHashTagOnChatRoom(createChatRoomRequest, chatRoom);
        };
    }

    private ChatRoom saveChatRoom(CreateChatRoomRequest createChatRoomRequest, Book book,
        ChatRoomHost chatRoomHost, String fileUrl) {
        ChatRoom chatRoom = createChatRoomRequest.makeChatRoom(book, chatRoomHost, fileUrl);
        chatRoomRepository.save(chatRoom);
        saveParticipantWithRoomHostAndRoom(chatRoomHost, chatRoom);
        return chatRoom;
    }

    private void saveParticipantWithRoomHostAndRoom(ChatRoomHost chatRoomHost, ChatRoom chatRoom) {
        Participant participant = Participant.builder()
            .chatRoom(chatRoom)
            .user(chatRoomHost.getMainHost())
            .build();
        participantRepository.save(participant);
    }

    private ChatRoomHost saveChatRoomHost(User mainHost) {
        ChatRoomHost chatRoomHost = ChatRoomHost.builder()
            .mainHost(mainHost)
            .build();
        chatRoomHostRepository.save(chatRoomHost);
        return chatRoomHost;
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
    public SliceOfChatRoomsResponse getUserChatRooms(Optional<Long> postCursorId,
        Pageable pageable, Long userId) {
        return SliceOfChatRoomsResponse.of(
            chatRoomRepository.findUserChatRoomsWithLastChat(postCursorId, pageable, userId));
    }
}
