package toy.bookchat.bookchat.domain.scrap.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.bookshelf.BookShelfEntity;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.domain.scrap.ScrapEntity;
import toy.bookchat.bookchat.domain.scrap.repository.ScrapRepository;
import toy.bookchat.bookchat.domain.scrap.service.dto.request.CreateScrapRequest;
import toy.bookchat.bookchat.domain.scrap.service.dto.response.ScrapResponse;
import toy.bookchat.bookchat.domain.scrap.service.dto.response.ScrapResponseSlice;
import toy.bookchat.bookchat.exception.notfound.book.BookNotFoundException;
import toy.bookchat.bookchat.exception.notfound.scrap.ScrapNotFoundException;

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
    public Long scrap(CreateScrapRequest createScrapRequest, Long userId) {
        BookShelfEntity bookShelfEntity = bookShelfRepository.findByIdAndUserId(createScrapRequest.getBookShelfId(), userId).orElseThrow(BookNotFoundException::new);
        ScrapEntity scrapEntity = createScrapRequest.create(bookShelfEntity);
        scrapRepository.save(scrapEntity);

        return scrapEntity.getId();
    }

    @Transactional(readOnly = true)
    public ScrapResponseSlice getScraps(Long bookShelfId, Long postCursorId, Pageable pageable, Long userId) {
        return ScrapResponseSlice.of(scrapRepository.findScraps(bookShelfId, postCursorId, pageable, userId));
    }

    @Transactional(readOnly = true)
    public ScrapResponse getScrap(Long scrapId, Long userId) {
        ScrapEntity scrapEntity = scrapRepository.findUserScrap(scrapId, userId).orElseThrow(ScrapNotFoundException::new);
        return ScrapResponse.from(scrapEntity);
    }

    public void deleteScraps(Long bookShelfId, List<Long> scrapIds, Long userId) {
        BookShelfEntity bookShelfEntity = bookShelfRepository.findByIdAndUserId(bookShelfId, userId).orElseThrow(BookNotFoundException::new);

        List<ScrapEntity> scrapEntityList = scrapRepository.findAllById(scrapIds)
            .stream()
            .filter(scrap -> scrap.getBookShelfEntity() == bookShelfEntity)
            .collect(Collectors.toList());

        scrapRepository.deleteAllInBatch(scrapEntityList);
    }
}
