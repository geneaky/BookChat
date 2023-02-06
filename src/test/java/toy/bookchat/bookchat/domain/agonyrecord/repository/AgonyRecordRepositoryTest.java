package toy.bookchat.bookchat.domain.agonyrecord.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import toy.bookchat.bookchat.domain.RepositoryTest;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.agony.repository.AgonyRepository;
import toy.bookchat.bookchat.domain.agonyrecord.AgonyRecord;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.exception.NotSupportedPagingConditionException;

@RepositoryTest
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
    @PersistenceContext
    private EntityManager em;

    private Book getBook() {
        return Book.builder()
            .isbn("1-4133-0454-0")
            .title("effective java")
            .authors(List.of("Joshua"))
            .publisher("insight")
            .bookCoverImageUrl("bookCover@naver.com")
            .build();
    }

    private User getUser() {
        return User.builder()
            .build();
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

    private AgonyRecord getAgonyRecord(Agony agony) {
        return AgonyRecord.builder()
            .title("recordTitle")
            .content("recordContent")
            .agony(agony)
            .build();
    }

    @Test
    void 고민기록_등록_성공() throws Exception {
        Book book = getBook();
        bookRepository.save(book);

        User user = getUser();
        userRepository.save(user);

        BookShelf bookShelf = getBookShelf(user, book);
        bookShelfRepository.save(bookShelf);

        Agony agony = getAgony(bookShelf);
        agonyRepository.save(agony);

        AgonyRecord agonyRecord = getAgonyRecord(agony);
        agonyRecordRepository.save(agonyRecord);

        AgonyRecord findAgonyRecord = agonyRecordRepository.findById(agonyRecord.getId()).get();
        assertThat(agonyRecord).isEqualTo(findAgonyRecord);
    }

    @Test
    void 고민_페이징_조회_성공() throws Exception {
        Book book = getBook();
        bookRepository.save(book);

        User user = getUser();
        userRepository.save(user);

        BookShelf bookShelf = getBookShelf(user, book);
        bookShelfRepository.save(bookShelf);

        Agony agony = getAgony(bookShelf);
        agonyRepository.save(agony);

        AgonyRecord agonyRecord = getAgonyRecord(agony);
        agonyRecordRepository.save(agonyRecord);

        Pageable pageable = PageRequest.of(0, 1, Sort.by("id").descending());
        List<AgonyRecord> content = agonyRecordRepository.findSliceOfUserAgonyRecords(
            bookShelf.getId(),
            agony.getId(),
            user.getId(), pageable, Optional.empty()).getContent();
        assertThat(content).containsExactly(agonyRecord);
    }

    @Test
    void 고민기록_DESC_커서ID로_조회_성공() throws Exception {
        Book book = getBook();
        bookRepository.save(book);

        User user = getUser();
        userRepository.save(user);

        BookShelf bookShelf = getBookShelf(user, book);
        bookShelfRepository.save(bookShelf);

        Agony agony = getAgony(bookShelf);
        agonyRepository.save(agony);

        AgonyRecord agonyRecord1 = getAgonyRecord(agony);
        AgonyRecord agonyRecord2 = getAgonyRecord(agony);
        AgonyRecord agonyRecord3 = getAgonyRecord(agony);
        agonyRecordRepository.save(agonyRecord1);
        agonyRecordRepository.save(agonyRecord2);
        agonyRecordRepository.save(agonyRecord3);

        Pageable pageable = PageRequest.of(0, 1, Sort.by("id").descending());
        List<AgonyRecord> content = agonyRecordRepository.findSliceOfUserAgonyRecords(
                bookShelf.getId(),
                agony.getId(),
                user.getId(), pageable, Optional.of(agonyRecord3.getId()))
            .getContent();
        assertThat(content).containsExactly(agonyRecord2);
    }

    @Test
    void 고민기록_ASC_커서ID로_조회_성공() throws Exception {
        Book book = getBook();
        bookRepository.save(book);

        User user = getUser();
        userRepository.save(user);

        BookShelf bookShelf = getBookShelf(user, book);
        bookShelfRepository.save(bookShelf);

        Agony agony = getAgony(bookShelf);
        agonyRepository.save(agony);

        AgonyRecord agonyRecord1 = getAgonyRecord(agony);
        AgonyRecord agonyRecord2 = getAgonyRecord(agony);
        AgonyRecord agonyRecord3 = getAgonyRecord(agony);
        agonyRecordRepository.save(agonyRecord1);
        agonyRecordRepository.save(agonyRecord2);
        agonyRecordRepository.save(agonyRecord3);

        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").ascending());
        List<AgonyRecord> content = agonyRecordRepository.findSliceOfUserAgonyRecords(
                bookShelf.getId(),
                agony.getId(),
                user.getId(), pageable, Optional.of(agonyRecord1.getId()))
            .getContent();
        assertThat(content).containsExactly(agonyRecord2, agonyRecord3);
    }

    @Test
    void 고민기록_지원하지않는_커서ID로_조회시_예외발생() throws Exception {
        Book book = getBook();
        bookRepository.save(book);

        User user = getUser();
        userRepository.save(user);

        BookShelf bookShelf = getBookShelf(user, book);
        bookShelfRepository.save(bookShelf);

        Agony agony = getAgony(bookShelf);
        agonyRepository.save(agony);

        AgonyRecord agonyRecord1 = getAgonyRecord(agony);
        AgonyRecord agonyRecord2 = getAgonyRecord(agony);
        AgonyRecord agonyRecord3 = getAgonyRecord(agony);
        agonyRecordRepository.save(agonyRecord1);
        agonyRecordRepository.save(agonyRecord2);
        agonyRecordRepository.save(agonyRecord3);

        Pageable pageable = PageRequest.of(0, 2, Sort.by("title").ascending());
        assertThatThrownBy(() -> {
            agonyRecordRepository.findSliceOfUserAgonyRecords(bookShelf.getId(), agony.getId(),
                user.getId(), pageable,
                Optional.of(agonyRecord1.getId()));
        }).isInstanceOf(NotSupportedPagingConditionException.class);
    }

    @Test
    void 사용자_고민기록_삭제_성공() throws Exception {
        Book book = getBook();
        bookRepository.save(book);

        User user = getUser();
        userRepository.save(user);

        BookShelf bookShelf = getBookShelf(user, book);
        bookShelfRepository.save(bookShelf);

        Agony agony = getAgony(bookShelf);
        agonyRepository.save(agony);

        AgonyRecord agonyRecord = getAgonyRecord(agony);
        agonyRecordRepository.save(agonyRecord);

        agonyRecordRepository.deleteAgonyRecord(bookShelf.getId(), agony.getId(),
            agonyRecord.getId(), user.getId());

        List<AgonyRecord> all = agonyRecordRepository.findAll();
        assertThat(all).isEmpty();
    }

    @Test
    void 고민기록_수정_성공() throws Exception {
        Book book = getBook();
        bookRepository.save(book);

        User user = getUser();
        userRepository.save(user);

        BookShelf bookShelf = getBookShelf(user, book);
        bookShelfRepository.save(bookShelf);

        Agony agony = getAgony(bookShelf);
        agonyRepository.save(agony);

        AgonyRecord agonyRecord = getAgonyRecord(agony);
        agonyRecordRepository.save(agonyRecord);

        agonyRecordRepository.reviseAgonyRecord(bookShelf.getId(), agony.getId(),
            agonyRecord.getId(), user.getId(), "수정 제목", "수정 내용");

        em.flush();
        em.clear();
        String result = agonyRecordRepository.findById(agonyRecord.getId()).get().getTitle();
        assertThat(result).isEqualTo("수정 제목");
    }

    @Test
    void 고민_기록_여러개_삭제_성공() throws Exception {
        Book book = getBook();
        bookRepository.save(book);

        User user = getUser();
        userRepository.save(user);

        BookShelf bookShelf = getBookShelf(user, book);
        bookShelfRepository.save(bookShelf);

        Agony agony = getAgony(bookShelf);
        agonyRepository.save(agony);

        AgonyRecord agonyRecord1 = getAgonyRecord(agony);
        AgonyRecord agonyRecord2 = getAgonyRecord(agony);
        AgonyRecord agonyRecord3 = getAgonyRecord(agony);
        List<AgonyRecord> agonyRecords = List.of(agonyRecord1, agonyRecord2, agonyRecord3);
        agonyRecordRepository.saveAll(agonyRecords);

        agonyRecordRepository.deleteByAgoniesIds(bookShelf.getId(), user.getId(),
            List.of(agony.getId()));

        List<AgonyRecord> result = agonyRecordRepository.findAll();
        assertThat(result).isEmpty();
    }

    @Test
    void 사용자가_생성한_고민기록_전부_삭제성공() throws Exception {
        Book book = getBook();
        bookRepository.save(book);

        User user = getUser();
        userRepository.save(user);

        BookShelf bookShelf = getBookShelf(user, book);
        bookShelfRepository.save(bookShelf);

        Agony agony = getAgony(bookShelf);
        agonyRepository.save(agony);

        AgonyRecord agonyRecord1 = getAgonyRecord(agony);
        AgonyRecord agonyRecord2 = getAgonyRecord(agony);
        AgonyRecord agonyRecord3 = getAgonyRecord(agony);
        List<AgonyRecord> agonyRecords = List.of(agonyRecord1, agonyRecord2, agonyRecord3);
        agonyRecordRepository.saveAll(agonyRecords);

        agonyRecordRepository.deleteAllByUserId(user.getId());
        List<AgonyRecord> result = agonyRecordRepository.findAll();
        assertThat(result).isEmpty();
    }
}