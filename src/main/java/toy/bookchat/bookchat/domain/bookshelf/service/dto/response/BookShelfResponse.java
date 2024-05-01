package toy.bookchat.bookchat.domain.bookshelf.service.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.Star;

@Getter
@EqualsAndHashCode
public class BookShelfResponse {

    private Long bookShelfId;
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
    private BookShelfResponse(Long bookShelfId, String title, String isbn,
        String bookCoverImageUrl, LocalDate publishAt, List<String> authors, String publisher,
        Star star, Integer pages, LocalDateTime lastUpdatedAt) {
        this.bookShelfId = bookShelfId;
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

    public static BookShelfResponse from(BookShelf bookShelf) {
        return BookShelfResponse.builder()
            .bookShelfId(bookShelf.getId())
            .title(bookShelf.getBookTitle())
            .isbn(bookShelf.getIsbn())
            .authors(bookShelf.getBookAuthors())
            .publisher(bookShelf.getBookPublisher())
            .bookCoverImageUrl(bookShelf.getBookCoverImageUrl())
            .publishAt(bookShelf.getBook().getPublishAt())
            .star(bookShelf.getStar())
            .pages(bookShelf.getPages())
            .lastUpdatedAt(bookShelf.getUpdatedAt())
            .build();
    }
}
