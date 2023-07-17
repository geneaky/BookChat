package toy.bookchat.bookchat.domain.chat.service.dto.response;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class ChatResponse {

    private Long chatId;
    private Long senderId;
    private String message;
    private String dispatchTime;

    @Builder
    private ChatResponse(Long chatId, Long senderId, String message, String dispatchTime) {
        this.chatId = chatId;
        this.senderId = senderId;
        this.message = message;
        this.dispatchTime = dispatchTime;
    }
}