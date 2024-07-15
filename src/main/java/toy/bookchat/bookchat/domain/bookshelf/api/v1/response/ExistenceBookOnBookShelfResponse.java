package toy.bookchat.bookchat.domain.bookshelf.api.v1.response;

import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;

@Getter
public class ExistenceBookOnBookShelfResponse {

    private Long bookShelfId;
    private ReadingStatus readingStatus;

    @Builder
    private ExistenceBookOnBookShelfResponse(Long bookShelfId, ReadingStatus readingStatus) {
        this.bookShelfId = bookShelfId;
        this.readingStatus = readingStatus;
    }

    public static ExistenceBookOnBookShelfResponse from(BookShelf bookShelf) {
        return new ExistenceBookOnBookShelfResponse(bookShelf.getId(), bookShelf.getReadingStatus());
    }
}
