package toy.bookchat.bookchat.db_module.chatroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomHashTagEntity;

public interface ChatRoomHashTagRepository extends JpaRepository<ChatRoomHashTagEntity, Long> {

  @Modifying
  @Query("delete from ChatRoomHashTagEntity c where c.chatRoomId = :chatRoomId")
  void deleteByChatRoomId(@Param("chatRoomId") Long chatRoomId);
}
