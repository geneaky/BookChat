package toy.bookchat.bookchat.domain.agony.service;

import org.springframework.stereotype.Service;
import toy.bookchat.bookchat.domain.agony.repository.AgonyRepository;
import toy.bookchat.bookchat.domain.agony.service.dto.CreateBookAgonyRequestDto;
import toy.bookchat.bookchat.domain.book.exception.BookNotFoundException;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.domain.user.User;

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

    public void storeBookAgony(CreateBookAgonyRequestDto createBookAgonyRequestDto, User user,
        Long bookId) {

        BookShelf bookShelf = bookShelfRepository.findByUserIdAndBookId(user.getId(), bookId)
            .orElseThrow(() -> {
                throw new BookNotFoundException("Not Registered Book");
            });

        agonyRepository.save(createBookAgonyRequestDto.getAgony(bookShelf));
    }
}
