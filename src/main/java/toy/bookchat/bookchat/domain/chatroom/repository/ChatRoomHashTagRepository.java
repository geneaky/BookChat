package toy.bookchat.bookchat.domain.chatroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.domain.chatroom.ChatRoomHashTag;

public interface ChatRoomHashTagRepository extends JpaRepository<ChatRoomHashTag, Long> {

}
