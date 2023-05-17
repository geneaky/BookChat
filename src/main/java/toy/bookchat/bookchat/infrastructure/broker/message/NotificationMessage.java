package toy.bookchat.bookchat.infrastructure.broker.message;

import lombok.*;
import toy.bookchat.bookchat.domain.chat.Chat;

import javax.validation.constraints.NotBlank;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationMessage {

    private Long targetId;
    private Long chatId;
    @NotBlank
    private String message;
    private String dispatchTime;
    private MessageType messageType;

    @Builder
    private NotificationMessage(Long targetId, Long chatId, String dispatchTime, MessageType messageType, String message) {
        this.targetId = targetId;
        this.chatId = chatId;
        this.dispatchTime = dispatchTime;
        this.messageType = messageType;
        this.message = message;
    }

    public static NotificationMessage createEntranceMessage(Chat chat, Long targetId) {
        return NotificationMessage.builder()
                .targetId(targetId)
                .chatId(chat.getId())
                .message(chat.getMessage())
                .messageType(MessageType.ENTER)
                .dispatchTime(chat.getDispatchTime())
                .build();
    }

    public static NotificationMessage createExitMessage(Chat chat, Long targetId) {
        return NotificationMessage.builder()
                .targetId(targetId)
                .chatId(chat.getId())
                .message(chat.getMessage())
                .messageType(MessageType.EXIT)
                .dispatchTime(chat.getDispatchTime())
                .build();
    }

    public static NotificationMessage createSubHostDelegateMessage(Chat chat, Long targetId) {
        return NotificationMessage.builder()
                .targetId(targetId)
                .chatId(chat.getId())
                .message(chat.getMessage())
                .messageType(MessageType.NOTICE_SUB_HOST_DELEGATE)
                .dispatchTime(chat.getDispatchTime())
                .build();
    }

    public static NotificationMessage createSubHostDismissMessage(Chat chat, Long targetId) {
        return NotificationMessage.builder()
                .targetId(targetId)
                .chatId(chat.getId())
                .message(chat.getMessage())
                .messageType(MessageType.NOTICE_SUB_HOST_DISMISS)
                .dispatchTime(chat.getDispatchTime())
                .build();
    }

    public static NotificationMessage createHostDelegateMessage(Chat chat, Long targetId) {
        return NotificationMessage.builder()
                .targetId(targetId)
                .chatId(chat.getId())
                .message(chat.getMessage())
                .messageType(MessageType.NOTICE_HOST_DELEGATE)
                .dispatchTime(chat.getDispatchTime())
                .build();
    }

    public static NotificationMessage createKickMessage(Chat chat, Long targetId) {
        return NotificationMessage.builder()
                .targetId(targetId)
                .chatId(chat.getId())
                .message(chat.getMessage())
                .messageType(MessageType.NOTICE_KICK)
                .dispatchTime(chat.getDispatchTime())
                .build();
    }
}
