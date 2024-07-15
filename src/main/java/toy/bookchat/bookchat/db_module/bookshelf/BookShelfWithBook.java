package toy.bookchat.bookchat.db_module.bookshelf;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.Star;

@Getter
@EqualsAndHashCode
public class BookShelfWithBook {

    private Long bookShelfId;
    private Long bookId;
    private String title;
    private String isbn;
    private String bookCoverImageUrl;
    private LocalDate publishAt;
    private List<String> authors;
    private String publisher;
    private Star star;
    private Integer pages;
    private LocalDateTime lastUpdatedAt;

    @Builder
    private BookShelfWithBook(Long bookShelfId, Long bookId, String title, String isbn, String bookCoverImageUrl, LocalDate publishAt, List<String> authors, String publisher, Star star, Integer pages,
        LocalDateTime lastUpdatedAt) {
        this.bookShelfId = bookShelfId;
        this.bookId = bookId;
        this.title = title;
        this.isbn = isbn;
        this.bookCoverImageUrl = bookCoverImageUrl;
        this.publishAt = publishAt;
        this.authors = authors;
        this.publisher = publisher;
        this.star = star;
        this.pages = pages;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    @QueryProjection
    public BookShelfWithBook(Long bookShelfId, Long bookId, String title, String isbn, String bookCoverImageUrl, LocalDate publishAt, String publisher, Star star, Integer pages,
        LocalDateTime lastUpdatedAt) {
        this.bookShelfId = bookShelfId;
        this.bookId = bookId;
        this.title = title;
        this.isbn = isbn;
        this.bookCoverImageUrl = bookCoverImageUrl;
        this.publishAt = publishAt;
        this.publisher = publisher;
        this.star = star;
        this.pages = pages;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }


    public BookShelf toBookShelf() {
        Book book = Book.builder()
            .title(this.title)
            .isbn(this.isbn)
            .bookCoverImageUrl(this.bookCoverImageUrl)
            .publisher(this.publisher)
            .publishAt(this.publishAt)
            .authors(this.authors)
            .build();

        return BookShelf.builder()
            .id(this.bookShelfId)
            .book(book)
            .star(this.star)
            .pages(this.pages)
            .lastUpdatedAt(this.lastUpdatedAt)
            .build();
    }
}
