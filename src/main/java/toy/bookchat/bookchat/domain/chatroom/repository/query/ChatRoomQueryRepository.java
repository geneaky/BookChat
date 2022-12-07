package toy.bookchat.bookchat.domain.chatroom.repository.query;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;

public interface ChatRoomQueryRepository {

    Slice<ChatRoom> findUserChatRoomsWithLastChat(Optional<Long> postCursorId,
        Pageable pageable, Long userId);
}
