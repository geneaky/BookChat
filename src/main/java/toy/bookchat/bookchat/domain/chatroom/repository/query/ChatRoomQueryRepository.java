package toy.bookchat.bookchat.domain.chatroom.repository.query;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import toy.bookchat.bookchat.domain.chatroom.repository.query.dto.response.ChatRoomResponse;
import toy.bookchat.bookchat.domain.chatroom.repository.query.dto.response.ChatRoomUsersResponse;

public interface ChatRoomQueryRepository {


    Slice<ChatRoomResponse> findUserChatRoomsWithLastChat(Pageable pageable,
        Optional<Long> postCursorId, Long userId);

    ChatRoomUsersResponse findChatRoomUsers(Long roomId, Long userId);
}
