package toy.bookchat.bookchat.domain.chatroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

}
