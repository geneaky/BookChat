package toy.bookchat.bookchat.domain.scrap.api;

import javax.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.scrap.service.ScrapService;
import toy.bookchat.bookchat.domain.scrap.service.dto.request.CreateScrapRequest;
import toy.bookchat.bookchat.domain.scrap.service.dto.response.ScrapResponseSlice;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPayload;

@RestController
@RequestMapping("/v1/api")
public class ScrapController {

    private final ScrapService scrapService;

    public ScrapController(ScrapService scrapService) {
        this.scrapService = scrapService;
    }

    @PostMapping("/scraps")
    public void scrapChat(@Valid @RequestBody CreateScrapRequest createScrapRequest,
        @UserPayload TokenPayload tokenPayload) {
        scrapService.scrap(createScrapRequest, tokenPayload.getUserId());
    }

    @GetMapping("/scraps")
    public ScrapResponseSlice getScraps(Long bookShelfId, Long postCursorId, Pageable pageable,
        @UserPayload TokenPayload tokenPayload) {
        return scrapService.getScraps(bookShelfId, postCursorId, pageable,
            tokenPayload.getUserId());
    }
}
