package toy.bookchat.bookchat.db_module.chatroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomBlockedUserEntity;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.ChatRoomBlockedUserQueryRepository;

public interface ChatRoomBlockedUserRepository extends ChatRoomBlockedUserQueryRepository,
    JpaRepository<ChatRoomBlockedUserEntity, Long> {

}
