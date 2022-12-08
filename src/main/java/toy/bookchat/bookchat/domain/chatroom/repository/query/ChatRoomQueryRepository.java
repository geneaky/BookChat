package toy.bookchat.bookchat.domain.chatroom.repository.query;

import java.util.List;
import toy.bookchat.bookchat.domain.chatroom.repository.query.dto.response.ChatRoomResponse;

public interface ChatRoomQueryRepository {

    List<ChatRoomResponse> test(Long userId);
}
