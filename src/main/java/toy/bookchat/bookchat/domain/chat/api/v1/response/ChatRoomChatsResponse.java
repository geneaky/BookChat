package toy.bookchat.bookchat.domain.chat.api.v1.response;

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

    public ChatRoomChatsResponse(Slice<Chat> slicedChat) {
        this.cursorMeta = new CursorMeta<>(slicedChat, Chat::getId);
        this.chatResponseList = slicedChat.stream()
            .map(this::createChatResponse)
            .collect(Collectors.toList());
    }

    private ChatResponse createChatResponse(Chat chat) {
        if (chat.isAnnouncement()) {
            return ChatResponse.builder()
                .chatId(chat.getId())
                .message(chat.getMessage())
                .dispatchTime(chat.getDispatchTime().toString())
                .build();
        }
        return ChatResponse.builder()
            .chatId(chat.getId())
            .senderId(chat.getSenderId())
            .message(chat.getMessage())
            .dispatchTime(chat.getDispatchTime().toString())
            .build();
    }
}
