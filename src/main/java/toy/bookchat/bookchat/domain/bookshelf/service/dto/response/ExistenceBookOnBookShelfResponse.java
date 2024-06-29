package toy.bookchat.bookchat.domain.bookshelf.service.dto.response;

import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.db_module.bookshelf.BookShelfEntity;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;

@Getter
public class ExistenceBookOnBookShelfResponse {

    private Long bookShelfId;
    private ReadingStatus readingStatus;

    @Builder
    private ExistenceBookOnBookShelfResponse(Long bookShelfId,
        ReadingStatus readingStatus) {
        this.bookShelfId = bookShelfId;
        this.readingStatus = readingStatus;
    }

    public static ExistenceBookOnBookShelfResponse from(BookShelfEntity bookShelfEntity) {
        return new ExistenceBookOnBookShelfResponse(bookShelfEntity.getId(), bookShelfEntity.getReadingStatus());
    }
}
