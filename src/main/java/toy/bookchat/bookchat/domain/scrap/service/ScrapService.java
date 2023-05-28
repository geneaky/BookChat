package toy.bookchat.bookchat.domain.scrap.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.domain.scrap.repository.ScrapRepository;
import toy.bookchat.bookchat.domain.scrap.service.dto.request.CreateScrapRequest;
import toy.bookchat.bookchat.exception.book.BookNotFoundException;

@Service
public class ScrapService {

    private final BookShelfRepository bookShelfRepository;
    private final ScrapRepository scrapRepository;

    public ScrapService(BookShelfRepository bookShelfRepository,
        ScrapRepository scrapRepository) {
        this.bookShelfRepository = bookShelfRepository;
        this.scrapRepository = scrapRepository;
    }

    @Transactional
    public void scrap(CreateScrapRequest createScrapRequest, Long userId) {
        BookShelf bookShelf = bookShelfRepository.findByIdAndUserId(
            createScrapRequest.getBookShelfId(), userId).orElseThrow(BookNotFoundException::new);

        scrapRepository.save(createScrapRequest.create(bookShelf));
    }
}
