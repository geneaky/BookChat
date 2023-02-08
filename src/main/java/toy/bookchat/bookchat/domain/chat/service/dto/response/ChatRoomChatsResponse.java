package toy.bookchat.bookchat.domain.chat.service.dto.response;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Slice;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.common.CursorMeta;

@Getter
public class ChatRoomChatsResponse {

    private List<ChatResponse> chatResponseList;
    private CursorMeta<Chat, Long> cursorMeta;

    public ChatRoomChatsResponse(Slice<Chat> chatSlice) {
        this.cursorMeta = new CursorMeta<>(chatSlice, Chat::getId);
        this.chatResponseList = from(chatSlice.getContent());
    }

    private List<ChatResponse> from(List<Chat> chatSlice) {
        List<ChatResponse> chatResponseList = new ArrayList<>();
        for (Chat chat : chatSlice) {
            chatResponseList.add(ChatResponse.builder()
                .chatId(chat.getId())
                .senderId(chat.getUserId())
                .senderNickname(chat.getUserNickname())
                .senderProfileImageUrl(chat.getUserProfileImageUrl())
                .snederDefaultProfileImageType(chat.getUserDefaultProfileImageType())
                .message(chat.getMessage())
                .dispatchTime(chat.getDispatchTime())
                .build());
        }
        return chatResponseList;
    }
}
