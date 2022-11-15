package toy.bookchat.bookchat.domain.agonyrecord.api;

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
import toy.bookchat.bookchat.domain.agony.service.dto.request.CreateAgonyRecordRequest;
import toy.bookchat.bookchat.domain.agony.service.dto.request.ReviseAgonyRecordRequest;
import toy.bookchat.bookchat.domain.agony.service.dto.response.SliceOfAgonyRecordsResponse;
import toy.bookchat.bookchat.domain.agonyrecord.service.AgonyRecordService;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPayload;

@RestController
public class AgonyRecordController {

    private final AgonyRecordService agonyRecordService;

    public AgonyRecordController(
        AgonyRecordService agonyRecordService) {
        this.agonyRecordService = agonyRecordService;
    }

    @PostMapping("/v1/api/agonies/{agonyId}/records")
    public void addAgonyRecordOnBookAgony(@PathVariable Long agonyId,
        @Valid @RequestBody CreateAgonyRecordRequest createAgonyRecordRequest,
        @UserPayload TokenPayload tokenPayload) {

        agonyRecordService.storeAgonyRecord(createAgonyRecordRequest, tokenPayload.getUserId(),
            agonyId);
    }

    @GetMapping("/v1/api/agonies/{agonyId}/records")
    public SliceOfAgonyRecordsResponse getAgonyRecordsOnBookAgony(@PathVariable Long agonyId,
        @RequestParam Optional<Long> postRecordCursorId,
        @UserPayload TokenPayload tokenPayload, Pageable pageable) {

        return agonyRecordService.searchPageOfAgonyRecords(agonyId, tokenPayload.getUserId(),
            pageable, postRecordCursorId);
    }

    @DeleteMapping("/v1/api/agonies/{agonyId}/records/{recordId}")
    public void deleteAgonyRecord(@PathVariable Long agonyId, @PathVariable Long recordId,
        @UserPayload TokenPayload tokenPayload) {

        agonyRecordService.deleteAgonyRecord(agonyId, recordId, tokenPayload.getUserId());
    }

    @PutMapping("/v1/api/agonies/{agonyId}/records/{recordId}")
    public void reviseAgonyRecord(@PathVariable Long agonyId, @PathVariable Long recordId,
        @Valid @RequestBody ReviseAgonyRecordRequest reviseAgonyRecordRequest,
        @UserPayload TokenPayload tokenPayload) {

        agonyRecordService.reviseAgonyRecord(agonyId, recordId, tokenPayload.getUserId(),
            reviseAgonyRecordRequest);
    }
}
