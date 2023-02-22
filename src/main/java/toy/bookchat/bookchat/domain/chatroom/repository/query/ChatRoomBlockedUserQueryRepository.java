package toy.bookchat.bookchat.domain.chatroom.repository.query;

import java.util.Optional;
import toy.bookchat.bookchat.domain.chatroom.ChatRoomBlockedUser;

public interface ChatRoomBlockedUserQueryRepository {

    Optional<ChatRoomBlockedUser> findByUserIdAndChatRoomId(Long userId, Long chatRoomId);
}
