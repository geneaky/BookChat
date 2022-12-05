package toy.bookchat.bookchat.domain.hashtag.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.domain.hashtag.HashTag;

public interface HashTagRepository extends JpaRepository<HashTag, Long> {

    Optional<HashTag> findByTagName(String tagName);
}
