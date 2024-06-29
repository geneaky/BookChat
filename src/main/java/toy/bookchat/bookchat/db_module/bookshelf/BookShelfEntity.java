package toy.bookchat.bookchat.db_module.bookshelf;

import static toy.bookchat.bookchat.domain.bookshelf.ReadingStatus.COMPLETE;
import static toy.bookchat.bookchat.domain.bookshelf.ReadingStatus.READING;
import static toy.bookchat.bookchat.domain.bookshelf.ReadingStatus.WISH;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.db_module.BaseEntity;
import toy.bookchat.bookchat.db_module.book.BookEntity;
import toy.bookchat.bookchat.db_module.bookreport.BookReportEntity;
import toy.bookchat.bookchat.db_module.user.UserEntity;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.Star;
import toy.bookchat.bookchat.exception.notfound.bookshelf.BookReportNotFoundException;

@Getter
@Entity
@Table(name = "book_shelf")
public class BookShelfEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer pages;
    @Enumerated(EnumType.STRING)
    private ReadingStatus readingStatus;
    @Enumerated(EnumType.STRING)
    private Star star;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private BookEntity bookEntity;
    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "book_report_id")
    private BookReportEntity bookReportEntity;

    @Builder
    private BookShelfEntity(Long id, Integer pages, ReadingStatus readingStatus, Star star, UserEntity userEntity,
        BookEntity bookEntity, BookReportEntity bookReportEntity) {
        this.id = id;
        this.pages = pages;
        this.readingStatus = readingStatus;
        this.star = star;
        this.userEntity = userEntity;
        this.bookEntity = bookEntity;
        this.bookReportEntity = bookReportEntity;
    }

    protected BookShelfEntity() {
    }

    public String getIsbn() {
        return this.bookEntity.getIsbn();
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public void setBookEntity(BookEntity bookEntity) {
        this.bookEntity = bookEntity;
    }

    public String getBookTitle() {
        return this.bookEntity.getTitle();
    }

    public List<String> getBookAuthors() {
        loadBookAuthors();
        return this.bookEntity.getAuthors();
    }

    private void loadBookAuthors() {
        this.bookEntity.getAuthors().size();
    }

    public String getBookPublisher() {
        return this.bookEntity.getBookCoverImageUrl();
    }

    public String getBookCoverImageUrl() {
        return this.bookEntity.getBookCoverImageUrl();
    }

    public void updatePage(Integer pages) {
        this.pages = pages;
    }

    public Long getBookId() {
        return this.bookEntity.getId();
    }

    public void updateReadingStatus(ReadingStatus readingStatus) {
        this.readingStatus = readingStatus;
    }

    public void writeReportInStateOfCompleteReading(BookReportEntity bookReportEntity) {
        this.readingStatus = COMPLETE;
        this.bookReportEntity = bookReportEntity;
    }

    public BookReportEntity getBookReportEntity() {
        isReportedBook();
        return this.bookReportEntity;
    }

    private void isReportedBook() {
        if (this.bookReportEntity == null) {
            throw new BookReportNotFoundException();
        }
    }

    public void deleteBookReport() {
        this.bookReportEntity = null;
    }

    public void updateStar(Star star) {
        this.star = star;
    }

    public boolean isCompleteReading() {
        return this.readingStatus == COMPLETE;
    }

    public boolean isReading() {
        return this.readingStatus == READING;
    }

    public boolean isWish() {
        return this.readingStatus == WISH;
    }
}
