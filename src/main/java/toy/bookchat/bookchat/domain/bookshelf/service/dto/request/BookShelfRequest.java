package toy.bookchat.bookchat.domain.bookshelf.service.dto.request;

import static toy.bookchat.bookchat.domain.bookshelf.ReadingStatus.COMPLETE;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.db_module.book.BookEntity;
import toy.bookchat.bookchat.db_module.bookshelf.BookShelfEntity;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.Star;
import toy.bookchat.bookchat.db_module.user.UserEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookShelfRequest {

    @Valid
    @NotNull
    private BookRequest bookRequest;
    @NotNull
    private ReadingStatus readingStatus;
    private Star star;

    @Builder
    private BookShelfRequest(BookRequest bookRequest, ReadingStatus readingStatus, Star star) {
        this.bookRequest = bookRequest;
        this.readingStatus = readingStatus;
        this.star = star;
    }

    public BookEntity extractBookEntity() {
        return this.bookRequest.extractBookEntity();
    }

    @JsonIgnore
    public boolean isCompleteReading() {
        if (this.readingStatus == COMPLETE) {
            return isEvaluated();
        }
        return false;
    }

    private boolean isEvaluated() {
        if (this.star == null) {
            throw new IllegalStateException(
                "Star is required to change bookshelf complete reading status");
        }
        return true;
    }

    @JsonIgnore
    public String getIsbn() {
        return this.bookRequest.getIsbn();
    }

    @JsonIgnore
    public LocalDate getPublishAt() {
        return this.bookRequest.getPublishAt();
    }

    public BookShelfEntity createBookShelfByReadingStatus(BookEntity bookEntity, UserEntity userEntity) {
        if (this.isCompleteReading()) {
            return BookShelfEntity.builder()
                .bookEntity(bookEntity)
                .readingStatus(this.getReadingStatus())
                .userEntity(userEntity)
                .star(this.getStar())
                .build();
        }
        return BookShelfEntity.builder()
            .bookEntity(bookEntity)
            .readingStatus(this.getReadingStatus())
            .userEntity(userEntity)
            .build();
    }
}
