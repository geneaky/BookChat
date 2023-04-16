package toy.bookchat.bookchat.domain.chat.service;

import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.GUEST;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chat.repository.ChatRepository;
import toy.bookchat.bookchat.domain.chat.service.dto.request.ChatDto;
import toy.bookchat.bookchat.domain.chat.service.dto.response.ChatRoomChatsResponse;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomBlockedUserRepository;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.exception.chatroom.BlockedUserInChatRoomException;
import toy.bookchat.bookchat.exception.chatroom.ChatRoomIsFullException;
import toy.bookchat.bookchat.exception.chatroom.ChatRoomNotFoundException;
import toy.bookchat.bookchat.exception.participant.NotParticipatedException;
import toy.bookchat.bookchat.exception.participant.ParticipantNotFoundException;
import toy.bookchat.bookchat.exception.user.UserNotFoundException;

@Service
public class ChatService {

    public final String DESTINATION_PREFIX = "/topic/";
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ParticipantRepository participantRepository;
    private final ChatRoomBlockedUserRepository chatRoomBlockedUserRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatService(ChatRepository chatRepository, UserRepository userRepository,
        ChatRoomRepository chatRoomRepository,
        ParticipantRepository participantRepository,
        ChatRoomBlockedUserRepository chatRoomBlockedUserRepository,
        SimpMessagingTemplate messagingTemplate) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.participantRepository = participantRepository;
        this.chatRoomBlockedUserRepository = chatRoomBlockedUserRepository;
        this.messagingTemplate = messagingTemplate;
    }

    private String getDestination(String roomSid) {
        return DESTINATION_PREFIX + roomSid;
    }

    @Transactional
    public void enterChatRoom(Long userId, Long roomId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
            .orElseThrow(ChatRoomNotFoundException::new);

        checkIsBlockedUser(user, chatRoom);
        checkIsFullChatRoom(chatRoom);

        participantRepository.save(Participant.builder()
            .participantStatus(GUEST)
            .user(user)
            .chatRoom(chatRoom)
            .build());

        Chat chat = chatRepository.save(Chat.builder()
            .user(user)
            .chatRoom(chatRoom)
            .message(getWelcomeMessage(user.getNickname()))
            .build());

        messagingTemplate.convertAndSend(getDestination(chatRoom.getRoomSid()),
            ChatDto.from(user, chat));
    }

    private void checkIsFullChatRoom(ChatRoom chatRoom) {
        List<Participant> participants = participantRepository.findWithPessimisticLockByChatRoom(
            chatRoom);

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

    private String getWelcomeMessage(String userNickname) {
        return userNickname + "님이 입장하셨습니다.";
    }

    @Transactional
    public void leaveChatRoom(Long userId, Long roomId) {
        Participant participant = participantRepository.findByUserIdAndChatRoomId(userId, roomId)
            .orElseThrow(ParticipantNotFoundException::new);

        User user = participant.getUser();
        ChatRoom chatRoom = participant.getChatRoom();

        if (user == chatRoom.getHost()) {
            chatRepository.deleteByChatRoom(chatRoom);
            participantRepository.deleteByChatRoom(chatRoom);
            chatRoomRepository.delete(chatRoom);

            return;
        }

        Chat chat = chatRepository.save(Chat.builder()
            .user(user)
            .chatRoom(chatRoom)
            .message(getSendOffMessage(user.getNickname()))
            .build());

        participantRepository.delete(participant);
        messagingTemplate.convertAndSend(getDestination(chatRoom.getRoomSid()),
            ChatDto.from(user, chat));
    }

    private String getSendOffMessage(String userNickname) {
        return userNickname + "님이 퇴장하셨습니다.";
    }

    @Transactional
    public void sendMessage(Long userId, Long roomId, String message) {
        Participant participant = participantRepository.findByUserIdAndChatRoomId(userId, roomId)
            .orElseThrow(NotParticipatedException::new);

        Chat chat = chatRepository.save(Chat.builder()
            .user(participant.getUser())
            .chatRoom(participant.getChatRoom())
            .message(message)
            .build());

        messagingTemplate.convertAndSend(getDestination(participant.getChatRoomSid()),
            ChatDto.from(participant.getUser(), chat));
    }

    @Transactional(readOnly = true)
    public ChatRoomChatsResponse getChatRoomChats(Long roomId, Optional<Long> postCursorId,
        Pageable pageable, Long userId) {
        return new ChatRoomChatsResponse(
            chatRepository.getChatRoomChats(roomId, postCursorId, pageable, userId));
    }
}
