package toy.bookchat.bookchat.infrastructure.broker.message;

import lombok.*;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.user.User;

import javax.validation.constraints.NotBlank;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonMessage {

    private Long chatId;
    @NotBlank
    private String message;
    private String dispatchTime;
    private MessageType messageType;
    private Long senderId;
    private String senderNickname;
    private String senderProfileImageUrl;
    private Integer senderDefaultProfileImageType;

    @Builder
    private CommonMessage(Long chatId, Long senderId, String senderNickname, String senderProfileImageUrl, Integer senderDefaultProfileImageType, String dispatchTime, MessageType messageType, String message) {
        this.chatId = chatId;
        this.senderId = senderId;
        this.senderNickname = senderNickname;
        this.senderProfileImageUrl = senderProfileImageUrl;
        this.senderDefaultProfileImageType = senderDefaultProfileImageType;
        this.dispatchTime = dispatchTime;
        this.messageType = MessageType.CHAT;
        this.message = message;
    }

    public static CommonMessage from(User user, Chat chat) {
        return CommonMessage.builder()
                .senderId(user.getId())
                .senderNickname(user.getNickname())
                .senderProfileImageUrl(user.getProfileImageUrl())
                .senderDefaultProfileImageType(user.getDefaultProfileImageType())
                .chatId(chat.getId())
                .dispatchTime(chat.getDispatchTime())
                .messageType(MessageType.CHAT)
                .message(chat.getMessage())
                .build();
    }
}
