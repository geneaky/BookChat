package toy.bookchat.bookchat.db_module.chatroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomHashTagEntity;

public interface ChatRoomHashTagRepository extends JpaRepository<ChatRoomHashTagEntity, Long> {

  void deleteAllByChatRoomId(Long chatRoomId);
}
