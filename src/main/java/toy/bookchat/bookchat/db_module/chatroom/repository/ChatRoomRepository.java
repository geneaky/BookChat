package toy.bookchat.bookchat.db_module.chatroom.repository;

import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomEntity;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.ChatRoomQueryRepository;

public interface ChatRoomRepository extends ChatRoomQueryRepository, JpaRepository<ChatRoomEntity, Long> {

  Optional<ChatRoomEntity> findChatRoomByIdAndHostId(Long id, Long hostId);


  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<ChatRoomEntity> findWithLockById(Long id);
}
