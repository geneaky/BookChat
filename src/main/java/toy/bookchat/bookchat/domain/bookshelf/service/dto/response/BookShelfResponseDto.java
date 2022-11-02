package toy.bookchat.bookchat.domain.bookshelf.service.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.domain.bookshelf.Star;

@Getter
public class BookShelfResponseDto {

    private Long bookId;
    private String title;
    private String isbn;
    private String bookCoverImageUrl;
    private List<String> authors;
    private String publisher;
    private Star star;
    private String singleLineAssessment;
    private Integer pages;

    @Builder
    private BookShelfResponseDto(Long bookId, String title, String isbn,
        String bookCoverImageUrl, List<String> authors, String publisher,
        Star star, String singleLineAssessment, Integer pages) {
        this.bookId = bookId;
        this.title = title;
        this.isbn = isbn;
        this.bookCoverImageUrl = bookCoverImageUrl;
        this.authors = authors;
        this.publisher = publisher;
        this.star = star;
        this.singleLineAssessment = singleLineAssessment;
        this.pages = pages;
    }
}
