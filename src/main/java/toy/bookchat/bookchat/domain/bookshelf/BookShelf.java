package toy.bookchat.bookchat.domain.bookshelf;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.domain.BaseEntity;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.bookreport.BookReport;
import toy.bookchat.bookchat.domain.user.User;

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(
        columnNames = {"user_id", "book_id"}
    )
})
@Getter
public class BookShelf extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer pages;
    @Enumerated(EnumType.STRING)
    private ReadingStatus readingStatus;
    @Enumerated(EnumType.STRING)
    private Star star;
    private String singleLineAssessment;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;
    @OneToOne(fetch = FetchType.LAZY)
    private BookReport bookReport;

    @Builder
    private BookShelf(Long id, Integer pages, ReadingStatus readingStatus, Star star,
        String singleLineAssessment, User user, Book book, BookReport bookReport) {
        this.id = id;
        this.pages = pages;
        this.readingStatus = readingStatus;
        this.star = star;
        this.singleLineAssessment = singleLineAssessment;
        this.user = user;
        this.book = book;
        this.bookReport = bookReport;
    }

    protected BookShelf() {
    }

    public String getIsbn() {
        return this.book.getIsbn();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getBookTitle() {
        return this.book.getTitle();
    }

    public List<String> getBookAuthors() {
        return this.book.getAuthors();
    }

    public String getBookPublisher() {
        return this.book.getBookCoverImageUrl();
    }

    public String getBookCoverImageUrl() {
        return this.book.getBookCoverImageUrl();
    }

    public boolean isNotReadingStatus() {
        return this.readingStatus != ReadingStatus.READING;
    }

    public void updatePage(Integer pages) {
        this.pages = pages;
    }

    public Long getBookId() {
        return this.book.getId();
    }

    public void updateReadingStatus(ReadingStatus readingStatus) {
        this.readingStatus = readingStatus;
    }

    public void setBookReport(BookReport bookReport) {
        this.bookReport = bookReport;
    }

    public void changeToCompleteReading() {
        this.readingStatus = ReadingStatus.COMPLETE;
    }
}
