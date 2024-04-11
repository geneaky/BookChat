package toy.bookchat.bookchat.domain.agony.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.agony.repository.AgonyRecordRepository;
import toy.bookchat.bookchat.domain.agony.repository.AgonyRepository;
import toy.bookchat.bookchat.domain.agony.service.dto.request.CreateBookAgonyRequest;
import toy.bookchat.bookchat.domain.agony.service.dto.request.ReviseAgonyRequest;
import toy.bookchat.bookchat.domain.agony.service.dto.response.AgonyResponse;
import toy.bookchat.bookchat.domain.agony.service.dto.response.SliceOfAgoniesResponse;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.exception.notfound.agony.AgonyNotFoundException;
import toy.bookchat.bookchat.exception.notfound.book.BookNotFoundException;

@Service
public class AgonyService {

    private final BookShelfRepository bookShelfRepository;
    private final AgonyRepository agonyRepository;
    private final AgonyRecordRepository agonyRecordRepository;

    public AgonyService(BookShelfRepository bookShelfRepository, AgonyRepository agonyRepository, AgonyRecordRepository agonyRecordRepository) {
        this.bookShelfRepository = bookShelfRepository;
        this.agonyRepository = agonyRepository;
        this.agonyRecordRepository = agonyRecordRepository;
    }

    @Transactional
    public Long storeBookShelfAgony(CreateBookAgonyRequest createBookAgonyRequest, Long userId, Long bookShelfId) {
        BookShelf bookShelf = bookShelfRepository.findByIdAndUserId(bookShelfId, userId).orElseThrow(BookNotFoundException::new);
        Agony agony = createBookAgonyRequest.getAgony(bookShelf);
        agonyRepository.save(agony);
        return agony.getId();
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
        Agony agony = agonyRepository.findUserBookShelfAgony(bookShelfId, agonyId, userId)
            .orElseThrow(AgonyNotFoundException::new);
        agony.changeTitle(reviseAgonyRequest.getTitle());
        agony.changeHexColorCode(reviseAgonyRequest.getHexColorCode());
    }

    @Transactional
    public void deleteAllUserAgony(Long userId) {
        agonyRecordRepository.deleteAllByUserId(userId);
        agonyRepository.deleteAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public AgonyResponse searchAgony(Long bookShelfId, Long agonyId, Long userId) {
        Agony agony = agonyRepository.findUserBookShelfAgony(bookShelfId, agonyId, userId).orElseThrow(AgonyNotFoundException::new);
        return AgonyResponse.from(agony);
    }
}
