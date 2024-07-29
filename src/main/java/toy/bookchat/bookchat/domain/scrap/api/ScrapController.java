package toy.bookchat.bookchat.domain.scrap.api;

import static org.springframework.http.HttpStatus.CREATED;

import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.scrap.api.v1.request.CreateScrapRequest;
import toy.bookchat.bookchat.domain.scrap.api.v1.response.ScrapResponse;
import toy.bookchat.bookchat.domain.scrap.api.v1.response.ScrapResponseSlice;
import toy.bookchat.bookchat.domain.scrap.service.ScrapService;
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
  public ResponseEntity<Void> scrapChat(@Valid @RequestBody CreateScrapRequest createScrapRequest,
      @UserPayload TokenPayload tokenPayload) {
    Long scrapId = scrapService.scrap(createScrapRequest, tokenPayload.getUserId());

    return ResponseEntity.status(CREATED)
        .headers(hs -> hs.setLocation(URI.create("/v1/api/scraps/" + scrapId)))
        .build();
  }

  @GetMapping("/scraps")
  public ScrapResponseSlice getScraps(Long bookShelfId, Long postCursorId, Pageable pageable,
      @UserPayload TokenPayload tokenPayload) {
    return scrapService.getScraps(bookShelfId, postCursorId, pageable, tokenPayload.getUserId());
  }

  @GetMapping("/scraps/{scrapId}")
  public ScrapResponse getScrap(@PathVariable Long scrapId, @UserPayload TokenPayload tokenPayload) {
    return scrapService.getScrap(scrapId, tokenPayload.getUserId());
  }

  @DeleteMapping("/scraps/{scrapIds}")
  public void deleteScraps(Long bookShelfId, @PathVariable List<Long> scrapIds,
      @UserPayload TokenPayload tokenPayload) {
    scrapService.deleteScraps(bookShelfId, scrapIds, tokenPayload.getUserId());
  }
}
