package toy.bookchat.bookchat.domain.chat.service;

import static toy.bookchat.bookchat.infrastructure.push.PushType.CHAT;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chat.api.dto.request.MessageDto;
import toy.bookchat.bookchat.domain.chat.api.dto.response.ChatDetailResponse;
import toy.bookchat.bookchat.domain.chat.repository.ChatRepository;
import toy.bookchat.bookchat.domain.chat.service.dto.response.ChatRoomChatsResponse;
import toy.bookchat.bookchat.domain.device.Device;
import toy.bookchat.bookchat.domain.device.repository.DeviceRepository;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.exception.badrequest.participant.NotParticipatedException;
import toy.bookchat.bookchat.infrastructure.broker.MessagePublisher;
import toy.bookchat.bookchat.infrastructure.broker.message.CommonMessage;
import toy.bookchat.bookchat.infrastructure.push.PushMessageBody;
import toy.bookchat.bookchat.infrastructure.push.service.PushService;

@Service
public class ChatService {

    private static final int SEPARATE_LENGTH = 1000;
    private final ChatRepository chatRepository;
    private final ParticipantRepository participantRepository;
    private final DeviceRepository deviceRepository;
    private final MessagePublisher messagePublisher;
    private final PushService pushService;

    public ChatService(ChatRepository chatRepository, ParticipantRepository participantRepository,
        DeviceRepository deviceRepository, MessagePublisher messagePublisher,
        PushService pushService) {
        this.chatRepository = chatRepository;
        this.participantRepository = participantRepository;
        this.deviceRepository = deviceRepository;
        this.messagePublisher = messagePublisher;
        this.pushService = pushService;
    }

    @Transactional
    public void sendMessage(Long userId, Long roomId, MessageDto messageDto) {
        Participant participant = participantRepository.findByUserIdAndChatRoomId(userId, roomId)
            .orElseThrow(NotParticipatedException::new);

        List<Device> disconnectedUserDevice = deviceRepository.getDisconnectedUserDevice(roomId);

        Chat chat = chatRepository.save(Chat.builder()
            .user(participant.getUser())
            .chatRoom(participant.getChatRoom())
            .message(messageDto.getMessage())
            .build());

        CommonMessage message = CommonMessage.from(participant.getUserId(), chat, messageDto);
        PushMessageBody pushMessageBody = PushMessageBody.of(CHAT, chat.getId());
        for (Device device : disconnectedUserDevice) {
            pushService.send(device.getFcmToken(), pushMessageBody);
        }

        messagePublisher.sendCommonMessage(participant.getChatRoomSid(), message);
    }

    @Transactional(readOnly = true)
    public ChatRoomChatsResponse getChatRoomChats(Long roomId, Long postCursorId, Pageable pageable,
        Long userId) {
        return new ChatRoomChatsResponse(
            chatRepository.getChatRoomChats(roomId, postCursorId, pageable, userId));
    }

    @Transactional(readOnly = true)
    public ChatDetailResponse getChatDetail(Long chatId, Long userId) {
        Chat chat = chatRepository.getUserChatRoomChat(chatId, userId).orElseThrow(NotParticipatedException::new);
        return ChatDetailResponse.from(chat);
    }
}
