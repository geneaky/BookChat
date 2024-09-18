package toy.bookchat.bookchat.db_module.chatroom.repository.query.dto;

import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Slice;
import toy.bookchat.bookchat.support.CursorMeta;

@Getter
public class UserChatRoomsResponseSlice {

  private List<UserChatRoomResponse> userChatRoomResponseList;
  private CursorMeta<UserChatRoomResponse, Long> cursorMeta;

  private UserChatRoomsResponseSlice(Slice<UserChatRoomResponse> slice) {
    this.cursorMeta = new CursorMeta<>(slice, UserChatRoomResponse::getRoomId);
    this.userChatRoomResponseList = slice.getContent();
  }

  public static UserChatRoomsResponseSlice of(Slice<UserChatRoomResponse> slice) {
    return new UserChatRoomsResponseSlice(slice);
  }
}
