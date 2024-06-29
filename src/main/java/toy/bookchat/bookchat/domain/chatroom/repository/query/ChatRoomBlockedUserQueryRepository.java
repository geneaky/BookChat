package toy.bookchat.bookchat.domain.chatroom.repository.query;

import java.util.Optional;
import toy.bookchat.bookchat.domain.chatroom.ChatRoomBlockedUserEntity;

public interface ChatRoomBlockedUserQueryRepository {

    Optional<ChatRoomBlockedUserEntity> findByUserIdAndChatRoomId(Long userId, Long chatRoomId);
}
