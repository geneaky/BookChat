package toy.bookchat.bookchat.domain.chat.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chat.api.dto.ChatDto;
import toy.bookchat.bookchat.domain.chat.repository.ChatRepository;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.exception.participant.AlreadyParticipatedException;

@Service
public class ChatService {

    public static final String DESTINATION_PREFIX = "/topic/";
    private final ChatRepository chatRepository;
    private final ParticipantRepository participantRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatCacheService chatCacheService;


    public ChatService(ChatRepository chatRepository, ParticipantRepository participantRepository,
        SimpMessagingTemplate messagingTemplate, ChatCacheService chatCacheService) {
        this.chatRepository = chatRepository;
        this.participantRepository = participantRepository;
        this.messagingTemplate = messagingTemplate;
        this.chatCacheService = chatCacheService;
    }

    @Transactional
    public void enterChatRoom(Long userId, String roomSid) {
        User user = chatCacheService.findUserByUserId(userId);
        ChatRoom chatRoom = chatCacheService.findChatRoomByRoomSid(roomSid);
        participantRepository.findByUserAndChatRoom(user, chatRoom).ifPresent(p -> {
            throw new AlreadyParticipatedException();
        });

        Chat chat = Chat.builder()
            .chatRoom(chatRoom)
            .user(user)
            .message(getWelcomeMessage(user))
            .build();

        Participant participant = Participant.builder()
            .chatRoom(chatRoom)
            .user(user)
            .build();

        ChatDto chatDto = ChatDto.builder()
            .message(chat.getMessage())
            .build();

        participantRepository.save(participant);
        chatCacheService.saveParticipantCache(user, chatRoom, participant);
        chatRepository.save(chat);
        messagingTemplate.convertAndSend(getDestination(roomSid),
            chatDto);
    }

    private static String getDestination(String roomSid) {
        StringBuilder stringBuilder = new StringBuilder(DESTINATION_PREFIX);
        stringBuilder.append(roomSid);
        return stringBuilder.toString();
    }

    private String getWelcomeMessage(User user) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(user.getNickname());
        stringBuilder.append("님이 입장하셨습니다.");
        return stringBuilder.toString();
    }

    @Transactional
    public void leaveChatRoom(Long userId, String roomSid) {
        User user = chatCacheService.findUserByUserId(userId);
        ChatRoom chatRoom = chatCacheService.findChatRoomByRoomSid(roomSid);
        Participant participant = chatCacheService.findParticipantByUserAndChatRoom(user, chatRoom);

        Chat chat = Chat.builder()
            .chatRoom(chatRoom)
            .user(user)
            .message(getSendOffMessage(user))
            .build();

        ChatDto chatDto = ChatDto.builder()
            .message(chat.getMessage())
            .build();

        participantRepository.delete(participant);
        chatCacheService.deleteParticipantCache(user, chatRoom);
        chatRepository.save(chat);
        messagingTemplate.convertAndSend(getDestination(roomSid),
            chatDto);
    }

    private static String getSendOffMessage(User user) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(user.getNickname());
        stringBuilder.append("님이 퇴장하셨습니다.");
        return stringBuilder.toString();
    }

    @Transactional
    public void sendMessage(Long userId, String roomSid, ChatDto chatDto) {
        User user = chatCacheService.findUserByUserId(userId);
        ChatRoom chatRoom = chatCacheService.findChatRoomByRoomSid(roomSid);
        chatCacheService.findParticipantByUserAndChatRoom(user, chatRoom);

        Chat chat = Chat.builder()
            .chatRoom(chatRoom)
            .user(user)
            .message(chatDto.getMessage())
            .build();

        chatRepository.save(chat);
        messagingTemplate.convertAndSend(getDestination(roomSid),
            chatDto);
    }
}
