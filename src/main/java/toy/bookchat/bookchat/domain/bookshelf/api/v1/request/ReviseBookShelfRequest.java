package toy.bookchat.bookchat.domain.bookshelf.service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.bookshelf.BookShelfPageAndStarAndReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.Star;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviseBookShelfRequest {

    private Integer pages;
    @NotNull
    @JsonProperty("readingStatus")
    private ReadingStatus readingStatus;
    private Star star;

    @Builder
    private ReviseBookShelfRequest(Integer pages, ReadingStatus readingStatus, Star star) {
        this.pages = pages;
        this.readingStatus = readingStatus;
        this.star = star;
    }

    public BookShelfPageAndStarAndReadingStatus toTarget() {
        return BookShelfPageAndStarAndReadingStatus.builder()
            .pages(this.pages)
            .star(this.star)
            .readingStatus(this.readingStatus)
            .build();

    }
}
