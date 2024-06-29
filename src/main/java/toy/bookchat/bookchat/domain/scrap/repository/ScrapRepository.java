package toy.bookchat.bookchat.domain.scrap.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.domain.scrap.ScrapEntity;
import toy.bookchat.bookchat.domain.scrap.repository.query.ScrapQueryRepository;

public interface ScrapRepository extends ScrapQueryRepository, JpaRepository<ScrapEntity, Long> {

}
