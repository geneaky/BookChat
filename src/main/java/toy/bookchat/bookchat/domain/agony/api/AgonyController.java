package toy.bookchat.bookchat.domain.agony.api;

import javax.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.agony.service.AgonyRecordService;
import toy.bookchat.bookchat.domain.agony.service.AgonyService;
import toy.bookchat.bookchat.domain.agony.service.dto.request.CreateAgonyRecordRequestDto;
import toy.bookchat.bookchat.domain.agony.service.dto.request.CreateBookAgonyRequestDto;
import toy.bookchat.bookchat.domain.agony.service.dto.response.PageOfAgoniesResponse;
import toy.bookchat.bookchat.domain.agony.service.dto.response.PageOfAgonyRecordsResponse;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPayload;

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
    public void makeBookAgony(@PathVariable Long bookId,
        @Valid @RequestBody CreateBookAgonyRequestDto createBookAgonyRequestDto,
        @UserPayload TokenPayload tokenPayload) {

        agonyService.storeBookAgony(createBookAgonyRequestDto, tokenPayload.getUserId(), bookId);
    }

    @GetMapping("/bookshelf/books/{bookId}/agonies")
    public PageOfAgoniesResponse searchPageOfAgonies(@PathVariable Long bookId, Pageable pageable,
        @UserPayload TokenPayload tokenPayload) {

        return agonyService.searchPageOfAgonies(bookId, tokenPayload.getUserId(), pageable);
    }

    @PostMapping("/bookshelf/books/{bookId}/agonies/{agonyId}/records")
    public void addAgonyRecordOnBookAgony(@PathVariable Long bookId, @PathVariable Long agonyId,
        @Valid @RequestBody CreateAgonyRecordRequestDto createAgonyRecordRequestDto,
        @UserPayload TokenPayload tokenPayload) {

        agonyRecordService.storeAgonyRecord(createAgonyRecordRequestDto, tokenPayload.getUserId(),
            bookId,
            agonyId);
    }

    @GetMapping("/bookshelf/books/{bookId}/agonies/{agonyId}/records")
    public PageOfAgonyRecordsResponse getAgonyRecordsOnBookAgony(@PathVariable Long bookId,
        @PathVariable Long agonyId, @UserPayload TokenPayload tokenPayload, Pageable pageable) {

        return agonyRecordService.searchPageOfAgonyRecords(bookId, agonyId,
            tokenPayload.getUserId(), pageable);
    }
}