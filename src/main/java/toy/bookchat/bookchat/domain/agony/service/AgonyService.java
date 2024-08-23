package toy.bookchat.bookchat.domain.agony.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.agony.AgonyTitleAndColorCode;
import toy.bookchat.bookchat.domain.agonyrecord.service.AgonyRecordCleaner;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.service.BookShelfReader;

@Service
public class AgonyService {

  private final BookShelfReader bookShelfReader;
  private final AgonyManager agonyManager;
  private final AgonyReader agonyReader;
  private final AgonyAppender agonyAppender;
  private final AgonyCleaner agonyCleaner;
  private final AgonyRecordCleaner agonyRecordCleaner;

  public AgonyService(AgonyReader agonyReader, BookShelfReader bookShelfReader, AgonyAppender agonyAppender,
      AgonyCleaner agonyCleaner, AgonyRecordCleaner agonyRecordCleaner,
      AgonyManager agonyManager) {
    this.agonyReader = agonyReader;
    this.bookShelfReader = bookShelfReader;
    this.agonyAppender = agonyAppender;
    this.agonyCleaner = agonyCleaner;
    this.agonyRecordCleaner = agonyRecordCleaner;
    this.agonyManager = agonyManager;
  }

  @Transactional
  public Long storeBookShelfAgony(Agony agony, Long userId, Long bookShelfId) {
    BookShelf BookShelf = bookShelfReader.readBookShelf(userId, bookShelfId);
    Long agonyId = agonyAppender.append(agony, BookShelf);
    return agonyId;
  }

  @Transactional(readOnly = true)
  public Agony searchAgony(Long bookShelfId, Long agonyId, Long userId) {
    return agonyReader.readAgony(userId, bookShelfId, agonyId);
  }

  @Transactional(readOnly = true)
  public Slice<Agony> searchSliceOfAgonies(Long bookShelfId, Long userId, Pageable pageable, Long postCursorId) {
    return agonyReader.readSlicedAgony(userId, bookShelfId, pageable, postCursorId);
  }

  @Transactional
  public void deleteAgony(Long bookShelfId, List<Long> agoniesIds, Long userId) {
    agonyRecordCleaner.clean(userId, bookShelfId, agoniesIds);
    agonyCleaner.clean(userId, bookShelfId, agoniesIds);
  }

  @Transactional
  public void reviseAgony(Long bookShelfId, Long agonyId, Long userId, AgonyTitleAndColorCode agonyTitleAndColorCode) {
    agonyManager.modify(userId, bookShelfId, agonyId, agonyTitleAndColorCode);
  }
}
