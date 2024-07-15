package toy.bookchat.bookchat.domain.bookshelf;

import static toy.bookchat.bookchat.domain.bookshelf.ReadingStatus.COMPLETE;
import static toy.bookchat.bookchat.domain.bookshelf.ReadingStatus.READING;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BookShelfPageAndStarAndReadingStatus {

    private Integer pages;
    private ReadingStatus readingStatus;
    private Star star;

    @Builder
    private BookShelfPageAndStarAndReadingStatus(Integer pages, ReadingStatus readingStatus, Star star) {
        this.pages = pages;
        this.readingStatus = readingStatus;
        this.star = star;
    }

    public boolean hasStar() {
        return this.star != null;
    }

    public boolean hasPages() {
        return this.pages != null;
    }

    public boolean isReadingComplete() {
        return this.readingStatus == COMPLETE;
    }

    public boolean isReading() {
        return this.readingStatus == READING;
    }
}
