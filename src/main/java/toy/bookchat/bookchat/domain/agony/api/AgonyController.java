package toy.bookchat.bookchat.domain.agony.api;

import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.agony.service.AgonyService;
import toy.bookchat.bookchat.domain.agony.service.dto.request.CreateBookAgonyRequest;
import toy.bookchat.bookchat.domain.agony.service.dto.request.ReviseAgonyRequest;
import toy.bookchat.bookchat.domain.agony.service.dto.response.SliceOfAgoniesResponse;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPayload;

@RestController
public class AgonyController {

    private final AgonyService agonyService;

    public AgonyController(AgonyService agonyService) {
        this.agonyService = agonyService;
    }

    @PostMapping("/v1/api/bookshelf/books/{bookId}/agonies")
    public void makeBookAgony(@PathVariable Long bookId,
        @Valid @RequestBody CreateBookAgonyRequest createBookAgonyRequest,
        @UserPayload TokenPayload tokenPayload) {

        agonyService.storeBookAgony(createBookAgonyRequest, tokenPayload.getUserId(), bookId);
    }

    @GetMapping("/v1/api/agonies")
    public SliceOfAgoniesResponse searchSliceOfAgonies(
        @RequestParam Optional<Long> postCursorId, Pageable pageable,
        @UserPayload TokenPayload tokenPayload) {

        return agonyService.searchSliceOfAgonies(tokenPayload.getUserId(), pageable,
            postCursorId);
    }

    @DeleteMapping("/v1/api/agonies/{agoniesIds}")
    public void deleteAgony(@PathVariable List<Long> agoniesIds,
        @UserPayload TokenPayload tokenPayload) {

        agonyService.deleteAgony(agoniesIds, tokenPayload.getUserId());
    }

    @PutMapping("/v1/api/agonies/{agonyId}")
    public void reviseAgony(@PathVariable Long agonyId,
        @Valid @RequestBody ReviseAgonyRequest reviseAgonyRequest,
        @UserPayload TokenPayload tokenPayload) {

        agonyService.reviseAgony(agonyId, tokenPayload.getUserId(), reviseAgonyRequest);
    }
}
