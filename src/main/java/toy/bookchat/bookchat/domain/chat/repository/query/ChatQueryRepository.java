package toy.bookchat.bookchat.domain.chat.repository.query;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import toy.bookchat.bookchat.domain.chat.Chat;

public interface ChatQueryRepository {

    Slice<Chat> findUserChatRoomsWithLastChat(Optional<Long> postChatRoomCursorId,
        Pageable pageable, Long userId);
}
