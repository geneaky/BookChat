package toy.bookchat.bookchat.domain.chat.service.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.user.User;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatDto {


    private Long chatId;
    private Long senderId;
    private String senderNickname;
    private String senderProfileImageUrl;
    private Integer senderDefaultProfileImageType;
    private String dispatchTime;
    @NotBlank
    private String message;

    @Builder
    private ChatDto(Long chatId, Long senderId, String senderNickname, String senderProfileImageUrl,
        Integer senderDefaultProfileImageType, String dispatchTime, String message) {
        this.chatId = chatId;
        this.senderId = senderId;
        this.senderNickname = senderNickname;
        this.senderProfileImageUrl = senderProfileImageUrl;
        this.senderDefaultProfileImageType = senderDefaultProfileImageType;
        this.dispatchTime = dispatchTime;
        this.message = message;
    }

    public static ChatDto from(User user, Chat chat) {
        return ChatDto.builder()
            .senderId(user.getId())
            .senderNickname(user.getNickname())
            .senderProfileImageUrl(user.getProfileImageUrl())
            .senderDefaultProfileImageType(user.getDefaultProfileImageType())
            .chatId(chat.getId())
            .dispatchTime(chat.getDispatchTime())
            .message(chat.getMessage())
            .build();
    }

    public static ChatDto announcement(Chat chat) {
        return ChatDto.builder()
            .chatId(chat.getId())
            .message(chat.getMessage())
            .dispatchTime(chat.getDispatchTime())
            .build();
    }
}
