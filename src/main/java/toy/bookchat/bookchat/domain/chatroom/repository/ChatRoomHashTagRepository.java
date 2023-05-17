package toy.bookchat.bookchat.domain.chatroom.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.ChatRoomHashTag;

public interface ChatRoomHashTagRepository extends JpaRepository<ChatRoomHashTag, Long> {

    List<ChatRoomHashTag> findByChatRoom(ChatRoom chatRoom);

    void deleteAllByChatRoom(ChatRoom chatRoom);
}
