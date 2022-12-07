package toy.bookchat.bookchat.domain.chatroom.service.dto.response;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Slice;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.common.CursorMeta;

@Getter
public class SliceOfChatRoomsResponse {

    private List<ChatRoomResponse> chatRoomResponseList;
    private CursorMeta<Long> cursorMeta;

    private SliceOfChatRoomsResponse(Slice<ChatRoom> slice) {
        this.cursorMeta = CursorMeta.from(slice, getNextCursorId(slice.getContent()));
        this.chatRoomResponseList = from(slice.getContent());
    }

    public static SliceOfChatRoomsResponse of(Slice<ChatRoom> slice) {
        return new SliceOfChatRoomsResponse(slice);
    }

    private Long getNextCursorId(List<ChatRoom> content) {
        if (content.isEmpty()) {
            return null;
        }
        return content.get(content.size() - 1).getId();
    }

    private List<ChatRoomResponse> from(List<ChatRoom> content) {
        List<ChatRoomResponse> result = new ArrayList<>();
        fillWithChatRoomResponse(content, result);
        return result;
    }

    private void fillWithChatRoomResponse(List<ChatRoom> content, List<ChatRoomResponse> result) {
        for (ChatRoom chatRoom : content) {
            result.add(ChatRoomResponse.builder()
                .roomId(chatRoom.getId())
                .roomName(chatRoom.getRoomName())
                .roomSid(chatRoom.getRoomSid())
                .defaultRoomImageType(chatRoom.getDefaultRoomImageType())
                .roomImageUri(chatRoom.getRoomImageUri())
                .lastChatContent(chatRoom.getLastMessage())
                .lastActiveTime(chatRoom.getLastSendTime())
                .build());
        }
    }
}
