package toy.bookchat.bookchat.db_module.scrap.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.db_module.scrap.ScrapEntity;
import toy.bookchat.bookchat.db_module.scrap.repository.query.ScrapEntityQueryRepository;

public interface ScrapEntityEntityRepository extends ScrapEntityQueryRepository, JpaRepository<ScrapEntity, Long> {

}
