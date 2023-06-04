package toy.bookchat.bookchat.domain.chat.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chat.api.dto.request.MessageDto;
import toy.bookchat.bookchat.domain.chat.repository.ChatRepository;
import toy.bookchat.bookchat.domain.chat.service.dto.response.ChatRoomChatsResponse;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.exception.badrequest.participant.NotParticipatedException;
import toy.bookchat.bookchat.infrastructure.broker.MessagePublisher;
import toy.bookchat.bookchat.infrastructure.broker.message.CommonMessage;

@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final ParticipantRepository participantRepository;
    private final MessagePublisher messagePublisher;

    public ChatService(ChatRepository chatRepository, ParticipantRepository participantRepository,
        MessagePublisher messagePublisher) {
        this.chatRepository = chatRepository;
        this.participantRepository = participantRepository;
        this.messagePublisher = messagePublisher;
    }

    @Transactional
    public void sendMessage(Long userId, Long roomId, MessageDto messageDto) {
        Participant participant = participantRepository.findByUserIdAndChatRoomId(userId, roomId)
            .orElseThrow(NotParticipatedException::new);

        Chat chat = chatRepository.save(Chat.builder()
            .user(participant.getUser())
            .chatRoom(participant.getChatRoom())
            .message(messageDto.getMessage())
            .build());

        messagePublisher.sendCommonMessage(participant.getChatRoomSid(),
            CommonMessage.from(participant.getUserId(), chat, messageDto));
    }

    @Transactional(readOnly = true)
    public ChatRoomChatsResponse getChatRoomChats(Long roomId, Long postCursorId, Pageable pageable,
        Long userId) {
        return new ChatRoomChatsResponse(
            chatRepository.getChatRoomChats(roomId, postCursorId, pageable, userId));
    }
}
