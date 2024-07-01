package toy.bookchat.bookchat.domain.bookshelf;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BookShelf {

    private Long id;

    @Builder
    private BookShelf(Long id) {
        this.id = id;
    }
}
