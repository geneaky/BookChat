package toy.bookchat.bookchat.domain.chatroom.service;

import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.GUEST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.HOST;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chat.repository.ChatRepository;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.ChatRoomHashTag;
import toy.bookchat.bookchat.domain.chatroom.HashTag;
import toy.bookchat.bookchat.domain.chatroom.api.dto.response.UserChatRoomDetailResponse;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomBlockedUserRepository;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomHashTagRepository;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.domain.chatroom.repository.HashTagRepository;
import toy.bookchat.bookchat.domain.chatroom.repository.query.dto.response.ChatRoomsResponseSlice;
import toy.bookchat.bookchat.domain.chatroom.repository.query.dto.response.UserChatRoomsResponseSlice;
import toy.bookchat.bookchat.domain.chatroom.service.dto.request.ChatRoomRequest;
import toy.bookchat.bookchat.domain.chatroom.service.dto.request.CreateChatRoomRequest;
import toy.bookchat.bookchat.domain.chatroom.service.dto.request.ReviseChatRoomRequest;
import toy.bookchat.bookchat.domain.chatroom.service.dto.response.CreatedChatRoomDto;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.participant.service.dto.response.ChatRoomDetails;
import toy.bookchat.bookchat.domain.storage.StorageService;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.service.UserReader;
import toy.bookchat.bookchat.exception.badrequest.chatroom.ChatRoomIsFullException;
import toy.bookchat.bookchat.exception.badrequest.participant.AlreadyParticipateException;
import toy.bookchat.bookchat.exception.forbidden.chatroom.BlockedUserInChatRoomException;
import toy.bookchat.bookchat.exception.notfound.chatroom.ChatRoomNotFoundException;
import toy.bookchat.bookchat.exception.notfound.pariticipant.ParticipantNotFoundException;
import toy.bookchat.bookchat.infrastructure.broker.MessagePublisher;
import toy.bookchat.bookchat.infrastructure.broker.message.NotificationMessage;

