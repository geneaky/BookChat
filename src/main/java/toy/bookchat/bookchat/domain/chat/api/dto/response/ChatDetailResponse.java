package toy.bookchat.bookchat.domain.chat.api.dto.response;

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
    private ChatSender sender;

    @Builder
    private ChatDetailResponse(Long chatId, Long chatRoomId, String message, LocalDateTime dispatchTime, ChatSender sender) {
        this.chatId = chatId;
        this.chatRoomId = chatRoomId;
        this.message = message;
        this.dispatchTime = dispatchTime;
        this.sender = sender;
    }

    public static ChatDetailResponse from(Chat chat) {
        return ChatDetailResponse.builder()
            .chatId(chat.getId())
            .chatRoomId(chat.getChatRoom().getId())
            .message(chat.getMessage())
            .dispatchTime(chat.getCreatedAt())
            .sender(ChatSender.from(chat.getUser()))
            .build();
    }
}
