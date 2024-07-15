package toy.bookchat.bookchat.domain.bookshelf.service;

import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.bookshelf.BookShelfEntity;
import toy.bookchat.bookchat.db_module.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.user.User;

@Component
public class BookShelfAppender {

    private final BookShelfRepository bookShelfRepository;

    public BookShelfAppender(BookShelfRepository bookShelfRepository) {
        this.bookShelfRepository = bookShelfRepository;
    }

    public Long append(BookShelf bookShelf, User user, Book book) {
        BookShelfEntity bookShelfEntity = BookShelfEntity.builder()
            .userId(user.getId())
            .bookId(book.getId())
            .readingStatus(bookShelf.getReadingStatus())
            .star(bookShelf.getStar())
            .pages(bookShelf.getPages())
            .build();

        bookShelfRepository.save(bookShelfEntity);

        return bookShelfEntity.getId();
    }
}
