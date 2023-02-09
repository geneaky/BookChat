package toy.bookchat.bookchat.domain.chat.service.dto.response;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class ChatResponse {

    private Long chatId;
    private Long senderId;
    private String senderNickname;
    private String senderProfileImageUrl;
    private Integer senderDefaultProfileImageType;
    private String message;
    private String dispatchTime;

    @Builder
    private ChatResponse(Long chatId, Long senderId, String senderNickname,
        String senderProfileImageUrl,
        Integer senderDefaultProfileImageType, String message, String dispatchTime) {
        this.chatId = chatId;
        this.senderId = senderId;
        this.senderNickname = senderNickname;
        this.senderProfileImageUrl = senderProfileImageUrl;
        this.senderDefaultProfileImageType = senderDefaultProfileImageType;
        this.message = message;
        this.dispatchTime = dispatchTime;
    }
}
