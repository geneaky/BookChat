package toy.bookchat.bookchat.domain.bookshelf.service.dto.response;

import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;

@Getter
public class ExistenceBookOnBookShelfResponse {

    private Long bookShelfId;
    private Long bookId;
    private ReadingStatus readingStatus;

    @Builder
    private ExistenceBookOnBookShelfResponse(Long bookShelfId, Long bookId,
        ReadingStatus readingStatus) {
        this.bookShelfId = bookShelfId;
        this.bookId = bookId;
        this.readingStatus = readingStatus;
    }
}
