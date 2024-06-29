package toy.bookchat.bookchat.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.domain.chat.ChatEntity;
import toy.bookchat.bookchat.domain.chat.repository.query.ChatQueryRepository;
import toy.bookchat.bookchat.domain.chatroom.ChatRoomEntity;

public interface ChatRepository extends ChatQueryRepository, JpaRepository<ChatEntity, Long> {

    void deleteByChatRoomEntity(ChatRoomEntity chatRoomEntity);
}
