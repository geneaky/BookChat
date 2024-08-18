package toy.bookchat.bookchat.domain.scrap.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.db_module.bookshelf.BookShelfEntity;
import toy.bookchat.bookchat.db_module.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.db_module.scrap.ScrapEntity;
import toy.bookchat.bookchat.db_module.scrap.repository.ScrapEntityEntityRepository;
import toy.bookchat.bookchat.domain.scrap.api.v1.request.CreateScrapRequest;
import toy.bookchat.bookchat.domain.scrap.api.v1.response.ScrapResponse;
import toy.bookchat.bookchat.domain.scrap.api.v1.response.ScrapResponseSlice;
import toy.bookchat.bookchat.exception.notfound.book.BookNotFoundException;
import toy.bookchat.bookchat.exception.notfound.scrap.ScrapNotFoundException;

@Service
public class ScrapService {

  private final BookShelfRepository bookShelfRepository;
  private final ScrapEntityEntityRepository scrapEntityRepository;

  public ScrapService(BookShelfRepository bookShelfRepository,
      ScrapEntityEntityRepository scrapEntityRepository) {
    this.bookShelfRepository = bookShelfRepository;
    this.scrapEntityRepository = scrapEntityRepository;
  }

  @Transactional
  public Long scrap(CreateScrapRequest createScrapRequest, Long userId) {
    BookShelfEntity bookShelfEntity = bookShelfRepository.findByIdAndUserId(createScrapRequest.getBookShelfId(), userId)
        .orElseThrow(BookNotFoundException::new);
    ScrapEntity scrapEntity = createScrapRequest.create(bookShelfEntity.getId());
    scrapEntityRepository.save(scrapEntity);

    return scrapEntity.getId();
  }

  @Transactional(readOnly = true)
  public ScrapResponseSlice getScraps(Long bookShelfId, Long postCursorId, Pageable pageable, Long userId) {
    return ScrapResponseSlice.of(scrapEntityRepository.findScraps(bookShelfId, postCursorId, pageable, userId));
  }

  @Transactional(readOnly = true)
  public ScrapResponse getScrap(Long scrapId, Long userId) {
    ScrapEntity scrapEntity = scrapEntityRepository.findUserScrap(scrapId, userId)
        .orElseThrow(ScrapNotFoundException::new);
    return ScrapResponse.from(scrapEntity);
  }

  public void deleteScraps(Long bookShelfId, List<Long> scrapIds, Long userId) {
    BookShelfEntity bookShelfEntity = bookShelfRepository.findByIdAndUserId(bookShelfId, userId)
        .orElseThrow(BookNotFoundException::new);

    List<ScrapEntity> scrapEntityList = scrapEntityRepository.findAllById(scrapIds)
        .stream()
        .filter(scrap -> scrap.getBookShelfId() == bookShelfEntity.getId())
        .collect(Collectors.toList());

    scrapEntityRepository.deleteAllInBatch(scrapEntityList);
  }
}
