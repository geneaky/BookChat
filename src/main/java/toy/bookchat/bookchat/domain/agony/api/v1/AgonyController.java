package toy.bookchat.bookchat.domain.agony.api.v1;

import static org.springframework.http.HttpStatus.CREATED;

import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.agony.api.v1.request.CreateBookAgonyRequest;
import toy.bookchat.bookchat.domain.agony.api.v1.request.ReviseAgonyRequest;
import toy.bookchat.bookchat.domain.agony.api.v1.response.AgonyResponse;
import toy.bookchat.bookchat.domain.agony.api.v1.response.SliceOfAgoniesResponse;
import toy.bookchat.bookchat.domain.agony.service.AgonyService;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPayload;

@RequiredArgsConstructor

@RestController
@RequestMapping("/v1/api/bookshelves/{bookShelfId}/agonies")
public class AgonyController {

  private final AgonyService agonyService;

  /**
   * 책장에 고민 등록
   *
   * @param bookShelfId
   * @param request
   * @param tokenPayload
   * @return
   */
  @PostMapping
  public ResponseEntity<Void> makeBookAgony(@PathVariable Long bookShelfId,
      @Valid @RequestBody CreateBookAgonyRequest request, @UserPayload TokenPayload tokenPayload) {
    Long agonyId = agonyService.storeBookShelfAgony(request.toTarget(), tokenPayload.getUserId(), bookShelfId);

    return ResponseEntity.status(CREATED)
        .headers(hs -> hs.setLocation(URI.create("/v1/api/bookshelves/" + bookShelfId + "/agonies/" + agonyId)))
        .build();
  }

  /**
   * 고민 조회
   *
   * @param bookShelfId
   * @param agonyId
   * @param tokenPayload
   * @return
   */
  @GetMapping("/{agonyId}")
  public AgonyResponse searchAgony(@PathVariable Long bookShelfId, @PathVariable Long agonyId,
      @UserPayload TokenPayload tokenPayload) {

    return AgonyResponse.from(agonyService.searchAgony(bookShelfId, agonyId, tokenPayload.getUserId()));
  }

  /**
   * 고민 리스트 조회
   *
   * @param bookShelfId
   * @param postCursorId
   * @param pageable
   * @param tokenPayload
   * @return
   */
  @GetMapping
  public SliceOfAgoniesResponse searchSliceOfAgonies(@PathVariable Long bookShelfId, Long postCursorId,
      Pageable pageable, @UserPayload TokenPayload tokenPayload) {
    return new SliceOfAgoniesResponse(
        agonyService.searchSliceOfAgonies(bookShelfId, tokenPayload.getUserId(), pageable, postCursorId));
  }

  /**
   * 고민 삭제
   *
   * @param bookShelfId
   * @param agoniesIds
   * @param tokenPayload
   */
  @DeleteMapping("/{agoniesIds}")
  public void deleteAgony(@PathVariable Long bookShelfId, @PathVariable List<Long> agoniesIds,
      @UserPayload TokenPayload tokenPayload) {

    agonyService.deleteAgony(bookShelfId, agoniesIds, tokenPayload.getUserId());
  }

  /**
   * 고민 수정
   *
   * @param bookShelfId
   * @param agonyId
   * @param request
   * @param tokenPayload
   */
  @PutMapping("/{agonyId}")
  public void reviseAgony(@PathVariable Long bookShelfId, @PathVariable Long agonyId,
      @Valid @RequestBody ReviseAgonyRequest request, @UserPayload TokenPayload tokenPayload) {
    agonyService.reviseAgony(bookShelfId, agonyId, tokenPayload.getUserId(), request.toTarget());
  }
}
