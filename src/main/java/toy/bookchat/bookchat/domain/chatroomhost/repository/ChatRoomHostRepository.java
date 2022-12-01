package toy.bookchat.bookchat.domain.chatroomhost.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.domain.chatroomhost.ChatRoomHost;

public interface ChatRoomHostRepository extends JpaRepository<ChatRoomHost, Long> {

}
