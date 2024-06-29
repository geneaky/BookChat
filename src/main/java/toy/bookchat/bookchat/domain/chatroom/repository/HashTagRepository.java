package toy.bookchat.bookchat.domain.chatroom.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.domain.chatroom.HashTagEntity;

public interface HashTagRepository extends JpaRepository<HashTagEntity, Long> {

    Optional<HashTagEntity> findByTagName(String tagName);
}
