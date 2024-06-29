package toy.bookchat.bookchat.db_module.chatroom.repository.query;

import java.util.Optional;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomBlockedUserEntity;

public interface ChatRoomBlockedUserQueryRepository {

    Optional<ChatRoomBlockedUserEntity> findByUserIdAndChatRoomId(Long userId, Long chatRoomId);
}
