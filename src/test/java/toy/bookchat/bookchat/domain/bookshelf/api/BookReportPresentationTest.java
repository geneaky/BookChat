package toy.bookchat.bookchat.domain.bookshelf.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import toy.bookchat.bookchat.domain.bookshelf.api.v1.BookReportController;
import toy.bookchat.bookchat.security.SecurityConfig;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@WebMvcTest(controllers = BookReportController.class)
@Import({SecurityConfig.class})
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "bookchat.link", uriPort = 443)
public @interface BookReportPresentationTest {

}
