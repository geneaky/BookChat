package toy.bookchat.bookchat.db_module.chatroom.repository.query.dto;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.domain.Slice;
import toy.bookchat.bookchat.domain.common.CursorMeta;

@Getter
@EqualsAndHashCode(of = "chatRoomResponseList")
public class ChatRoomsResponseSlice {

  private List<ChatRoomResponse> chatRoomResponseList;
  private CursorMeta<ChatRoomResponse, Long> cursorMeta;

  private ChatRoomsResponseSlice(Slice<ChatRoomResponse> slice) {
    this.cursorMeta = new CursorMeta<>(slice, ChatRoomResponse::getLastChatId);
    this.chatRoomResponseList = slice.getContent();
  }

  public static ChatRoomsResponseSlice of(Slice<ChatRoomResponse> slice) {
    return new ChatRoomsResponseSlice(slice);
  }
}
