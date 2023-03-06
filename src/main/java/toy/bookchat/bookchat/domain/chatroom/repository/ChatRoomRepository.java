package toy.bookchat.bookchat.domain.chatroom.repository;

import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.repository.query.ChatRoomQueryRepository;

public interface ChatRoomRepository extends ChatRoomQueryRepository, JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByRoomSid(String roomSid);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ChatRoom> findWithPessimisticLockById(Long id);
}
