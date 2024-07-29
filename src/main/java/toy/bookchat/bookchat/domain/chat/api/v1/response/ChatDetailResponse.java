package toy.bookchat.bookchat.domain.chat.api.v1.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.domain.chat.Chat;

@Getter
public class ChatDetailResponse {

    private Long chatId;
    private Long chatRoomId;
    private String message;
    private LocalDateTime dispatchTime;
    private ChatSenderResponse sender;

    @Builder
    private ChatDetailResponse(Long chatId, Long chatRoomId, String message, LocalDateTime dispatchTime, ChatSenderResponse sender) {
        this.chatId = chatId;
        this.chatRoomId = chatRoomId;
        this.message = message;
        this.dispatchTime = dispatchTime;
        this.sender = sender;
    }

    public static ChatDetailResponse from(Chat chat) {
        return ChatDetailResponse.builder()
            .chatId(chat.getId())
            .chatRoomId(chat.getChatRoomId())
            .message(chat.getMessage())
            .dispatchTime(chat.getDispatchTime())
            .sender(ChatSenderResponse.from(chat.getSender()))
            .build();
    }
}
