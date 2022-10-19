package toy.bookchat.bookchat.domain.bookshelf.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import toy.bookchat.bookchat.config.JpaAuditingConfig;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.exception.BookNotFoundException;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.Star;
import toy.bookchat.bookchat.domain.configuration.TestConfig;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;

@DataJpaTest
@AutoConfigureJson
@Import({JpaAuditingConfig.class, TestConfig.class})
class BookShelfRepositoryTest {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookShelfRepository bookShelfRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 책_저장() throws Exception {
        Book book = new Book("1234", "effective java", List.of("Joshua"), "insight",
            "bookCover@naver.com");

        Book savedBook = bookRepository.save(book);
        assertThat(book).isEqualTo(savedBook);
    }

    @Test
    void 책장에_책을_저장() throws Exception {

        BookShelf bookShelf = BookShelf.builder().build();

        Book book = new Book("1-4133-0454-0", "effective java", List.of("Joshua"), "insight",
            "bookCover@naver.com");

        book.setBookShelf(bookShelf);
        Book savedBook = bookRepository.save(book);

        User user = User.builder()
            .build();
        user.setBookShelf(bookShelf);
        User savedUser = userRepository.save(user);

        BookShelf savedBookShelf = bookShelfRepository.save(bookShelf);

        System.out.println(savedBookShelf.getBook());

        assertThat(book).isEqualTo(savedBook);
        assertThat(user).isEqualTo(savedUser);
        assertThat(bookShelf).isEqualTo(savedBookShelf);
    }

    @Test
    void 읽고있는_책을_조회() throws Exception {
        Book book1 = new Book("1234", "effective java", List.of("Joshua"), "insight",
            "bookCover@naver.com");
        Book book2 = new Book("12345", "effective java2", List.of("Joshua"), "insight",
            "bookCove2r@naver.com");
        bookRepository.save(book1);
        bookRepository.save(book2);

        User user = User.builder().build();
        userRepository.save(user);

        BookShelf bookShelf1 = BookShelf.builder()
            .book(book1)
            .user(user)
            .readingStatus(ReadingStatus.READING)
            .star(Star.ZERO)
            .singleLineAssessment(null)
            .build();

        BookShelf bookShelf2 = BookShelf.builder()
            .book(book2)
            .user(user)
            .readingStatus(ReadingStatus.READING)
            .star(Star.ZERO)
            .singleLineAssessment(null)
            .build();

        book1.setBookShelf(bookShelf1);
        book2.setBookShelf(bookShelf2);
        user.setBookShelf(bookShelf1);
        user.setBookShelf(bookShelf2);

        bookShelfRepository.save(bookShelf1);
        bookShelfRepository.save(bookShelf2);

        Pageable pageable = PageRequest.of(0, 2, Sort.by(Direction.DESC, "id"));
        Page<BookShelf> pagingBookShelves = bookShelfRepository.findSpecificStatusBookByUserId(
            ReadingStatus.READING, pageable, user.getId());
        List<BookShelf> bookShelves = pagingBookShelves.getContent();
        int result = bookShelves.size();
        assertThat(result).isEqualTo(2);
    }

    @Test
    void 읽은_책을_조회() throws Exception {
        Book book1 = new Book("1234", "effective java", List.of("Joshua"), "insight",
            "bookCover@naver.com");
        Book book2 = new Book("12345", "effective java2", List.of("Joshua"), "insight",
            "bookCove2r@naver.com");
        bookRepository.save(book1);
        bookRepository.save(book2);

        User user = User.builder().build();
        userRepository.save(user);

        BookShelf bookShelf1 = BookShelf.builder()
            .book(book1)
            .user(user)
            .readingStatus(ReadingStatus.COMPLETE)
            .star(Star.THREE_HALF)
            .singleLineAssessment("재밌네요 허허")
            .build();

        BookShelf bookShelf2 = BookShelf.builder()
            .book(book2)
            .user(user)
            .readingStatus(ReadingStatus.COMPLETE)
            .star(Star.FIVE)
            .singleLineAssessment("이시대 최고의 도서")
            .build();

        book1.setBookShelf(bookShelf1);
        book2.setBookShelf(bookShelf2);
        user.setBookShelf(bookShelf1);
        user.setBookShelf(bookShelf2);

        bookShelfRepository.save(bookShelf1);
        bookShelfRepository.save(bookShelf2);

        Pageable pageable = PageRequest.of(0, 2, Sort.by(Direction.DESC, "id"));
        Page<BookShelf> pagingBookShelves = bookShelfRepository.findSpecificStatusBookByUserId(
            ReadingStatus.COMPLETE, pageable, user.getId());
        List<BookShelf> bookShelves = pagingBookShelves.getContent();
        int result = bookShelves.size();
        assertThat(result).isEqualTo(2);
    }

