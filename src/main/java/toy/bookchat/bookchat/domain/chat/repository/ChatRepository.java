package toy.bookchat.bookchat.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chat.repository.query.ChatQueryRepository;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;

public interface ChatRepository extends ChatQueryRepository, JpaRepository<Chat, Long> {

    void deleteByChatRoom(ChatRoom chatRoom);
}
