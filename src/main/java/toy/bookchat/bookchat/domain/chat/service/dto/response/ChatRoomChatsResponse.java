package toy.bookchat.bookchat.domain.chat.service.dto.response;

import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.domain.Slice;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.common.CursorMeta;

@Getter
@EqualsAndHashCode(exclude = "cursorMeta")
public class ChatRoomChatsResponse {

    private List<ChatResponse> chatResponseList;
    private CursorMeta<Chat, Long> cursorMeta;

    public ChatRoomChatsResponse(Slice<Chat> chatSlice) {
        this.cursorMeta = new CursorMeta<>(chatSlice, Chat::getId);
        this.chatResponseList = chatSlice.stream().map(this::createChatResponse)
            .collect(Collectors.toList());
    }

    private ChatResponse createChatResponse(Chat chat) {
        if (chat.isAnnouncementChat()) {
            return ChatResponse.builder()
                .chatId(chat.getId())
                .message(chat.getMessage())
                .dispatchTime(chat.getDispatchTime())
                .build();
        }
        return ChatResponse.builder()
            .chatId(chat.getId())
            .senderId(chat.getUserId())
            .message(chat.getMessage())
            .dispatchTime(chat.getDispatchTime())
            .build();
    }
}
