package toy.bookchat.bookchat.domain.agonyrecord.api.v1;

import static org.springframework.http.HttpStatus.CREATED;

import java.net.URI;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.agonyrecord.AgonyRecord;
import toy.bookchat.bookchat.domain.agonyrecord.api.v1.request.CreateAgonyRecordRequest;
import toy.bookchat.bookchat.domain.agonyrecord.api.v1.request.ReviseAgonyRecordRequest;
import toy.bookchat.bookchat.domain.agonyrecord.api.v1.response.AgonyRecordResponse;
import toy.bookchat.bookchat.domain.agonyrecord.api.v1.response.SliceOfAgonyRecordsResponse;
import toy.bookchat.bookchat.domain.agonyrecord.service.AgonyRecordService;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPayload;

@RequiredArgsConstructor

@RestController
@RequestMapping("/v1/api/bookshelves/{bookShelfId}/agonies/{agonyId}/records")
public class AgonyRecordController {

  private final AgonyRecordService agonyRecordService;

  @PostMapping
  public ResponseEntity<Void> addAgonyRecordOnBookAgony(@PathVariable Long bookShelfId, @PathVariable Long agonyId,
      @Valid @RequestBody CreateAgonyRecordRequest createAgonyRecordRequest, @UserPayload TokenPayload tokenPayload) {
    Long agonyRecordId = agonyRecordService.storeAgonyRecord(bookShelfId, createAgonyRecordRequest.toTarget(),
        tokenPayload.getUserId(), agonyId);

    return ResponseEntity.status(CREATED)
        .headers(hs -> hs.setLocation(
            URI.create("/v1/api/bookshelves/" + bookShelfId + "/agonies/" + agonyId + "/records/" + agonyRecordId)))
        .build();
  }

  @GetMapping
  public SliceOfAgonyRecordsResponse getAgonyRecordsOnBookAgony(@PathVariable Long bookShelfId,
      @PathVariable Long agonyId, Long postCursorId, @UserPayload TokenPayload tokenPayload, Pageable pageable) {
    Slice<AgonyRecord> agonyRecordSlice = agonyRecordService.searchPageOfAgonyRecords(bookShelfId, agonyId,
        tokenPayload.getUserId(), pageable, postCursorId);

    return new SliceOfAgonyRecordsResponse(agonyRecordSlice);
  }

  @GetMapping("/{recordId}")
  public AgonyRecordResponse getAgonyRecordsOnBookAgony(@PathVariable Long bookShelfId, @PathVariable Long agonyId,
      @PathVariable Long recordId, @UserPayload TokenPayload tokenPayload) {
    AgonyRecord agonyRecord = agonyRecordService.searchAgonyRecord(bookShelfId, agonyId, recordId,
        tokenPayload.getUserId());

    return AgonyRecordResponse.from(agonyRecord);
  }

  @DeleteMapping("/{recordId}")
  public void deleteAgonyRecord(@PathVariable Long bookShelfId, @PathVariable Long agonyId, @PathVariable Long recordId,
      @UserPayload TokenPayload tokenPayload) {

    agonyRecordService.deleteAgonyRecord(bookShelfId, agonyId, recordId, tokenPayload.getUserId());
  }

  @PutMapping("/{recordId}")
  public void reviseAgonyRecord(@PathVariable Long bookShelfId, @PathVariable Long agonyId, @PathVariable Long recordId,
      @Valid @RequestBody ReviseAgonyRecordRequest reviseAgonyRecordRequest, @UserPayload TokenPayload tokenPayload) {

    agonyRecordService.reviseAgonyRecord(bookShelfId, agonyId, recordId, tokenPayload.getUserId(),
        reviseAgonyRecordRequest.toTarget());
  }
}
