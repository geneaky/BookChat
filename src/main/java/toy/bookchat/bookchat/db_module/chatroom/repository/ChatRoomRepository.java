package toy.bookchat.bookchat.db_module.chatroom.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.ChatRoomQueryRepository;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomEntity;

public interface ChatRoomRepository extends ChatRoomQueryRepository, JpaRepository<ChatRoomEntity, Long> {

    Optional<ChatRoomEntity> findChatRoomByIdAndHostId(Long id, Long hostId);
}
