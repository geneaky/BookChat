package toy.bookchat.bookchat.domain.agony.api;

import javax.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.agony.service.AgonyRecordService;
import toy.bookchat.bookchat.domain.agony.service.AgonyService;
import toy.bookchat.bookchat.domain.agony.service.dto.CreateAgonyRecordRequestDto;
import toy.bookchat.bookchat.domain.agony.service.dto.CreateBookAgonyRequestDto;
import toy.bookchat.bookchat.domain.agony.service.dto.PageOfAgoniesResponse;
import toy.bookchat.bookchat.domain.agony.service.dto.PageOfAgonyRecordsResponse;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.security.user.CurrentUser;

@RestController
@RequestMapping("/v1/api")
public class AgonyController {

    private final AgonyService agonyService;
    private final AgonyRecordService agonyRecordService;

    public AgonyController(AgonyService agonyService,
        AgonyRecordService agonyRecordService) {
        this.agonyService = agonyService;
        this.agonyRecordService = agonyRecordService;
    }

    @PostMapping("/bookshelf/books/{bookId}/agonies")
    public ResponseEntity<Void> makeBookAgony(@PathVariable final Long bookId,
        @Valid @RequestBody CreateBookAgonyRequestDto createBookAgonyRequestDto,
        @CurrentUser User user) {
        agonyService.storeBookAgony(createBookAgonyRequestDto, user.getId(), bookId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/bookshelf/books/{bookId}/agonies")
    public ResponseEntity<PageOfAgoniesResponse> searchPageOfAgonies(
        @PathVariable final Long bookId,
        final Pageable pageable, @CurrentUser User user) {
        return ResponseEntity.ok(agonyService.searchPageOfAgonies(bookId, user.getId(), pageable));
    }

    @PostMapping("/bookshelf/books/{bookId}/agonies/{agonyId}/records")
    public ResponseEntity<Void> addAgonyRecordOnBookAgony(@PathVariable final Long bookId,
        @PathVariable final Long agonyId,
        @Valid @RequestBody final CreateAgonyRecordRequestDto createAgonyRecordRequestDto,
        @CurrentUser User user) {
        agonyRecordService.storeAgonyRecord(createAgonyRecordRequestDto, user.getId(), bookId,
            agonyId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/bookshelf/books/{bookId}/agonies/{agonyId}/records")
    public ResponseEntity<PageOfAgonyRecordsResponse> getAgonyRecordsOnBookAgony(
        @PathVariable final Long bookId,
        @PathVariable final Long agonyId, @CurrentUser User user) {

        return ResponseEntity.ok(
            agonyRecordService.searchPageOfAgonyRecords(bookId, agonyId, user.getId()));
    }
}
