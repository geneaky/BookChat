package toy.bookchat.bookchat.domain.chat.api.dto;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chat.service.cache.UserCache;

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

    @Builder
    private ChatDto(String message) {
        this.message = message;
    }

    public static ChatDto from(UserCache userCache, Chat chat) {
        return ChatDto.builder()
            .senderId(userCache.getUserId())
            .senderNickname(userCache.getUserNickname())
            .senderProfileImageUrl(userCache.getProfileImageUrl())
            .senderDefaultProfileImageType(userCache.getDefaultProfileImageType())
            .chatId(chat.getId())
            .dispatchTime(chat.getCreatedAt().toString())
            .message(chat.getMessage())
            .build();
    }
}
