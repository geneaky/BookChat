package toy.bookchat.bookchat.domain.agony.api;

import static org.springframework.http.HttpStatus.CREATED;

import java.net.URI;
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
import toy.bookchat.bookchat.domain.agony.service.AgonyRecordService;
import toy.bookchat.bookchat.domain.agony.service.dto.request.CreateAgonyRecordRequest;
import toy.bookchat.bookchat.domain.agony.service.dto.request.ReviseAgonyRecordRequest;
import toy.bookchat.bookchat.domain.agony.service.dto.response.AgonyRecordResponse;
import toy.bookchat.bookchat.domain.agony.service.dto.response.SliceOfAgonyRecordsResponse;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPayload;

@RestController
public class AgonyRecordController {

    private final AgonyRecordService agonyRecordService;

    public AgonyRecordController(AgonyRecordService agonyRecordService) {
        this.agonyRecordService = agonyRecordService;
    }

    @PostMapping("/v1/api/bookshelves/{bookShelfId}/agonies/{agonyId}/records")
    public ResponseEntity<Void> addAgonyRecordOnBookAgony(@PathVariable Long bookShelfId, @PathVariable Long agonyId, @Valid @RequestBody CreateAgonyRecordRequest createAgonyRecordRequest,
        @UserPayload TokenPayload tokenPayload) {

        Long agonyRecordId = agonyRecordService.storeAgonyRecord(bookShelfId, createAgonyRecordRequest, tokenPayload.getUserId(), agonyId);

        return ResponseEntity.status(CREATED)
            .headers(hs -> hs.setLocation(URI.create("/v1/api/bookshelves/" + bookShelfId + "/agonies/" + agonyId + "/records/" + agonyRecordId)))
            .build();
    }

    @GetMapping("/v1/api/bookshelves/{bookShelfId}/agonies/{agonyId}/records")
    public SliceOfAgonyRecordsResponse getAgonyRecordsOnBookAgony(@PathVariable Long bookShelfId, @PathVariable Long agonyId, Long postCursorId, @UserPayload TokenPayload tokenPayload,
        Pageable pageable) {

        return agonyRecordService.searchPageOfAgonyRecords(bookShelfId, agonyId, tokenPayload.getUserId(), pageable, postCursorId);
    }

    @GetMapping("/v1/api/bookshelves/{bookShelfId}/agonies/{agonyId}/records/{recordId}")
    public AgonyRecordResponse getAgonyRecordsOnBookAgony(@PathVariable Long bookShelfId, @PathVariable Long agonyId, @PathVariable Long recordId, @UserPayload TokenPayload tokenPayload) {

        return agonyRecordService.searchAgonyRecord(bookShelfId, agonyId, recordId, tokenPayload.getUserId());
    }

    @DeleteMapping("/v1/api/bookshelves/{bookShelfId}/agonies/{agonyId}/records/{recordId}")
    public void deleteAgonyRecord(@PathVariable Long bookShelfId, @PathVariable Long agonyId, @PathVariable Long recordId, @UserPayload TokenPayload tokenPayload) {

        agonyRecordService.deleteAgonyRecord(bookShelfId, agonyId, recordId, tokenPayload.getUserId());
    }

    @PutMapping("/v1/api/bookshelves/{bookShelfId}/agonies/{agonyId}/records/{recordId}")
    public void reviseAgonyRecord(@PathVariable Long bookShelfId, @PathVariable Long agonyId, @PathVariable Long recordId, @Valid @RequestBody ReviseAgonyRecordRequest reviseAgonyRecordRequest,
        @UserPayload TokenPayload tokenPayload) {

        agonyRecordService.reviseAgonyRecord(bookShelfId, agonyId, recordId, tokenPayload.getUserId(), reviseAgonyRecordRequest);
    }
}
