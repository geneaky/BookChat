package toy.bookchat.bookchat.domain.agony.api;

import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.agony.service.AgonyService;
import toy.bookchat.bookchat.domain.agony.service.dto.CreateBookAgonyRequestDto;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.security.user.CurrentUser;

@RestController
@RequestMapping("/v1/api")
public class AgonyController {

    private final AgonyService agonyService;

    public AgonyController(AgonyService agonyService) {
        this.agonyService = agonyService;
    }

    @PostMapping("/bookshelf/{bookShelfId}/agonies")
    public ResponseEntity<Void> makeBookAgony(@PathVariable Long bookShelfId,
        @Valid @RequestBody CreateBookAgonyRequestDto createBookAgonyRequestDto,
        @CurrentUser User user) {
        agonyService.storeBookAgony(createBookAgonyRequestDto, user, bookShelfId);
        return ResponseEntity.ok().build();
    }

}
