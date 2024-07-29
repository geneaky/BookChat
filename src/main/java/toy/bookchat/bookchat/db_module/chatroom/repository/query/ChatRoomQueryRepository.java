package toy.bookchat.bookchat.db_module.chatroom.repository.query;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomEntity;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.response.ChatRoomResponse;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.response.UserChatRoomResponse;
import toy.bookchat.bookchat.domain.chatroom.api.v1.request.ChatRoomRequest;
import toy.bookchat.bookchat.domain.participant.api.v1.response.ChatRoomDetails;

public interface ChatRoomQueryRepository {


  Slice<UserChatRoomResponse> findUserChatRoomsWithLastChat(Pageable pageable, Long bookId, Long postCursorId,
      Long userId);

  Slice<ChatRoomResponse> findChatRooms(Long userId, ChatRoomRequest chatRoomRequest, Pageable pageable);

  ChatRoomDetails findChatRoomDetails(Long roomId, Long userId);

  Optional<ChatRoomEntity> findUserChatRoom(Long roomId, Long userId);
}
