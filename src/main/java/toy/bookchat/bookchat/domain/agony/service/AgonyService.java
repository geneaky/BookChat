package toy.bookchat.bookchat.domain.agony.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.agony.repository.AgonyRepository;
import toy.bookchat.bookchat.domain.agony.service.dto.request.CreateBookAgonyRequest;
import toy.bookchat.bookchat.domain.agony.service.dto.response.BasePageOfAgoniesResponse;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.exception.book.BookNotFoundException;

@Service
public class AgonyService {

    private final BookShelfRepository bookShelfRepository;
    private final AgonyRepository agonyRepository;

    public AgonyService(
        BookShelfRepository bookShelfRepository,
        AgonyRepository agonyRepository) {
        this.bookShelfRepository = bookShelfRepository;
        this.agonyRepository = agonyRepository;
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

    public void deleteAgony(Long bookId, Long agonyId, Long userId) {
        
    }
}
