package toy.bookchat.bookchat.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chat.repository.query.ChatQueryRepository;

public interface ChatRepository extends ChatQueryRepository, JpaRepository<Chat, Long> {

}