    @Test
    void 읽을_책을_조회() throws Exception {
        Book book1 = new Book("1234", "effective java", List.of("Joshua"), "insight",
            "bookCover@naver.com");
        Book book2 = new Book("12345", "effective java2", List.of("Joshua"), "insight",
            "bookCove2r@naver.com");
        bookRepository.save(book1);
        bookRepository.save(book2);

        User user = User.builder().name("hi").build();
        userRepository.save(user);

        BookShelf bookShelf1 = BookShelf.builder()
            .book(book1)
            .user(user)
            .readingStatus(ReadingStatus.WISH)
            .star(Star.ZERO)
            .singleLineAssessment(null)
            .build();

        BookShelf bookShelf2 = BookShelf.builder()
            .book(book2)
            .user(user)
            .readingStatus(ReadingStatus.WISH)
            .star(Star.ZERO)
            .singleLineAssessment(null)
            .build();

        book1.setBookShelf(bookShelf1);
        book2.setBookShelf(bookShelf2);
        user.setBookShelf(bookShelf1);
        user.setBookShelf(bookShelf2);

        bookShelfRepository.save(bookShelf1);
        bookShelfRepository.save(bookShelf2);

        userRepository.flush();
        bookRepository.flush();
        bookShelfRepository.flush();

        user.updateImageUrl("hi");
        bookShelf1.getUser().updateImageUrl("by");

        Pageable pageable = PageRequest.of(0, 2, Sort.by(Direction.DESC, "id"));
        Page<BookShelf> pagingBookShelves = bookShelfRepository.findSpecificStatusBookByUserId(
            ReadingStatus.WISH, pageable, user.getId());

        List<BookShelf> bookShelves = pagingBookShelves.getContent();
        int result = bookShelves.size();
        assertThat(result).isEqualTo(2);
    }

    @Test
    void 읽고있는_책_isbn으로_조회성공() throws Exception {
        Book book = new Book("1234", "effective java", List.of("Joshua"), "insight",
            "bookCover@naver.com");

        bookRepository.save(book);

        User user = User.builder().name("hi").build();
        userRepository.save(user);

        BookShelf bookShelf = BookShelf.builder()
            .book(book)
            .user(user)
            .readingStatus(ReadingStatus.READING)
            .star(null)
            .singleLineAssessment(null)
            .build();

        bookShelfRepository.save(bookShelf);

        userRepository.flush();
        bookRepository.flush();
        bookShelfRepository.flush();

        BookShelf readingBook = bookShelfRepository.findReadingBookByUserIdAndIsbn(
            user.getId(), "1234");

        assertThat(readingBook).isNotNull();
    }

    @Test
    void 읽고있는_책_isbn으로_조회시_없으면_예외발생() throws Exception {
        assertThatThrownBy(() -> {
            bookShelfRepository.findReadingBookByUserIdAndIsbn(1L, "1234");
        }).isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void 책장에있는_책_isbn으로_삭제_성공() throws Exception {
        Book book = new Book("1234", "effective java", List.of("Joshua"), "insight",
            "bookCover@naver.com");

        bookRepository.save(book);

        User user = User.builder().name("hi").build();
        userRepository.save(user);

        BookShelf bookShelf = BookShelf.builder()
            .book(book)
            .user(user)
            .readingStatus(ReadingStatus.READING)
            .build();

        bookShelfRepository.save(bookShelf);

        userRepository.flush();
        bookRepository.flush();
        bookShelfRepository.flush();

        bookShelfRepository.deleteBookByUserIdAndIsbn(user.getId(), book.getIsbn());

        bookShelfRepository.flush();

        int result = bookShelfRepository.findAll().size();
        assertThat(result).isZero();
    }
}
