package toy.bookchat.bookchat.domain.bookshelf;

import static toy.bookchat.bookchat.domain.bookshelf.ReadingStatus.COMPLETE;
import static toy.bookchat.bookchat.domain.bookshelf.ReadingStatus.READING;
import static toy.bookchat.bookchat.domain.bookshelf.ReadingStatus.WISH;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import toy.bookchat.bookchat.domain.book.Book;

@Getter
@EqualsAndHashCode
public class BookShelf {

    private Long id;
    private Book book;
    private Star star;
    private ReadingStatus readingStatus;
    private Integer pages;
    private LocalDateTime lastUpdatedAt;

    @Builder
    private BookShelf(Long id, Book book, Star star, ReadingStatus readingStatus, Integer pages, LocalDateTime lastUpdatedAt) {
        this.id = id;
        this.book = book;
        this.star = star;
        this.readingStatus = readingStatus;
        this.pages = pages;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public boolean isCompleteReading() {
        return this.readingStatus == COMPLETE;
    }

    public void updateStar(Star star) {
        this.star = star;
    }

    public boolean isReading() {
        return this.readingStatus == READING;
    }

    public void updatePage(Integer pages) {
        this.pages = pages;
    }

    public void updateReadingStatus(ReadingStatus readingStatus) {
        this.readingStatus = readingStatus;
    }

    public boolean isWish() {
        return this.readingStatus == WISH;
    }
}
