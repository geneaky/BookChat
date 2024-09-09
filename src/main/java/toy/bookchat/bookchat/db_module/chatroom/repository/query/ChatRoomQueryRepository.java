package toy.bookchat.bookchat.db_module.chatroom.repository.query;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomEntity;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.ChatRoomResponse;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.UserChatRoomResponse;
import toy.bookchat.bookchat.domain.chatroom.api.v1.request.ChatRoomRequest;
import toy.bookchat.bookchat.domain.chatroom.api.v1.response.ChatRoomDetails;
import toy.bookchat.bookchat.domain.participant.ParticipantStatus;

public interface ChatRoomQueryRepository {


  Slice<UserChatRoomResponse> findUserChatRoomsWithLastChat(Pageable pageable, Long bookId, Long postCursorId,
      Long userId);

  Slice<ChatRoomResponse> findChatRooms(ChatRoomRequest chatRoomRequest, Pageable pageable);

  ChatRoomDetails findChatRoomDetails(Long roomId, Long userId);

  Optional<ChatRoomEntity> findUserChatRoom(Long roomId, Long userId, ParticipantStatus participantStatus);
}
