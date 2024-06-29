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
import toy.bookchat.bookchat.domain.book.BookEntity;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.chat.ChatEntity;
import toy.bookchat.bookchat.domain.chat.repository.ChatRepository;
import toy.bookchat.bookchat.domain.chatroom.ChatRoomEntity;
import toy.bookchat.bookchat.domain.chatroom.ChatRoomHashTagEntity;
import toy.bookchat.bookchat.domain.chatroom.HashTagEntity;
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
import toy.bookchat.bookchat.domain.participant.ParticipantEntity;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.participant.service.dto.response.ChatRoomDetails;
import toy.bookchat.bookchat.domain.storage.StorageService;
import toy.bookchat.bookchat.domain.user.UserEntity;
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
        BookEntity bookEntity = bookRepository.findByIsbnAndPublishAt(createChatRoomRequest.getIsbn(), createChatRoomRequest.getPublishAt())
            .orElseGet(() -> bookRepository.save(createChatRoomRequest.createBook()));
        UserEntity host = userReader.readUser(userId);

        if (chatRoomImageExistent(chatRoomImage)) {
            String uploadFileUrl = storageService.upload(chatRoomImage, UUID.randomUUID().toString(), LocalDateTime.now().format(dateTimeFormatter));
            return CreatedChatRoomDto.of(registerChatRoom(createChatRoomRequest, bookEntity, host, uploadFileUrl));
        }
        return CreatedChatRoomDto.of(registerChatRoom(createChatRoomRequest, bookEntity, host, null));
    }

    private boolean chatRoomImageExistent(MultipartFile chatRoomImage) {
        return chatRoomImage != null;
    }

    private ChatRoomEntity registerChatRoom(CreateChatRoomRequest createChatRoomRequest, BookEntity bookEntity, UserEntity host, String prefixedUUIDFileUrl) {
        ChatRoomEntity chatRoomEntity = saveChatRoom(createChatRoomRequest, bookEntity, host, prefixedUUIDFileUrl);
        registerHashTagOnChatRoom(createChatRoomRequest, chatRoomEntity);
        return chatRoomEntity;
    }

    private ChatRoomEntity saveChatRoom(CreateChatRoomRequest createChatRoomRequest, BookEntity bookEntity, UserEntity host, String fileUrl) {
        ChatRoomEntity chatRoomEntity = chatRoomRepository.save(createChatRoomRequest.makeChatRoom(bookEntity, host, fileUrl));
        saveParticipantWithRoomHostAndRoom(host, chatRoomEntity);
        return chatRoomEntity;
    }

    private void saveParticipantWithRoomHostAndRoom(UserEntity host, ChatRoomEntity chatRoomEntity) {
        ParticipantEntity participantEntity = ParticipantEntity.builder()
            .participantStatus(HOST)
            .chatRoomEntity(chatRoomEntity)
            .userEntity(host)
            .build();
        participantRepository.save(participantEntity);
    }

    private void registerHashTagOnChatRoom(CreateChatRoomRequest createChatRoomRequest, ChatRoomEntity chatRoomEntity) {
        createChatRoomRequest.getHashTags().stream()
            .map(tagName -> hashTagRepository.findByTagName(tagName)
                .orElseGet(() -> hashTagRepository.save(HashTagEntity.of(tagName))))
            .forEach(
                hashTag -> chatRoomHashTagRepository.save(ChatRoomHashTagEntity.of(chatRoomEntity, hashTag)));
    }

    @Transactional(readOnly = true)
    public UserChatRoomsResponseSlice getUserChatRooms(Long bookId, Long postCursorId, Pageable pageable, Long userId) {
        return UserChatRoomsResponseSlice.of(chatRoomRepository.findUserChatRoomsWithLastChat(pageable, bookId, postCursorId, userId));
    }

    @Transactional(readOnly = true)
    public UserChatRoomDetailResponse getUserChatRoomDetails(Long roomId, Long userId) {
        ChatRoomEntity chatroom = chatRoomRepository.findUserChatRoom(roomId, userId).orElseThrow(ChatRoomNotFoundException::new);
        Long roomMemberCount = participantRepository.countByChatRoomEntity(chatroom);

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
        ChatRoomEntity chatRoomEntity = chatRoomRepository.findChatRoomByIdAndHostId(reviseChatRoomRequest.getRoomId(), userId).orElseThrow(ChatRoomNotFoundException::new);

        updateIfChatRoomHashTagsPresent(reviseChatRoomRequest, chatRoomEntity);
        if (chatRoomImageExistent(chatRoomImage)) {
            String roomImageUri = storageService.upload(chatRoomImage, UUID.randomUUID().toString(), LocalDateTime.now().format(dateTimeFormatter));
            chatRoomEntity.changeRoomImageUri(roomImageUri);
        }
        reviseChatRoomRequest.reviseChatRoom(chatRoomEntity);
    }

    private void updateIfChatRoomHashTagsPresent(ReviseChatRoomRequest reviseChatRoomRequest, ChatRoomEntity chatRoomEntity) {
        if (reviseChatRoomRequest.tagExistent()) {
            chatRoomHashTagRepository.deleteAllByChatRoomEntity(chatRoomEntity);

            List<HashTagEntity> hashTagEntities = reviseChatRoomRequest.createHashTag();
            hashTagRepository.saveAll(hashTagEntities);

            List<ChatRoomHashTagEntity> chatRoomHashTagEntities = hashTagEntities.stream()
                .map(ht -> ChatRoomHashTagEntity.of(chatRoomEntity, ht))
                .collect(Collectors.toList());
            chatRoomHashTagRepository.saveAll(chatRoomHashTagEntities);
        }
    }

    @Transactional
    public void enterChatRoom(Long userId, Long roomId) {
        UserEntity userEntity = userReader.readUser(userId);
        ChatRoomEntity chatRoomEntity = chatRoomRepository.findById(roomId).orElseThrow(ChatRoomNotFoundException::new);
        participantRepository.findByUserIdAndChatRoomId(userEntity.getId(), chatRoomEntity.getId())
            .ifPresent(p -> {
                throw new AlreadyParticipateException();
            });

        if (chatRoomEntity.getHost() != userEntity) {
            checkIsBlockedUser(userEntity, chatRoomEntity);
            checkIsFullChatRoom(chatRoomEntity);

            participantRepository.save(ParticipantEntity.builder()
                .participantStatus(GUEST)
                .userEntity(userEntity)
                .chatRoomEntity(chatRoomEntity)
                .build());
        }

        ChatEntity chatEntity = chatRepository.save(ChatEntity.builder()
            .chatRoomEntity(chatRoomEntity)
            .message("#" + userEntity.getId() + "#님이 입장하셨습니다.")
            .build());

        messagePublisher.sendNotificationMessage(chatRoomEntity.getRoomSid(),
            NotificationMessage.createEntranceMessage(chatEntity, userEntity.getId()));
    }

    private void checkIsFullChatRoom(ChatRoomEntity chatRoomEntity) {
        List<ParticipantEntity> participantEntities = participantRepository.findWithPessimisticLockByChatRoomEntity(chatRoomEntity);

        if (chatRoomEntity.getRoomSize() <= participantEntities.size()) {
            throw new ChatRoomIsFullException();
        }
    }

    private void checkIsBlockedUser(UserEntity userEntity, ChatRoomEntity chatRoomEntity) {
        chatRoomBlockedUserRepository.findByUserIdAndChatRoomId(userEntity.getId(), chatRoomEntity.getId())
            .ifPresent(b -> {
                throw new BlockedUserInChatRoomException();
            });
    }

    @Transactional
    public void exitChatRoom(Long userId, Long roomId) {
        chatRoomRepository.findById(roomId).orElseThrow(ChatRoomNotFoundException::new);
        ParticipantEntity participantEntity = participantRepository.findByUserIdAndChatRoomId(userId, roomId)
            .orElseThrow(ParticipantNotFoundException::new);

        UserEntity userEntity = participantEntity.getUserEntity();
        ChatRoomEntity chatRoomEntity = participantEntity.getChatRoomEntity();

        if (userEntity == chatRoomEntity.getHost()) {
            ChatEntity chatEntity = createExitChat(chatRoomEntity, userEntity);
            participantRepository.deleteByChatRoomEntity(chatRoomEntity);
            chatRoomRepository.delete(chatRoomEntity);
            messagePublisher.sendNotificationMessage(chatRoomEntity.getRoomSid(), NotificationMessage.createHostExitMessage(chatEntity));
            return;
        }

        ChatEntity chatEntity = createExitChat(chatRoomEntity, userEntity);

        participantRepository.delete(participantEntity);
        messagePublisher.sendNotificationMessage(chatRoomEntity.getRoomSid(), NotificationMessage.createExitMessage(chatEntity, userEntity.getId()));
    }

    private ChatEntity createExitChat(ChatRoomEntity chatRoomEntity, UserEntity userEntity) {
        ChatEntity chatEntity = chatRepository.save(ChatEntity.builder()
            .chatRoomEntity(chatRoomEntity)
            .message("#" + userEntity.getId() + "#님이 퇴장하셨습니다.")
            .build());
        return chatEntity;
    }

}
