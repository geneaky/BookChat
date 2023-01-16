package toy.bookchat.bookchat.domain.chat.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chat.api.dto.ChatDto;
import toy.bookchat.bookchat.domain.chat.repository.ChatRepository;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.exception.chatroom.ChatRoomNotFoundException;
import toy.bookchat.bookchat.exception.participant.AlreadyParticipatedException;
import toy.bookchat.bookchat.exception.participant.NotParticipatedException;
import toy.bookchat.bookchat.exception.user.UserNotFoundException;

@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;
    private final SimpMessagingTemplate messagingTemplate;


    public ChatService(ChatRepository chatRepository, ChatRoomRepository chatRoomRepository,
        UserRepository userRepository, ParticipantRepository participantRepository,
        SimpMessagingTemplate messagingTemplate) {
        this.chatRepository = chatRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
        this.participantRepository = participantRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public void enterChatRoom(Long userId, String chatRoomSid) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        ChatRoom chatRoom = chatRoomRepository.findByRoomSid(chatRoomSid).orElseThrow(
            ChatRoomNotFoundException::new);
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
        /* TODO: 2023-01-16 cache 등록
         */
        chatRepository.save(chat);
        messagingTemplate.convertAndSend("/topic/" + chatRoomSid,
            chatDto);
    }

    private String getWelcomeMessage(User user) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(user.getNickname());
        stringBuilder.append("님이 입장하셨습니다.");
        return stringBuilder.toString();
    }

    @Transactional
    public void leaveChatRoom(Long userId, String chatRoomSid) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        ChatRoom chatRoom = chatRoomRepository.findByRoomSid(chatRoomSid)
            .orElseThrow(ChatRoomNotFoundException::new);
        Participant participant = participantRepository.findByUserAndChatRoom(user, chatRoom)
            .orElseThrow(NotParticipatedException::new);

        Chat chat = Chat.builder()
            .chatRoom(chatRoom)
            .user(user)
            .message(getSendOffMessage(user))
            .build();

        ChatDto chatDto = ChatDto.builder()
            .message(chat.getMessage())
            .build();

        participantRepository.delete(participant);
        /* TODO: 2023-01-16 cache 삭제
         */
        chatRepository.save(chat);
        messagingTemplate.convertAndSend("/topic/" + chatRoomSid,
            chatDto);
    }

    private static String getSendOffMessage(User user) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(user.getNickname());
        stringBuilder.append("님이 퇴장하셨습니다.");
        return stringBuilder.toString();
    }

    @Transactional
    public void sendMessage(Long userId, String chatRoomSid, ChatDto chatDto) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        ChatRoom chatRoom = chatRoomRepository.findByRoomSid(chatRoomSid)
            .orElseThrow(ChatRoomNotFoundException::new);
        participantRepository.findByUserAndChatRoom(user, chatRoom)
            .orElseThrow(NotParticipatedException::new);
        /* TODO: 2023-01-16 cache 등록, 조회
         */

        Chat chat = Chat.builder()
            .chatRoom(chatRoom)
            .user(user)
            .message(chatDto.getMessage())
            .build();

        chatRepository.save(chat);
        messagingTemplate.convertAndSend("/topic/" + chatRoomSid,
            chatDto);
    }
}
