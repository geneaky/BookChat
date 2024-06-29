package toy.bookchat.bookchat.domain.bookshelf.service.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import toy.bookchat.bookchat.db_module.bookshelf.BookShelfEntity;
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

    public static BookShelfResponse from(BookShelfEntity bookShelfEntity) {
        return BookShelfResponse.builder()
            .bookShelfId(bookShelfEntity.getId())
            .title(bookShelfEntity.getBookTitle())
            .isbn(bookShelfEntity.getIsbn())
            .authors(bookShelfEntity.getBookAuthors())
            .publisher(bookShelfEntity.getBookPublisher())
            .bookCoverImageUrl(bookShelfEntity.getBookCoverImageUrl())
            .publishAt(bookShelfEntity.getBookEntity().getPublishAt())
            .star(bookShelfEntity.getStar())
            .pages(bookShelfEntity.getPages())
            .lastUpdatedAt(bookShelfEntity.getUpdatedAt())
            .build();
    }
}
