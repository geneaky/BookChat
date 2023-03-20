package toy.bookchat.bookchat.domain.chatroom.repository.query.dto.response;

import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Slice;
import toy.bookchat.bookchat.domain.common.CursorMeta;

@Getter
public class UserChatRoomsResponseSlice {

    private List<UserChatRoomResponse> userChatRoomResponseList;
    private CursorMeta<UserChatRoomResponse, Long> cursorMeta;

    private UserChatRoomsResponseSlice(Slice<UserChatRoomResponse> slice) {
        this.cursorMeta = new CursorMeta<>(slice, UserChatRoomResponse::getLastChatId);
        this.userChatRoomResponseList = slice.getContent();
    }

    public static UserChatRoomsResponseSlice of(Slice<UserChatRoomResponse> slice) {
        return new UserChatRoomsResponseSlice(slice);
    }
}
