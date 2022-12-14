package toy.bookchat.bookchat.domain.chatroom.repository.query.dto.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import org.springframework.data.domain.Slice;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.common.CursorMeta;

@Getter
public class ChatRoomsResponseSlice {

    private List<ChatRoomResponse> chatRoomResponseList;
    private CursorMeta<Long> cursorMeta;

    private ChatRoomsResponseSlice(Slice<ChatRoomResponse> slice) {
        this.cursorMeta = CursorMeta.from(slice, getNextCursorId(slice.getContent()));
        this.chatRoomResponseList = slice.getContent();
    }

    public static ChatRoomsResponseSlice of(Slice<ChatRoomResponse> slice) {
        return new ChatRoomsResponseSlice(slice);
    }

    private Long getNextCursorId(List<ChatRoomResponse> content) {
        return Optional.ofNullable(content.get(content.size() - 1))
            .map(ChatRoomResponse::getLastChatId)
            .orElse(null);
    }

    private List<ChatRoomResponse> from(List<Chat> content) {
        List<ChatRoomResponse> result = new ArrayList<>();
        fillWithChatRoomResponse(content, result);
        return result;
    }

    private void fillWithChatRoomResponse(List<Chat> content, List<ChatRoomResponse> result) {
        for (Chat chat : content) {
            result.add(ChatRoomResponse.builder()
                .roomId(chat.getChatRoom().getId())
                .roomName(chat.getChatRoom().getRoomName())
                .roomSid(chat.getChatRoom().getRoomSid())
                .defaultRoomImageType(chat.getChatRoom().getDefaultRoomImageType())
                .roomImageUri(chat.getChatRoom().getRoomImageUri())
                .lastChatContent(chat.getMessage())
                .lastActiveTime(chat.getCreatedAt())
                .build());
        }
    }
}
