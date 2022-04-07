package toy.bookchat.bookchat.domain.chat;

import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.restdocs.RestDocumentationExtension;

public class RestDocExtention {

    @RegisterExtension
    final RestDocumentationExtension restDocumentationExtension = new RestDocumentationExtension("build/docs");

}
