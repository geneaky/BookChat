package toy.bookchat.bookchat.infrastructure.broker.message;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.chat.ChatEntity;

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

    public static NotificationMessage createEntranceMessage(ChatEntity chatEntity, Long targetId) {
        return NotificationMessage.builder()
            .targetId(targetId)
            .chatId(chatEntity.getId())
            .message(chatEntity.getMessage())
            .notificationMessageType(NotificationMessageType.NOTICE_ENTER)
            .dispatchTime(chatEntity.getDispatchTime())
            .build();
    }

    public static NotificationMessage createExitMessage(ChatEntity chatEntity, Long targetId) {
        return NotificationMessage.builder()
            .targetId(targetId)
            .chatId(chatEntity.getId())
            .message(chatEntity.getMessage())
            .notificationMessageType(NotificationMessageType.NOTICE_EXIT)
            .dispatchTime(chatEntity.getDispatchTime())
            .build();
    }

    public static NotificationMessage createHostExitMessage(ChatEntity chatEntity) {
        return NotificationMessage.builder()
            .chatId(chatEntity.getId())
            .message("방장이 오픈채팅방을 종료했습니다.\n더 이상 대화를 할 수 없으며, \n채팅방을 나가면 다시 입장 할 수 없게 됩니다.")
            .notificationMessageType(NotificationMessageType.NOTICE_HOST_EXIT)
            .dispatchTime(chatEntity.getDispatchTime())
            .build();
    }

    public static NotificationMessage createSubHostDelegateMessage(ChatEntity chatEntity, Long targetId) {
        return NotificationMessage.builder()
            .targetId(targetId)
            .chatId(chatEntity.getId())
            .message(chatEntity.getMessage())
            .notificationMessageType(NotificationMessageType.NOTICE_SUB_HOST_DELEGATE)
            .dispatchTime(chatEntity.getDispatchTime())
            .build();
    }

    public static NotificationMessage createSubHostDismissMessage(ChatEntity chatEntity, Long targetId) {
        return NotificationMessage.builder()
            .targetId(targetId)
            .chatId(chatEntity.getId())
            .message(chatEntity.getMessage())
            .notificationMessageType(NotificationMessageType.NOTICE_SUB_HOST_DISMISS)
            .dispatchTime(chatEntity.getDispatchTime())
            .build();
    }

    public static NotificationMessage createHostDelegateMessage(ChatEntity chatEntity, Long targetId) {
        return NotificationMessage.builder()
            .targetId(targetId)
            .chatId(chatEntity.getId())
            .message(chatEntity.getMessage())
            .notificationMessageType(NotificationMessageType.NOTICE_HOST_DELEGATE)
            .dispatchTime(chatEntity.getDispatchTime())
            .build();
    }

    public static NotificationMessage createKickMessage(ChatEntity chatEntity, Long targetId) {
        return NotificationMessage.builder()
            .targetId(targetId)
            .chatId(chatEntity.getId())
            .message(chatEntity.getMessage())
            .notificationMessageType(NotificationMessageType.NOTICE_KICK)
            .dispatchTime(chatEntity.getDispatchTime())
            .build();
    }
}
