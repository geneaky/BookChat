package toy.bookchat.bookchat.domain.agony.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import toy.bookchat.bookchat.config.JpaAuditingConfig;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.agony.AgonyRecord;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.domain.configuration.TestConfig;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;

@DataJpaTest
@Import({JpaAuditingConfig.class, TestConfig.class})
class AgonyRecordRepositoryTest {

    @Autowired
    AgonyRecordRepository agonyRecordRepository;
    @Autowired
    AgonyRepository agonyRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookShelfRepository bookShelfRepository;

    @Test
    void 고민기록_등록_성공() throws Exception {
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

        Agony agony = new Agony(null, "title", "blue", bookShelf);
        agonyRepository.save(agony);

        bookRepository.flush();
        userRepository.flush();
        bookShelfRepository.flush();
        agonyRepository.flush();

        AgonyRecord agonyRecord = new AgonyRecord("recordTitle", "recordContent", agony);

        agonyRecordRepository.save(agonyRecord);

        agonyRecordRepository.flush();

        AgonyRecord findAgonyRecord = agonyRecordRepository.findById(agonyRecord.getId()).get();

        assertThat(agonyRecord).isEqualTo(findAgonyRecord);
    }

    @Test
    void 고민_페이징_조회_성공() throws Exception {
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

        Agony agony = new Agony(null, "title", "blue", bookShelf);
        agonyRepository.save(agony);

        bookRepository.flush();
        userRepository.flush();
        bookShelfRepository.flush();
        agonyRepository.flush();

        AgonyRecord agonyRecord = new AgonyRecord("recordTitle", "recordContent", agony);

        agonyRecordRepository.save(agonyRecord);

        agonyRecordRepository.flush();

        Pageable pageable = PageRequest.of(0, 1, Sort.by("id").descending());
        List<AgonyRecord> content = agonyRecordRepository.findPageOfUserAgonyRecords(
            book.getId(), agony.getId(), user.getId(), pageable).getContent();

        assertThat(content).containsExactly(agonyRecord);
    }
}