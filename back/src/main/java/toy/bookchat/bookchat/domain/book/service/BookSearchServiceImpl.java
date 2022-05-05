package toy.bookchat.bookchat.domain.book.service;

import org.springframework.stereotype.Service;
import toy.bookchat.bookchat.domain.book.dto.BookDto;

@Service
public class BookSearchServiceImpl implements BookSearchService {

    @Override
    public BookDto searchByIsbn(String isbn) {
        return null;
    }

    @Override
    public BookDto searchByTitle(String title) {
        return null;
    }

    @Override
    public BookDto searchByAuthor(String author) {
        return null;
    }
}
