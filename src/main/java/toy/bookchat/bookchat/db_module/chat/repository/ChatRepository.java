package toy.bookchat.bookchat.db_module.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.db_module.chat.ChatEntity;
import toy.bookchat.bookchat.db_module.chat.repository.query.ChatQueryRepository;

public interface ChatRepository extends ChatQueryRepository, JpaRepository<ChatEntity, Long> {

}
