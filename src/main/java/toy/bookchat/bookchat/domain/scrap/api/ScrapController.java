package toy.bookchat.bookchat.domain.scrap.api;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.scrap.service.dto.request.CreateScrapRequest;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPayload;

@RestController
@RequestMapping("/v1/api")
public class ScrapController {

    @PostMapping("/scraps")
    public void scrapChat(@Valid @RequestBody CreateScrapRequest createScrapRequest,
        @UserPayload TokenPayload tokenPayload) {
        
    }

}
