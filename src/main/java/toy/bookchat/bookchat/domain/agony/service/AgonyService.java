package toy.bookchat.bookchat.domain.agony.service;

import java.util.function.Supplier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.agony.repository.AgonyRecordRepository;
import toy.bookchat.bookchat.domain.agony.repository.AgonyRepository;
import toy.bookchat.bookchat.domain.agony.service.dto.request.CreateBookAgonyRequest;
import toy.bookchat.bookchat.domain.agony.service.dto.request.ReviseAgonyRequest;
import toy.bookchat.bookchat.domain.agony.service.dto.response.BasePageOfAgoniesResponse;
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
            .orElseThrow(() -> {
                throw new BookNotFoundException("Not Registered Book");
            });

        agonyRepository.save(createBookAgonyRequest.getAgony(bookShelf));
    }

    @Transactional(readOnly = true)
    public BasePageOfAgoniesResponse searchPageOfAgonies(Long bookId, Long userId,
        Pageable pageable) {
        return new BasePageOfAgoniesResponse(
            agonyRepository.findUserBookShelfPageOfAgonies(bookId, userId, pageable));
    }

    @Transactional
    public void deleteAgony(Long bookId, Long agonyId, Long userId) {
        Agony agony = agonyRepository.findUserBookShelfAgony(userId, bookId, agonyId)
            .orElseThrow(AgonyNotFound());
        agonyRecordRepository.deleteByAgony(agony);
        agonyRepository.delete(agony);
    }

    @Transactional
    public void reviseAgony(Long bookId, Long agonyId, Long userId,
        ReviseAgonyRequest reviseAgonyRequest) {
        Agony agony = agonyRepository.findUserBookShelfAgony(userId, bookId, agonyId)
            .orElseThrow(AgonyNotFound());
        agony.changeTitle(reviseAgonyRequest.getAgonyTitle());
        agony.changeHexColorCode(reviseAgonyRequest.getAgonyColor());
    }

    private Supplier<RuntimeException> AgonyNotFound() {
        return () -> {
            throw new AgonyNotFoundException("Not Registered Agony");
        };
    }
}
