package toy.bookchat.bookchat.infrastructure.broker.message;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.chat.Chat;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationMessage {

    private Long targetId;
    private Long chatId;
    @NotBlank
    private String message;
    private String dispatchTime;
    private NotificationMessageType notificationMessageType;

    @Builder
    private NotificationMessage(Long targetId, Long chatId, String dispatchTime,
        NotificationMessageType notificationMessageType, String message) {
        this.targetId = targetId;
        this.chatId = chatId;
        this.dispatchTime = dispatchTime;
        this.notificationMessageType = notificationMessageType;
        this.message = message;
    }

    public static NotificationMessage createEntranceMessage(Chat chat, Long targetId) {
        return NotificationMessage.builder()
            .targetId(targetId)
            .chatId(chat.getId())
            .message(chat.getMessage())
            .notificationMessageType(NotificationMessageType.NOTICE_ENTER)
            .dispatchTime(chat.getDispatchTime())
            .build();
    }

    public static NotificationMessage createExitMessage(Chat chat, Long targetId) {
        return NotificationMessage.builder()
            .targetId(targetId)
            .chatId(chat.getId())
            .message(chat.getMessage())
            .notificationMessageType(NotificationMessageType.NOTICE_EXIT)
            .dispatchTime(chat.getDispatchTime())
            .build();
    }

    public static NotificationMessage createHostExitMessage(Chat chat) {
        return NotificationMessage.builder()
            .chatId(chat.getId())
            .message("방장이 오픈채팅방을 종료했습니다.\n더 이상 대화를 할 수 없으며, \n채팅방을 나가면 다시 입장 할 수 없게 됩니다.")
            .notificationMessageType(NotificationMessageType.NOTICE_HOST_EXIT)
            .dispatchTime(chat.getDispatchTime())
            .build();
    }

    public static NotificationMessage createSubHostDelegateMessage(Chat chat, Long targetId) {
        return NotificationMessage.builder()
            .targetId(targetId)
            .chatId(chat.getId())
            .message(chat.getMessage())
            .notificationMessageType(NotificationMessageType.NOTICE_SUB_HOST_DELEGATE)
            .dispatchTime(chat.getDispatchTime())
            .build();
    }

    public static NotificationMessage createSubHostDismissMessage(Chat chat, Long targetId) {
        return NotificationMessage.builder()
            .targetId(targetId)
            .chatId(chat.getId())
            .message(chat.getMessage())
            .notificationMessageType(NotificationMessageType.NOTICE_SUB_HOST_DISMISS)
            .dispatchTime(chat.getDispatchTime())
            .build();
    }

    public static NotificationMessage createHostDelegateMessage(Chat chat, Long targetId) {
        return NotificationMessage.builder()
            .targetId(targetId)
            .chatId(chat.getId())
            .message(chat.getMessage())
            .notificationMessageType(NotificationMessageType.NOTICE_HOST_DELEGATE)
            .dispatchTime(chat.getDispatchTime())
            .build();
    }

    public static NotificationMessage createKickMessage(Chat chat, Long targetId) {
        return NotificationMessage.builder()
            .targetId(targetId)
            .chatId(chat.getId())
            .message(chat.getMessage())
            .notificationMessageType(NotificationMessageType.NOTICE_KICK)
            .dispatchTime(chat.getDispatchTime())
            .build();
    }
}
