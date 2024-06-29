package toy.bookchat.bookchat.db_module.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.db_module.chat.ChatEntity;
import toy.bookchat.bookchat.db_module.chat.repository.query.ChatQueryRepository;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomEntity;

public interface ChatRepository extends ChatQueryRepository, JpaRepository<ChatEntity, Long> {

    void deleteByChatRoomEntity(ChatRoomEntity chatRoomEntity);
}
