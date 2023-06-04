package toy.bookchat.bookchat.domain.scrap.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.domain.scrap.Scrap;
import toy.bookchat.bookchat.domain.scrap.repository.ScrapRepository;
import toy.bookchat.bookchat.domain.scrap.service.dto.request.CreateScrapRequest;
import toy.bookchat.bookchat.domain.scrap.service.dto.response.ScrapResponseSlice;
import toy.bookchat.bookchat.exception.notfound.book.BookNotFoundException;

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

    @Transactional(readOnly = true)
    public ScrapResponseSlice getScraps(Long bookShelfId, Long postCursorId, Pageable pageable,
        Long userId) {
        return ScrapResponseSlice.of(
            scrapRepository.findScraps(bookShelfId, postCursorId, pageable, userId));
    }

    public void deleteScraps(Long bookShelfId, List<Long> scrapIds, Long userId) {
        BookShelf bookShelf = bookShelfRepository.findByIdAndUserId(bookShelfId, userId)
            .orElseThrow(BookNotFoundException::new);

        List<Scrap> scrapList = scrapRepository.findAllById(scrapIds).stream()
            .filter(scrap -> scrap.getBookShelf() == bookShelf).collect(Collectors.toList());

        scrapRepository.deleteAllInBatch(scrapList);
    }
}
