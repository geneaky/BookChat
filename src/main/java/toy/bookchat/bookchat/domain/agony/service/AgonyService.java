package toy.bookchat.bookchat.domain.agony.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.agony.repository.AgonyRepository;
import toy.bookchat.bookchat.domain.agony.service.dto.request.CreateBookAgonyRequest;
import toy.bookchat.bookchat.domain.agony.service.dto.request.ReviseAgonyRequest;
import toy.bookchat.bookchat.domain.agony.service.dto.response.SliceOfAgoniesResponse;
import toy.bookchat.bookchat.domain.agonyrecord.repository.AgonyRecordRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.exception.agony.AgonyNotFoundException;
import toy.bookchat.bookchat.exception.book.BookNotFoundException;

@Service
public class AgonyService {

    private final BookShelfRepository bookShelfRepository;
    private final AgonyRepository agonyRepository;
    private final AgonyRecordRepository agonyRecordRepository;

    public AgonyService(
        BookShelfRepository bookShelfRepository,
        AgonyRepository agonyRepository,
        AgonyRecordRepository agonyRecordRepository) {
        this.bookShelfRepository = bookShelfRepository;
        this.agonyRepository = agonyRepository;
        this.agonyRecordRepository = agonyRecordRepository;
    }

    @Transactional
    public void storeBookAgony(CreateBookAgonyRequest createBookAgonyRequest, Long userId,
        Long bookId) {

        BookShelf bookShelf = bookShelfRepository.findByUserIdAndBookId(userId, bookId)
            .orElseThrow(BookNotFoundException::new);

        agonyRepository.save(createBookAgonyRequest.getAgony(bookShelf));
    }

    @Transactional(readOnly = true)
    public SliceOfAgoniesResponse searchSliceOfAgonies(Long userId,
        Pageable pageable, Optional<Long> postAgonyCursorId) {
        return new SliceOfAgoniesResponse(
            agonyRepository.findUserBookShelfSliceOfAgonies(userId, pageable,
                postAgonyCursorId));
    }

    @Transactional
    public void deleteAgony(List<Long> agoniesIds, Long userId) {
        agonyRecordRepository.deleteByAgoniesIds(userId, agoniesIds);
        agonyRepository.deleteByAgoniesIds(userId, agoniesIds);
    }

    @Transactional
    public void reviseAgony(Long agonyId, Long userId,
        ReviseAgonyRequest reviseAgonyRequest) {
        Agony agony = agonyRepository.findUserBookShelfAgony(userId, agonyId)
            .orElseThrow(AgonyNotFoundException::new);
        agony.changeTitle(reviseAgonyRequest.getAgonyTitle());
        agony.changeHexColorCode(reviseAgonyRequest.getAgonyColor());
    }
}
