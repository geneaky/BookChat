package toy.bookchat.bookchat.domain.bookshelf.service.dto.response;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Page;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.common.PageMeta;

@Getter
public class SearchBookShelfByReadingStatus {

    private PageMeta pageMeta;
    private List<BookShelfResponse> contents;

    public SearchBookShelfByReadingStatus(Page<BookShelf> pagingBookShelves) {
        this.pageMeta = PageMeta.from(pagingBookShelves);
        getBookShelfSearchResponseDtos(pagingBookShelves.getContent());
    }

    private void getBookShelfSearchResponseDtos(List<BookShelf> bookShelves) {
        this.contents = new ArrayList<>();

        for (BookShelf bookShelf : bookShelves) {
            BookShelfResponse bookShelfResponse = BookShelfResponse.builder()
                .bookId(bookShelf.getBookId())
                .title(bookShelf.getBookTitle())
                .isbn(bookShelf.getIsbn())
                .authors(bookShelf.getBookAuthors())
                .publisher(bookShelf.getBookPublisher())
                .bookCoverImageUrl(bookShelf.getBookCoverImageUrl())
                .publishAt(bookShelf.getBook().getPublishAt())
                .star(bookShelf.getStar())
                .pages(bookShelf.getPages())
                .build();

            contents.add(bookShelfResponse);
        }
    }
}
