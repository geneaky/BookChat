package toy.bookchat.bookchat.domain;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import toy.bookchat.bookchat.config.log.P6spyLogMessageFormatConfig;
import toy.bookchat.bookchat.config.query.JpaAuditingConfig;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaAuditingConfig.class, RepositoryTestConfiguration.class,
    P6spyLogMessageFormatConfig.class})
public abstract class RepositoryTest {

}
