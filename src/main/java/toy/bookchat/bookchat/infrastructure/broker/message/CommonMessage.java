package toy.bookchat.bookchat.infrastructure.broker.message;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.db_module.chat.ChatEntity;
import toy.bookchat.bookchat.domain.chat.api.dto.request.MessageDto;

@Getter
@EqualsAndHashCode(of = {"chatId", "senderId"})
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonMessage {

    private Long chatRoomId;
    private Long chatId;
    private Long senderId;
    private Integer receiptId;
    private String message;
    private String dispatchTime;

    @Builder
    private CommonMessage(Long chatRoomId, Long chatId, Long senderId, Integer receiptId,
        String dispatchTime,
        String message) {
        this.chatRoomId = chatRoomId;
        this.chatId = chatId;
        this.senderId = senderId;
        this.receiptId = receiptId;
        this.message = message;
        this.dispatchTime = dispatchTime;
    }

    public static CommonMessage from(Long senderId, ChatEntity chatEntity, MessageDto messageDto) {
        return CommonMessage.builder()
            .chatRoomId(chatEntity.getChatRoomId())
            .chatId(chatEntity.getId())
            .senderId(senderId)
            .receiptId(messageDto.getReceiptId())
            .message(messageDto.getMessage())
            .dispatchTime(chatEntity.getDispatchTime())
            .build();
    }

    public static CommonMessage from(Long senderId, ChatEntity chatEntity, MessageDto messageDto,
        String subMessage) {
        return CommonMessage.builder()
            .chatRoomId(chatEntity.getChatRoomId())
            .chatId(chatEntity.getId())
            .senderId(senderId)
            .receiptId(messageDto.getReceiptId())
            .message(subMessage)
            .dispatchTime(chatEntity.getDispatchTime())
            .build();
    }
}
