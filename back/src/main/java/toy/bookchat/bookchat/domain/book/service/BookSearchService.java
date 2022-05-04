package toy.bookchat.bookchat.domain.book.service;

import org.springframework.stereotype.Service;
import toy.bookchat.bookchat.domain.book.dto.BookDto;

@Service
public interface BookSearchService {

    BookDto search(String isbn);
}
