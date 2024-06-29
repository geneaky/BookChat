package toy.bookchat.bookchat.db_module.chatroom.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.db_module.chatroom.HashTagEntity;

public interface HashTagRepository extends JpaRepository<HashTagEntity, Long> {

    Optional<HashTagEntity> findByTagName(String tagName);
}
