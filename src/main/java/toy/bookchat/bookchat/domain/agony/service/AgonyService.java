package toy.bookchat.bookchat.domain.agony.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.agony.repository.AgonyRepository;
import toy.bookchat.bookchat.domain.agony.service.dto.request.CreateBookAgonyRequestDto;
import toy.bookchat.bookchat.domain.agony.service.dto.response.PageOfAgoniesResponse;
import toy.bookchat.bookchat.domain.book.exception.BookNotFoundException;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;

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
    public void storeBookAgony(CreateBookAgonyRequestDto createBookAgonyRequestDto, Long userId,
        Long bookId) {

        BookShelf bookShelf = bookShelfRepository.findByUserIdAndBookId(userId, bookId)
            .orElseThrow(() -> {
                throw new BookNotFoundException("Not Registered Book");
            });

        agonyRepository.save(createBookAgonyRequestDto.getAgony(bookShelf));
    }

    @Transactional(readOnly = true)
    public PageOfAgoniesResponse searchPageOfAgonies(Long bookId, Long userId, Pageable pageable) {
        return new PageOfAgoniesResponse(
            agonyRepository.findUserBookShelfPageOfAgonies(bookId, userId, pageable));
    }
}
