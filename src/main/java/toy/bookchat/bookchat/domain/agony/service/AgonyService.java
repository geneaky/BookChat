package toy.bookchat.bookchat.domain.agony.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.db_module.agony.AgonyEntity;
import toy.bookchat.bookchat.db_module.agony.repository.AgonyRepository;
import toy.bookchat.bookchat.db_module.agonyrecord.repository.AgonyRecordRepository;
import toy.bookchat.bookchat.db_module.bookshelf.BookShelfEntity;
import toy.bookchat.bookchat.db_module.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.domain.agony.api.v1.request.CreateBookAgonyRequest;
import toy.bookchat.bookchat.domain.agony.api.v1.request.ReviseAgonyRequest;
import toy.bookchat.bookchat.domain.agony.api.v1.response.AgonyResponse;
import toy.bookchat.bookchat.domain.agony.api.v1.response.SliceOfAgoniesResponse;
import toy.bookchat.bookchat.exception.notfound.agony.AgonyNotFoundException;
import toy.bookchat.bookchat.exception.notfound.book.BookNotFoundException;

@Service
public class AgonyService {

    private final AgonyAppender agonyAppender;
    private final BookShelfRepository bookShelfRepository;
    private final AgonyRepository agonyRepository;
    private final AgonyRecordRepository agonyRecordRepository;

    public AgonyService(AgonyAppender agonyAppender, BookShelfRepository bookShelfRepository, AgonyRepository agonyRepository, AgonyRecordRepository agonyRecordRepository) {
        this.agonyAppender = agonyAppender;
        this.bookShelfRepository = bookShelfRepository;
        this.agonyRepository = agonyRepository;
        this.agonyRecordRepository = agonyRecordRepository;
    }

    @Transactional
    public Long storeBookShelfAgony(CreateBookAgonyRequest request, Long userId, Long bookShelfId) {
        BookShelfEntity bookShelfEntity = bookShelfRepository.findByIdAndUserId(bookShelfId, userId).orElseThrow(BookNotFoundException::new);
        AgonyEntity agonyEntity = request.getAgony(bookShelfEntity);
        agonyRepository.save(agonyEntity);
        return agonyEntity.getId();
    }

    @Transactional(readOnly = true)
    public SliceOfAgoniesResponse searchSliceOfAgonies(Long bookShelfId, Long userId, Pageable pageable, Long postCursorId) {
        return new SliceOfAgoniesResponse(agonyRepository.findUserBookShelfSliceOfAgonies(bookShelfId, userId, pageable, postCursorId));
    }

    @Transactional
    public void deleteAgony(Long bookShelfId, List<Long> agoniesIds, Long userId) {
        agonyRecordRepository.deleteByAgoniesIds(bookShelfId, userId, agoniesIds);
        agonyRepository.deleteByAgoniesIds(bookShelfId, userId, agoniesIds);
    }

    @Transactional
    public void reviseAgony(Long bookShelfId, Long agonyId, Long userId, ReviseAgonyRequest reviseAgonyRequest) {
        AgonyEntity agonyEntity = agonyRepository.findUserBookShelfAgony(bookShelfId, agonyId, userId)
            .orElseThrow(AgonyNotFoundException::new);
        agonyEntity.changeTitle(reviseAgonyRequest.getTitle());
        agonyEntity.changeHexColorCode(reviseAgonyRequest.getHexColorCode());
    }

    @Transactional
    public void deleteAllUserAgony(Long userId) {
        agonyRecordRepository.deleteAllByUserId(userId);
        agonyRepository.deleteAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public AgonyResponse searchAgony(Long bookShelfId, Long agonyId, Long userId) {
        AgonyEntity agonyEntity = agonyRepository.findUserBookShelfAgony(bookShelfId, agonyId, userId).orElseThrow(AgonyNotFoundException::new);
        return AgonyResponse.from(agonyEntity);
    }
}
