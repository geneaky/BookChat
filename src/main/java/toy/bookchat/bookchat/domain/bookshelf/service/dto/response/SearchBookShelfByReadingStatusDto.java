package toy.bookchat.bookchat.domain.bookshelf.service.dto.response;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Page;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.common.PageDto;

@Getter
public class SearchBookShelfByReadingStatusDto extends PageDto {

    private List<BookShelfResponseDto> contents;

    public SearchBookShelfByReadingStatusDto(Page<BookShelf> pagingBookShelves) {
        super(pagingBookShelves);
        getBookShelfSearchResponseDtos(pagingBookShelves.getContent());
    }

    private void getBookShelfSearchResponseDtos(
        List<BookShelf> bookShelves) {
        this.contents = new ArrayList<>();
        fillContentsWithBookShelfResponseDto(bookShelves);
    }

    private void fillContentsWithBookShelfResponseDto(List<BookShelf> bookShelves) {
        for (BookShelf bookShelf : bookShelves) {
            BookShelfResponseDto bookShelfResponseDto = BookShelfResponseDto.builder()
                .bookId(bookShelf.getBookId())
                .title(bookShelf.getBookTitle())
                .isbn(bookShelf.getIsbn())
                .authors(bookShelf.getBookAuthors())
                .publisher(bookShelf.getBookPublisher())
                .bookCoverImageUrl(bookShelf.getBookCoverImageUrl())
                .star(bookShelf.getStar())
                .singleLineAssessment(bookShelf.getSingleLineAssessment())
                .pages(bookShelf.getPages())
                .build();

            this.contents.add(bookShelfResponseDto);
        }
    }
}
