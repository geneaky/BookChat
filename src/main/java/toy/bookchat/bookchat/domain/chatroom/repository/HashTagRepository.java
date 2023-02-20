package toy.bookchat.bookchat.domain.chatroom.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.domain.chatroom.HashTag;

public interface HashTagRepository extends JpaRepository<HashTag, Long> {

    Optional<HashTag> findByTagName(String tagName);
}
