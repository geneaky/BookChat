package toy.bookchat.bookchat.domain.chat.service.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ChatResponse {

    private Long chatId;
    private Long senderId;
    private String senderNickname;
    private String senderProfileImageUrl;
    private Integer snederDefaultProfileImageType;
    private String message;
    private String dispatchTime;

    @Builder
    private ChatResponse(Long chatId, Long senderId, String senderNickname,
        String senderProfileImageUrl,
        Integer snederDefaultProfileImageType, String message, String dispatchTime) {
        this.chatId = chatId;
        this.senderId = senderId;
        this.senderNickname = senderNickname;
        this.senderProfileImageUrl = senderProfileImageUrl;
        this.snederDefaultProfileImageType = snederDefaultProfileImageType;
        this.message = message;
        this.dispatchTime = dispatchTime;
    }
}
