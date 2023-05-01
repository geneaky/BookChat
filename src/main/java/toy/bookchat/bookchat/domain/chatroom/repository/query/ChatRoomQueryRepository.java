package toy.bookchat.bookchat.domain.chatroom.repository.query;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import toy.bookchat.bookchat.domain.chatroom.repository.query.dto.response.ChatRoomResponse;
import toy.bookchat.bookchat.domain.chatroom.repository.query.dto.response.UserChatRoomResponse;
import toy.bookchat.bookchat.domain.chatroom.service.dto.request.ChatRoomRequest;
import toy.bookchat.bookchat.domain.participant.service.dto.response.ChatRoomDetails;

public interface ChatRoomQueryRepository {


    Slice<UserChatRoomResponse> findUserChatRoomsWithLastChat(Pageable pageable,
        Optional<Long> postCursorId, Long userId);

    Slice<ChatRoomResponse> findChatRooms(ChatRoomRequest chatRoomRequest, Pageable pageable);

    ChatRoomDetails findChatRoomDetails(Long roomId, Long userId);
}
