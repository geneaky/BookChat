package toy.bookchat.bookchat.infrastructure.broker.message;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.chat.Chat;
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

    public static CommonMessage from(Long senderId, Chat chat, MessageDto messageDto) {
        return CommonMessage.builder()
            .chatRoomId(chat.getChatRoomId())
            .chatId(chat.getId())
            .senderId(senderId)
            .receiptId(messageDto.getReceiptId())
            .message(messageDto.getMessage())
            .dispatchTime(chat.getDispatchTime())
            .build();
    }
}
