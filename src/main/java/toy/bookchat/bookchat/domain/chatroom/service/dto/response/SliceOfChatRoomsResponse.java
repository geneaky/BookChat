package toy.bookchat.bookchat.domain.chatroom.service.dto.response;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Slice;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.common.CursorMeta;

@Getter
public class SliceOfChatRoomsResponse {

    private List<ChatRoomResponse> chatRoomResponseList;
    private CursorMeta cursorMeta;

    private SliceOfChatRoomsResponse(Slice<Chat> slice) {
        this.cursorMeta = CursorMeta.from(slice);
        this.chatRoomResponseList = from(slice.getContent());
    }

    public static SliceOfChatRoomsResponse of(Slice<Chat> slice) {
        return new SliceOfChatRoomsResponse(slice);
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
