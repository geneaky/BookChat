package toy.bookchat.bookchat.domain.agony.api;

import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.agony.service.AgonyRecordService;
import toy.bookchat.bookchat.domain.agony.service.AgonyService;
import toy.bookchat.bookchat.domain.agony.service.dto.CreateAgonyRecordRequestDto;
import toy.bookchat.bookchat.domain.agony.service.dto.CreateBookAgonyRequestDto;
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
    public ResponseEntity<Void> makeBookAgony(@PathVariable Long bookId,
        @Valid @RequestBody CreateBookAgonyRequestDto createBookAgonyRequestDto,
        @CurrentUser User user) {
        agonyService.storeBookAgony(createBookAgonyRequestDto, user, bookId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/bookshelf/books/{bookId}/agonies/{agonyId}/records")
    public ResponseEntity<Void> addAgonyRecordOnBookAgony(@PathVariable Long bookId,
        @PathVariable Long agonyId,
        @Valid @RequestBody CreateAgonyRecordRequestDto createAgonyRecordRequestDto,
        @CurrentUser User user) {
        agonyRecordService.storeAgonyRecord(createAgonyRecordRequestDto, user, bookId, agonyId);
        return ResponseEntity.ok().build();
    }
}
