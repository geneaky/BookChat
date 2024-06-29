package toy.bookchat.bookchat.db_module.chatroom.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomEntity;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomHashTagEntity;

public interface ChatRoomHashTagRepository extends JpaRepository<ChatRoomHashTagEntity, Long> {

    List<ChatRoomHashTagEntity> findByChatRoomEntity(ChatRoomEntity chatRoomEntity);

    void deleteAllByChatRoomEntity(ChatRoomEntity chatRoomEntity);
}
