package toy.bookchat.bookchat.domain.chat.service.dto.response;

import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.domain.Slice;
import toy.bookchat.bookchat.db_module.chat.ChatEntity;
import toy.bookchat.bookchat.domain.common.CursorMeta;

@Getter
@EqualsAndHashCode(exclude = "cursorMeta")
public class ChatRoomChatsResponse {

    private List<ChatResponse> chatResponseList;
    private CursorMeta<ChatEntity, Long> cursorMeta;

    public ChatRoomChatsResponse(Slice<ChatEntity> chatSlice) {
        this.cursorMeta = new CursorMeta<>(chatSlice, ChatEntity::getId);
        this.chatResponseList = chatSlice.stream().map(this::createChatResponse)
            .collect(Collectors.toList());
    }

    private ChatResponse createChatResponse(ChatEntity chatEntity) {
        if (chatEntity.isAnnouncementChat()) {
            return ChatResponse.builder()
                .chatId(chatEntity.getId())
                .message(chatEntity.getMessage())
                .dispatchTime(chatEntity.getDispatchTime())
                .build();
        }
        return ChatResponse.builder()
            .chatId(chatEntity.getId())
            .senderId(chatEntity.getUserId())
            .message(chatEntity.getMessage())
            .dispatchTime(chatEntity.getDispatchTime())
            .build();
    }
}
