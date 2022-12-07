package toy.bookchat.bookchat.domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import toy.bookchat.bookchat.config.JpaAuditingConfig;
import toy.bookchat.bookchat.config.log.P6spyLogMessageFormatConfig;
import toy.bookchat.bookchat.domain.configuration.TestConfig;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@DataJpaTest(showSql = false)
@Import({JpaAuditingConfig.class, TestConfig.class, P6spyLogMessageFormatConfig.class})
public @interface RepositoryTest {

}
