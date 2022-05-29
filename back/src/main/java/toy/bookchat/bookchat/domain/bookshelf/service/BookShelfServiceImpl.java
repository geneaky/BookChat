package toy.bookchat.bookchat.domain.bookshelf.service;

import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import toy.bookchat.bookchat.domain.bookshelf.dto.BookShelfRequestDto;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;

@Service
@RequiredArgsConstructor
public class BookShelfServiceImpl implements BookShelfService {

    private final BookShelfRepository bookShelfRepository;

    @Override
    @Transactional
    public void putBookOnBookShelf(BookShelfRequestDto bookShelfRequestDto) {
        bookShelfRepository.save(bookShelfRequestDto.getBook());
    }
}
