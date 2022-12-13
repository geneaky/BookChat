package toy.bookchat.bookchat.domain.chatroom.repository.query;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import toy.bookchat.bookchat.domain.chatroom.repository.query.dto.response.ChatRoomResponse;

public interface ChatRoomQueryRepository {


    Slice<ChatRoomResponse> test2(Pageable pageable, Optional<Long> postCursorId, Long userId);
}
