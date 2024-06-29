package toy.bookchat.bookchat.db_module.scrap.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.db_module.scrap.repository.query.ScrapQueryRepository;
import toy.bookchat.bookchat.db_module.scrap.ScrapEntity;

public interface ScrapRepository extends ScrapQueryRepository, JpaRepository<ScrapEntity, Long> {

}
