package toy.bookchat.bookchat.domain.agony.api.v1;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.agony.api.v1.request.CreateBookAgonyRequest;
import toy.bookchat.bookchat.domain.agony.api.v1.request.ReviseAgonyRequest;
import toy.bookchat.bookchat.domain.agony.api.v1.response.AgonyResponse;
import toy.bookchat.bookchat.domain.agony.api.v1.response.SliceOfAgoniesResponse;
import toy.bookchat.bookchat.domain.agony.service.AgonyService;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPayload;

@RestController
public class AgonyController {

    private final AgonyService agonyService;

    public AgonyController(AgonyService agonyService) {
        this.agonyService = agonyService;
    }

    @PostMapping("/v1/api/bookshelves/{bookShelfId}/agonies")
    public ResponseEntity<Void> makeBookAgony(@PathVariable Long bookShelfId, @Valid @RequestBody CreateBookAgonyRequest createBookAgonyRequest, @UserPayload TokenPayload tokenPayload) {
        Long agonyId = agonyService.storeBookShelfAgony(createBookAgonyRequest, tokenPayload.getUserId(), bookShelfId);

        return ResponseEntity.status(CREATED)
            .headers(hs -> hs.setLocation(URI.create("/v1/api/bookshelves/" + bookShelfId + "/agonies/" + agonyId)))
            .build();
    }

    @GetMapping("/v1/api/bookshelves/{bookShelfId}/agonies/{agonyId}")
    public AgonyResponse searchAgony(@PathVariable Long bookShelfId, @PathVariable Long agonyId, @UserPayload TokenPayload tokenPayload) {
        return agonyService.searchAgony(bookShelfId, agonyId, tokenPayload.getUserId());
    }

    @GetMapping("/v1/api/bookshelves/{bookShelfId}/agonies")
    public SliceOfAgoniesResponse searchSliceOfAgonies(@PathVariable Long bookShelfId, Long postCursorId, Pageable pageable, @UserPayload TokenPayload tokenPayload) {

        return agonyService.searchSliceOfAgonies(bookShelfId, tokenPayload.getUserId(), pageable, postCursorId);
    }

    @DeleteMapping("/v1/api/bookshelves/{bookShelfId}/agonies/{agoniesIds}")
    public void deleteAgony(@PathVariable Long bookShelfId, @PathVariable List<Long> agoniesIds, @UserPayload TokenPayload tokenPayload) {

        agonyService.deleteAgony(bookShelfId, agoniesIds, tokenPayload.getUserId());
    }

    @PutMapping("/v1/api/bookshelves/{bookShelfId}/agonies/{agonyId}")
    public void reviseAgony(@PathVariable Long bookShelfId, @PathVariable Long agonyId, @Valid @RequestBody ReviseAgonyRequest reviseAgonyRequest, @UserPayload TokenPayload tokenPayload) {

        agonyService.reviseAgony(bookShelfId, agonyId, tokenPayload.getUserId(), reviseAgonyRequest);
    }
}
