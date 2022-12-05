package toy.bookchat.bookchat.domain.hashtag.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.domain.hashtag.HashTag;

public interface HashTagRepository extends JpaRepository<HashTag, Long> {

}
