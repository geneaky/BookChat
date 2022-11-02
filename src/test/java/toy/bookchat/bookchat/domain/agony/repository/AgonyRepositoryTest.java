package toy.bookchat.bookchat.domain.agony.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import toy.bookchat.bookchat.config.JpaAuditingConfig;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.domain.configuration.TestConfig;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;

@DataJpaTest
@Import({JpaAuditingConfig.class, TestConfig.class})
class AgonyRepositoryTest {

    @Autowired
    AgonyRepository agonyRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookShelfRepository bookShelfRepository;

    private Book getBook() {
        return Book.builder()
            .isbn("1-4133-0454-0")
            .title("effective java")
            .publisher("insight")
            .bookCoverImageUrl("bookCover@naver.com")
            .authors(List.of("Joshua"))
            .build();
    }

    private User getUser() {
        return User.builder().build();
    }

    private BookShelf getBookShelf(User user, Book book) {
        return BookShelf.builder()
            .user(user)
            .book(book)
            .build();
    }

    private Agony getAgony(BookShelf bookShelf) {
        return Agony.builder()
            .title("title")
            .hexColorCode("blue")
            .bookShelf(bookShelf)
            .build();
    }

    @Test
    void 고민_등록_성공() throws Exception {

        Book book = getBook();
        bookRepository.save(book);

        User user = getUser();
        userRepository.save(user);

        BookShelf bookShelf = getBookShelf(user, book);
        bookShelfRepository.save(bookShelf);

        Agony agony = getAgony(bookShelf);
        agonyRepository.save(agony);

        bookRepository.flush();
        userRepository.flush();
        bookShelfRepository.flush();
        agonyRepository.flush();

        Agony findAgony = agonyRepository.findById(agony.getId()).get();

        assertThat(findAgony).isEqualTo(agony);
    }

    @Test
    void 사용자의_책꽂이에_등록한_고민_조회_성공() throws Exception {
        Book book = getBook();
        bookRepository.save(book);

        User user = getUser();
        userRepository.save(user);

        BookShelf bookShelf = getBookShelf(user, book);
        bookShelfRepository.save(bookShelf);

        Agony agony = getAgony(bookShelf);
        agonyRepository.save(agony);

        bookRepository.flush();
        userRepository.flush();
        bookShelfRepository.flush();
        agonyRepository.flush();

        Agony findAgony = agonyRepository.findUserBookShelfAgony(user.getId(), book.getId(),
            agony.getId()).get();

        assertThat(findAgony).isEqualTo(agony);
    }

    @Test
    void 사용자_책꽂이에_등록한_고민들_paging성공() throws Exception {
        Book book = getBook();
        bookRepository.save(book);

        User user = getUser();
        userRepository.save(user);

        BookShelf bookShelf = getBookShelf(user, book);
        bookShelfRepository.save(bookShelf);

        Agony agony1 = getAgony(bookShelf);
        Agony agony2 = Agony.builder()
            .title("title2")
            .hexColorCode("red")
            .bookShelf(bookShelf)
            .build();
        Agony agony3 = Agony.builder()
            .title("title3")
            .hexColorCode("pupple")
            .bookShelf(bookShelf)
            .build();
        ;
        List<Agony> agonyList = List.of(agony1, agony2, agony3);
        agonyRepository.saveAll(agonyList);

        bookRepository.flush();
        userRepository.flush();
        bookShelfRepository.flush();
        agonyRepository.flush();

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").descending());
        Page<Agony> pageOfAgonies = agonyRepository.findUserBookShelfPageOfAgonies(
            book.getId(), user.getId(), pageRequest);

        List<Agony> content = pageOfAgonies.getContent();
        assertThat(content).containsExactly(agony3, agony2);
    }
}