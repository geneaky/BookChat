package toy.bookchat.bookchat.domain.chatroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.domain.chatroom.ChatRoomBlockedUser;
import toy.bookchat.bookchat.domain.chatroom.repository.query.ChatRoomBlockedUserQueryRepository;

public interface ChatRoomBlockedUserRepository extends ChatRoomBlockedUserQueryRepository,
    JpaRepository<ChatRoomBlockedUser, Long> {

}
