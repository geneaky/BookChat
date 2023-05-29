package toy.bookchat.bookchat.domain.scrap.repository.query;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import toy.bookchat.bookchat.domain.RepositoryTest;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.domain.scrap.Scrap;
import toy.bookchat.bookchat.domain.scrap.repository.ScrapRepository;
import toy.bookchat.bookchat.domain.scrap.service.dto.response.ScrapResponse;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;

@RepositoryTest
class ScrapQueryRepositoryTest {

    @Autowired
    private ScrapRepository scrapRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookShelfRepository bookShelfRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void 사용자_서재_스크랩_조회_성공() throws Exception {
        User user = User.builder().build();
        userRepository.save(user);

        Book book = Book.builder()
            .isbn("1231241")
            .publishAt(LocalDate.now())
            .build();
        bookRepository.save(book);

        BookShelf bookShelf = BookShelf.builder()
            .book(book)
            .user(user)
            .build();
        bookShelfRepository.save(bookShelf);

        Scrap scrap = Scrap.builder()
            .bookShelf(bookShelf)
            .scrapContent("content1")
            .build();
        scrapRepository.save(scrap);

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by("id").ascending());

        Slice<ScrapResponse> scrapResponseSlice = scrapRepository.findScraps(bookShelf.getId(),
            null,
            pageRequest, user.getId());

        assertThat(scrapResponseSlice.hasNext()).isFalse();
    }
}