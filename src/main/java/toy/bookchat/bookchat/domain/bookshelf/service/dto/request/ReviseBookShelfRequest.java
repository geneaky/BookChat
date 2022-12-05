package toy.bookchat.bookchat.domain.bookshelf.service.dto.request;

import static toy.bookchat.bookchat.domain.bookshelf.ReadingStatus.COMPLETE;
import static toy.bookchat.bookchat.domain.bookshelf.ReadingStatus.READING;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.Star;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviseBookShelfRequest {

    private Optional<Integer> pages;
    @NotNull
    @JsonProperty("readingStatus")
    private ReadingStatus readingStatus;
    private Optional<Star> star;

    @Builder
    private ReviseBookShelfRequest(Optional<Integer> pages,
        ReadingStatus readingStatus, Optional<Star> star) {
        this.pages = pages;
        this.readingStatus = readingStatus;
        this.star = star;
    }

    public void applyChanges(BookShelf bookShelf) {
        if (bookShelf.isCompleteReading()) { //독서완료한 상태에서만 별점 변경 가능
            this.star.ifPresent(bookShelf::updateStar);
        }
        if (bookShelf.isReading()) { //독서중 상태에서만 페이지 쪽수 지정
            this.pages.ifPresent(bookShelf::updatePage);
            changeReadingStatusToComplete(bookShelf);
        }
        if (bookShelf.isWish() && this.readingStatus == READING) { // 독서예정 상태에서 독서중으로 변경
            bookShelf.updateReadingStatus(this.readingStatus);
        }
    }

    private void changeReadingStatusToComplete(BookShelf bookShelf) {
        if (this.readingStatus == COMPLETE && this.star.isPresent()) { //독서중에서 독서 완료로 변경시 별점이 필수
            bookShelf.updateReadingStatus(this.readingStatus);
            this.star.ifPresent(bookShelf::updateStar);
        }
    }
}
