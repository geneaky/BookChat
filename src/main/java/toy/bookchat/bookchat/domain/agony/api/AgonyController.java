package toy.bookchat.bookchat.domain.agony.api;

import javax.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.agony.service.AgonyRecordService;
import toy.bookchat.bookchat.domain.agony.service.AgonyService;
import toy.bookchat.bookchat.domain.agony.service.dto.request.CreateAgonyRecordRequest;
import toy.bookchat.bookchat.domain.agony.service.dto.request.CreateBookAgonyRequest;
import toy.bookchat.bookchat.domain.agony.service.dto.request.ReviseAgonyRecordRequest;
import toy.bookchat.bookchat.domain.agony.service.dto.request.ReviseAgonyRequest;
import toy.bookchat.bookchat.domain.agony.service.dto.response.BasePageOfAgoniesResponse;
import toy.bookchat.bookchat.domain.agony.service.dto.response.BasePageOfAgonyRecordsResponse;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPayload;

@RestController
@RequestMapping("/v1/api/bookshelf/books/{bookId}/agonies")
public class AgonyController {

    private final AgonyService agonyService;
    private final AgonyRecordService agonyRecordService;

    public AgonyController(AgonyService agonyService,
        AgonyRecordService agonyRecordService) {
        this.agonyService = agonyService;
        this.agonyRecordService = agonyRecordService;
    }

    @PostMapping
    public void makeBookAgony(@PathVariable Long bookId,
        @Valid @RequestBody CreateBookAgonyRequest createBookAgonyRequest,
        @UserPayload TokenPayload tokenPayload) {

        agonyService.storeBookAgony(createBookAgonyRequest, tokenPayload.getUserId(), bookId);
    }

    @GetMapping
    public BasePageOfAgoniesResponse searchPageOfAgonies(@PathVariable Long bookId,
        Pageable pageable,
        @UserPayload TokenPayload tokenPayload) {

        return agonyService.searchPageOfAgonies(bookId, tokenPayload.getUserId(), pageable);
    }

    @PostMapping("/{agonyId}/records")
    public void addAgonyRecordOnBookAgony(@PathVariable Long bookId, @PathVariable Long agonyId,
        @Valid @RequestBody CreateAgonyRecordRequest createAgonyRecordRequest,
        @UserPayload TokenPayload tokenPayload) {

        agonyRecordService.storeAgonyRecord(createAgonyRecordRequest, tokenPayload.getUserId(),
            bookId,
            agonyId);
    }

    @GetMapping("/{agonyId}/records")
    public BasePageOfAgonyRecordsResponse getAgonyRecordsOnBookAgony(@PathVariable Long bookId,
        @PathVariable Long agonyId, @UserPayload TokenPayload tokenPayload, Pageable pageable) {

        return agonyRecordService.searchPageOfAgonyRecords(bookId, agonyId,
            tokenPayload.getUserId(), pageable);
    }

    @DeleteMapping("/{agonyId}")
    public void deleteAgony(@PathVariable Long bookId, @PathVariable Long agonyId,
        @UserPayload TokenPayload tokenPayload) {

        agonyService.deleteAgony(bookId, agonyId, tokenPayload.getUserId());
    }

    @PutMapping("/{agonyId}")
    public void reviseAgony(@PathVariable Long bookId, @PathVariable Long agonyId,
        @Valid @RequestBody ReviseAgonyRequest reviseAgonyRequest,
        @UserPayload TokenPayload tokenPayload) {

        agonyService.reviseAgony(bookId, agonyId, tokenPayload.getUserId(), reviseAgonyRequest);
    }

    @DeleteMapping("/{agonyId}/records/{recordId}")
    public void deleteAgonyRecord(@PathVariable Long bookId, @PathVariable Long agonyId,
        @PathVariable Long recordId, @UserPayload TokenPayload tokenPayload) {

        agonyRecordService.deleteAgonyRecord(bookId, agonyId, recordId, tokenPayload.getUserId());
    }

    @PutMapping("/{agonyId}/records/{recordId}")
    public void reviseAgonyRecord(@PathVariable Long bookId, @PathVariable Long agonyId,
        @PathVariable Long recordId,
        @Valid @RequestBody ReviseAgonyRecordRequest reviseAgonyRecordRequest,
        @UserPayload TokenPayload tokenPayload) {

        agonyRecordService.reviseAgonyRecord(bookId, agonyId, recordId, tokenPayload.getUserId(),
            reviseAgonyRecordRequest);
    }
}
