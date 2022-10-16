package toy.bookchat.bookchat.domain.bookshelf.service.dto;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.common.PageDto;

public class SearchBookShelfByReadingStatusDto extends PageDto {

    private List<BookShelfResponseDto> contents;

    public SearchBookShelfByReadingStatusDto(Page<BookShelf> pagingBookShelves) {
        super(pagingBookShelves);
        getBookShelfSearchResponseDtos(pagingBookShelves.getContent());
    }

    private void getBookShelfSearchResponseDtos(
        List<BookShelf> bookShelves) {
        this.contents = new ArrayList<>();

        for (BookShelf bookShelf : bookShelves) {
            BookShelfResponseDto bookShelfResponseDto = BookShelfResponseDto.builder()
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
