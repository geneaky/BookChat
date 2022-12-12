package toy.bookchat.bookchat.domain.chatroom.repository.query;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import toy.bookchat.bookchat.domain.chatroom.repository.query.dto.response.ChatRoomResponse;

public interface ChatRoomQueryRepository {

    List<ChatRoomResponse> test(Pageable pageable, Optional<Long> postCursorId, Long userId);
}
