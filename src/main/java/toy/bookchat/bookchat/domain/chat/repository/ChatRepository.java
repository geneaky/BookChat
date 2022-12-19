package toy.bookchat.bookchat.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.domain.chat.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long> {

}
