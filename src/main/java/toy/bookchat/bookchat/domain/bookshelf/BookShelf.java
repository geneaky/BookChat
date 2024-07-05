package toy.bookchat.bookchat.domain.bookshelf;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class BookShelf {

    private Long id;

    @Builder
    private BookShelf(Long id) {
        this.id = id;
    }
}
