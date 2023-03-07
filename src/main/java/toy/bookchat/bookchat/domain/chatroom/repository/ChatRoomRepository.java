package toy.bookchat.bookchat.domain.chatroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.repository.query.ChatRoomQueryRepository;

public interface ChatRoomRepository extends ChatRoomQueryRepository, JpaRepository<ChatRoom, Long> {

}
