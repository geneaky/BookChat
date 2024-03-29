package toy.bookchat.bookchat.domain.bookshelf.service.dto.response;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.domain.bookshelf.Star;

@Getter
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

    @Builder
    private BookShelfResponse(Long bookShelfId, String title, String isbn,
        String bookCoverImageUrl, LocalDate publishAt, List<String> authors, String publisher,
        Star star, Integer pages) {
        this.bookShelfId = bookShelfId;
        this.title = title;
        this.isbn = isbn;
        this.bookCoverImageUrl = bookCoverImageUrl;
        this.publishAt = publishAt;
        this.authors = authors;
        this.publisher = publisher;
        this.star = star;
        this.pages = pages;
    }
}
