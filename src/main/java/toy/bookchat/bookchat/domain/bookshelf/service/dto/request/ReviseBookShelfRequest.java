package toy.bookchat.bookchat.domain.bookshelf.service.dto.request;

import static toy.bookchat.bookchat.domain.bookshelf.ReadingStatus.COMPLETE;
import static toy.bookchat.bookchat.domain.bookshelf.ReadingStatus.READING;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.bookshelf.BookShelfEntity;
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

    public void applyChanges(BookShelfEntity bookShelfEntity) {
        if (bookShelfEntity.isCompleteReading() && starPresent()) { //독서완료한 상태에서만 별점 변경 가능
            bookShelfEntity.updateStar(this.star);
        }
        if (bookShelfEntity.isReading() && pagePresent()) { //독서중 상태에서만 페이지 쪽수 지정
            bookShelfEntity.updatePage(this.pages);
            changeReadingStatusToComplete(bookShelfEntity);
        }
        if (bookShelfEntity.isWish() && this.readingStatus == READING) { // 독서예정 상태에서 독서중으로 변경
            bookShelfEntity.updateReadingStatus(this.readingStatus);
        }
    }

    private void changeReadingStatusToComplete(BookShelfEntity bookShelfEntity) {
        if (this.readingStatus == COMPLETE && starPresent()) { //독서중에서 독서 완료로 변경시 별점이 필수
            bookShelfEntity.updateReadingStatus(this.readingStatus);
            bookShelfEntity.updateStar(this.star);
        }
    }

    private boolean starPresent() {
        return this.star != null;
    }

    private boolean pagePresent() {
        return this.pages != null;
    }
}
