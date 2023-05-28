package toy.bookchat.bookchat.domain.scrap.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.domain.scrap.Scrap;
import toy.bookchat.bookchat.domain.scrap.repository.query.ScrapQueryRepository;

public interface ScrapRepository extends ScrapQueryRepository, JpaRepository<Scrap, Long> {

}
