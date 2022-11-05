package toy.bookchat.bookchat.domain.bookshelf.service.dto.request;

import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.bookshelf.Star;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviseBookShelfStarRequest {

    @NotNull
    private Star star;

    private ReviseBookShelfStarRequest(Star star) {
        this.star = star;
    }

    public static ReviseBookShelfStarRequest of(Star star) {
        return new ReviseBookShelfStarRequest(star);
    }
}
