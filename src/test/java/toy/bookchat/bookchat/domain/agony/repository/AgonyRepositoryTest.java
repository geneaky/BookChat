package toy.bookchat.bookchat.domain.agony.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
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

    @Test
    void 고민_등록_성공() throws Exception {

        Book book = new Book("1-4133-0454-0", "effective java", List.of("Joshua"), "insight",
            "bookCover@naver.com");
        bookRepository.save(book);

        User user = User.builder()
            .build();
        userRepository.save(user);

        BookShelf bookShelf = BookShelf.builder()
            .user(user)
            .book(book)
            .agonies(new ArrayList<>())
            .build();
        bookShelfRepository.save(bookShelf);

        bookRepository.flush();
        userRepository.flush();
        bookShelfRepository.flush();

        Agony agony = new Agony(null, "title", "blue", bookShelf);
        agonyRepository.saveAndFlush(agony);
        Agony findAgony = agonyRepository.findById(agony.getId()).get();

        assertThat(findAgony).isEqualTo(agony);
    }

    @Test
    void 사용자의_책꽂이에_등록한_고민_조회_성공() throws Exception {
        Book book = new Book("1-4133-0454-0", "effective java", List.of("Joshua"), "insight",
            "bookCover@naver.com");
        bookRepository.save(book);

        User user = User.builder()
            .build();
        userRepository.save(user);

        BookShelf bookShelf = BookShelf.builder()
            .user(user)
            .book(book)
            .agonies(new ArrayList<>())
            .build();
        bookShelfRepository.save(bookShelf);

        bookRepository.flush();
        userRepository.flush();
        bookShelfRepository.flush();

        Agony agony = new Agony(null, "title", "blue", bookShelf);
        agonyRepository.saveAndFlush(agony);
        Agony findAgony = agonyRepository.findUserBookShelfAgony(user.getId(), book.getId(),
            agony.getId()).get();

        assertThat(findAgony).isEqualTo(agony);
    }

    @Test
    void 사용자_책꽂이에_등록한_고민들_paging성공() throws Exception {
        Book book = new Book("1-4133-0454-0", "effective java", List.of("Joshua"), "insight",
            "bookCover@naver.com");
        bookRepository.save(book);

        User user = User.builder()
            .build();
        userRepository.save(user);

        BookShelf bookShelf = BookShelf.builder()
            .user(user)
            .book(book)
            .agonies(new ArrayList<>())
            .build();
        bookShelfRepository.save(bookShelf);

        bookRepository.flush();
        userRepository.flush();
        bookShelfRepository.flush();

        Agony agony1 = new Agony(null, "title1", "blue", bookShelf);
        Agony agony2 = new Agony(null, "title2", "red", bookShelf);
        Agony agony3 = new Agony(null, "title3", "yellow", bookShelf);
        List<Agony> agonyList = List.of(agony1, agony2, agony3);
        agonyRepository.saveAllAndFlush(agonyList);

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").descending());
        Page<Agony> pageOfAgonies = agonyRepository.findUserBookShelfPageOfAgonies(
            book.getId(), user.getId(), pageRequest);

        List<Agony> content = pageOfAgonies.getContent();
        assertThat(content).containsExactly(agony3, agony2);
    }
}