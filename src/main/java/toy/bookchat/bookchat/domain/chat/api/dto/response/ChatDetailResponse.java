package toy.bookchat.bookchat.domain.chat.api.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.domain.chat.ChatEntity;

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

    public static ChatDetailResponse from(ChatEntity chatEntity) {
        return ChatDetailResponse.builder()
            .chatId(chatEntity.getId())
            .chatRoomId(chatEntity.getChatRoomEntity().getId())
            .message(chatEntity.getMessage())
            .dispatchTime(chatEntity.getCreatedAt())
            .sender(ChatSender.from(chatEntity.getUserEntity()))
            .build();
    }
}