@Service
public class ChatRoomService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ParticipantRepository participantRepository;
    private final HashTagRepository hashTagRepository;
    private final ChatRoomHashTagRepository chatRoomHashTagRepository;
    private final StorageService storageService;
    private final BookRepository bookRepository;
    private final UserReader userReader;
    private final ChatRoomBlockedUserRepository chatRoomBlockedUserRepository;
    private final MessagePublisher messagePublisher;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ChatRoomService(ChatRepository chatRepository, ChatRoomRepository chatRoomRepository, ParticipantRepository participantRepository, HashTagRepository hashTagRepository,
        ChatRoomHashTagRepository chatRoomHashTagRepository, @Qualifier("chatRoomStorageService") StorageService storageService, BookRepository bookRepository, UserReader userReader,
        ChatRoomBlockedUserRepository chatRoomBlockedUserRepository, MessagePublisher messagePublisher) {
        this.chatRepository = chatRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.participantRepository = participantRepository;
        this.hashTagRepository = hashTagRepository;
        this.chatRoomHashTagRepository = chatRoomHashTagRepository;
        this.storageService = storageService;
        this.bookRepository = bookRepository;
        this.userReader = userReader;
        this.chatRoomBlockedUserRepository = chatRoomBlockedUserRepository;
        this.messagePublisher = messagePublisher;
    }

    @Transactional
    public CreatedChatRoomDto createChatRoom(CreateChatRoomRequest createChatRoomRequest, MultipartFile chatRoomImage, Long userId) {
        Book book = bookRepository.findByIsbnAndPublishAt(createChatRoomRequest.getIsbn(), createChatRoomRequest.getPublishAt())
            .orElseGet(() -> bookRepository.save(createChatRoomRequest.createBook()));
        User host = userReader.readUser(userId);

        if (chatRoomImageExistent(chatRoomImage)) {
            String uploadFileUrl = storageService.upload(chatRoomImage, UUID.randomUUID().toString(), LocalDateTime.now().format(dateTimeFormatter));
            return CreatedChatRoomDto.of(registerChatRoom(createChatRoomRequest, book, host, uploadFileUrl));
        }
        return CreatedChatRoomDto.of(registerChatRoom(createChatRoomRequest, book, host, null));
    }

    private boolean chatRoomImageExistent(MultipartFile chatRoomImage) {
        return chatRoomImage != null;
    }

    private ChatRoom registerChatRoom(CreateChatRoomRequest createChatRoomRequest, Book book, User host, String prefixedUUIDFileUrl) {
        ChatRoom chatRoom = saveChatRoom(createChatRoomRequest, book, host, prefixedUUIDFileUrl);
        registerHashTagOnChatRoom(createChatRoomRequest, chatRoom);
        return chatRoom;
    }

    private ChatRoom saveChatRoom(CreateChatRoomRequest createChatRoomRequest, Book book, User host, String fileUrl) {
        ChatRoom chatRoom = chatRoomRepository.save(createChatRoomRequest.makeChatRoom(book, host, fileUrl));
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

    private void registerHashTagOnChatRoom(CreateChatRoomRequest createChatRoomRequest, ChatRoom chatRoom) {
        createChatRoomRequest.getHashTags().stream()
            .map(tagName -> hashTagRepository.findByTagName(tagName)
                .orElseGet(() -> hashTagRepository.save(HashTag.of(tagName))))
            .forEach(
                hashTag -> chatRoomHashTagRepository.save(ChatRoomHashTag.of(chatRoom, hashTag)));
    }

    @Transactional(readOnly = true)
    public UserChatRoomsResponseSlice getUserChatRooms(Long bookId, Long postCursorId, Pageable pageable, Long userId) {
        return UserChatRoomsResponseSlice.of(chatRoomRepository.findUserChatRoomsWithLastChat(pageable, bookId, postCursorId, userId));
    }

    @Transactional(readOnly = true)
    public UserChatRoomDetailResponse getUserChatRoomDetails(Long roomId, Long userId) {
        ChatRoom chatroom = chatRoomRepository.findUserChatRoom(roomId, userId).orElseThrow(ChatRoomNotFoundException::new);
        Long roomMemberCount = participantRepository.countByChatRoom(chatroom);

        return UserChatRoomDetailResponse.from(chatroom, roomMemberCount);
    }

    @Transactional(readOnly = true)
    public ChatRoomsResponseSlice getChatRooms(Long userId, ChatRoomRequest chatRoomRequest, Pageable pageable) {
        return ChatRoomsResponseSlice.of(chatRoomRepository.findChatRooms(userId, chatRoomRequest, pageable));
    }

    @Transactional(readOnly = true)
    public ChatRoomDetails getChatRoomDetails(Long roomId, Long userId) {
        chatRoomBlockedUserRepository.findByUserIdAndChatRoomId(userId, roomId)
            .ifPresent(b -> {
                throw new BlockedUserInChatRoomException();
            });
        chatRoomRepository.findById(roomId).orElseThrow(ChatRoomNotFoundException::new);

        return chatRoomRepository.findChatRoomDetails(roomId, userId);
    }

    @Transactional
    public void reviseChatRoom(ReviseChatRoomRequest reviseChatRoomRequest, MultipartFile chatRoomImage, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findChatRoomByIdAndHostId(reviseChatRoomRequest.getRoomId(), userId).orElseThrow(ChatRoomNotFoundException::new);

        updateIfChatRoomHashTagsPresent(reviseChatRoomRequest, chatRoom);
        if (chatRoomImageExistent(chatRoomImage)) {
            String roomImageUri = storageService.upload(chatRoomImage, UUID.randomUUID().toString(), LocalDateTime.now().format(dateTimeFormatter));
            chatRoom.changeRoomImageUri(roomImageUri);
        }
        reviseChatRoomRequest.reviseChatRoom(chatRoom);
    }

    private void updateIfChatRoomHashTagsPresent(ReviseChatRoomRequest reviseChatRoomRequest, ChatRoom chatRoom) {
        if (reviseChatRoomRequest.tagExistent()) {
            chatRoomHashTagRepository.deleteAllByChatRoom(chatRoom);

            List<HashTag> hashTags = reviseChatRoomRequest.createHashTag();
            hashTagRepository.saveAll(hashTags);

            List<ChatRoomHashTag> chatRoomHashTags = hashTags.stream()
                .map(ht -> ChatRoomHashTag.of(chatRoom, ht))
                .collect(Collectors.toList());
            chatRoomHashTagRepository.saveAll(chatRoomHashTags);
        }
    }

    @Transactional
    public void enterChatRoom(Long userId, Long roomId) {
        User user = userReader.readUser(userId);
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(ChatRoomNotFoundException::new);
        participantRepository.findByUserIdAndChatRoomId(user.getId(), chatRoom.getId())
            .ifPresent(p -> {
                throw new AlreadyParticipateException();
            });

        if (chatRoom.getHost() != user) {
            checkIsBlockedUser(user, chatRoom);
            checkIsFullChatRoom(chatRoom);

            participantRepository.save(Participant.builder()
                .participantStatus(GUEST)
                .user(user)
                .chatRoom(chatRoom)
                .build());
        }

        Chat chat = chatRepository.save(Chat.builder()
            .chatRoom(chatRoom)
            .message("#" + user.getId() + "#님이 입장하셨습니다.")
            .build());

        messagePublisher.sendNotificationMessage(chatRoom.getRoomSid(),
            NotificationMessage.createEntranceMessage(chat, user.getId()));
    }

    private void checkIsFullChatRoom(ChatRoom chatRoom) {
        List<Participant> participants = participantRepository.findWithPessimisticLockByChatRoom(chatRoom);

        if (chatRoom.getRoomSize() <= participants.size()) {
            throw new ChatRoomIsFullException();
        }
    }

    private void checkIsBlockedUser(User user, ChatRoom chatRoom) {
        chatRoomBlockedUserRepository.findByUserIdAndChatRoomId(user.getId(), chatRoom.getId())
            .ifPresent(b -> {
                throw new BlockedUserInChatRoomException();
            });
    }

    @Transactional
    public void exitChatRoom(Long userId, Long roomId) {
        chatRoomRepository.findById(roomId).orElseThrow(ChatRoomNotFoundException::new);
        Participant participant = participantRepository.findByUserIdAndChatRoomId(userId, roomId)
            .orElseThrow(ParticipantNotFoundException::new);

        User user = participant.getUser();
        ChatRoom chatRoom = participant.getChatRoom();

        if (user == chatRoom.getHost()) {
            Chat chat = createExitChat(chatRoom, user);
            participantRepository.deleteByChatRoom(chatRoom);
            chatRoomRepository.delete(chatRoom);
            messagePublisher.sendNotificationMessage(chatRoom.getRoomSid(), NotificationMessage.createHostExitMessage(chat));
            return;
        }

        Chat chat = createExitChat(chatRoom, user);

        participantRepository.delete(participant);
        messagePublisher.sendNotificationMessage(chatRoom.getRoomSid(), NotificationMessage.createExitMessage(chat, user.getId()));
    }

    private Chat createExitChat(ChatRoom chatRoom, User user) {
        Chat chat = chatRepository.save(Chat.builder()
            .chatRoom(chatRoom)
            .message("#" + user.getId() + "#님이 퇴장하셨습니다.")
            .build());
        return chat;
    }

}
