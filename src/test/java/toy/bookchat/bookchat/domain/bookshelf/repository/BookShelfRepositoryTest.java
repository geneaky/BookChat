package toy.bookchat.bookchat.domain.bookshelf.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import toy.bookchat.bookchat.domain.RepositoryTest;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.Star;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.exception.book.BookNotFoundException;

@RepositoryTest
class BookShelfRepositoryTest {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookShelfRepository bookShelfRepository;

    private Book getBook(String isbn) {
        return Book.builder()
            .isbn(isbn)
            .title("effective java")
            .authors(List.of("Joshua"))
            .publisher("insight")
            .bookCoverImageUrl("bookCover@naver.com")
            .build();
    }

    @Test
    void 책_저장() throws Exception {
        Book book = getBook("1-4133-0454-0");

        Book savedBook = bookRepository.save(book);
        assertThat(book).isEqualTo(savedBook);
    }

    @Test
    void 책장에_책을_저장() throws Exception {

        Book book = getBook("1-4133-0454-0");
        bookRepository.save(book);

        User user = User.builder().build();
        userRepository.save(user);

        BookShelf bookShelf = BookShelf.builder()
            .book(book)
            .user(user)
            .build();
        bookShelfRepository.save(bookShelf);

        BookShelf findBookShelf = bookShelfRepository.findById(bookShelf.getId()).get();
        Book findBook = findBookShelf.getBook();
        assertThat(book).isEqualTo(findBook);
    }

    @Test
    void 읽고있는_책을_조회() throws Exception {
        Book book1 = getBook("1-4133-0454-0");
        Book book2 = getBook("1-4133-0454-1");

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

        bookShelfRepository.save(bookShelf1);
        bookShelfRepository.save(bookShelf2);

        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").descending());
        Page<BookShelf> pagingBookShelves = bookShelfRepository.findSpecificStatusBookByUserId(
            ReadingStatus.READING, pageable, user.getId());
        List<BookShelf> bookShelves = pagingBookShelves.getContent();
        int result = bookShelves.size();
        assertThat(result).isEqualTo(2);
    }

    @Test
    void 읽은_책을_조회() throws Exception {
        Book book1 = getBook("1-4133-0454-0");
        Book book2 = getBook("1-4133-0454-1");
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

        bookShelfRepository.save(bookShelf1);
        bookShelfRepository.save(bookShelf2);

        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").descending());
        Page<BookShelf> pagingBookShelves = bookShelfRepository.findSpecificStatusBookByUserId(
            ReadingStatus.COMPLETE, pageable, user.getId());
        List<BookShelf> bookShelves = pagingBookShelves.getContent();
        int result = bookShelves.size();
        assertThat(result).isEqualTo(2);
    }

    @Test
    void 읽을_책을_조회() throws Exception {
        Book book1 = getBook("1-4133-0454-0");
        Book book2 = getBook("1-4133-0454-1");

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

        bookShelfRepository.save(bookShelf1);
        bookShelfRepository.save(bookShelf2);

        user.updateImageUrl("hi");
        bookShelf1.getUser().updateImageUrl("by");

        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").descending());
        Page<BookShelf> pagingBookShelves = bookShelfRepository.findSpecificStatusBookByUserId(
            ReadingStatus.WISH, pageable, user.getId());

        List<BookShelf> bookShelves = pagingBookShelves.getContent();
        int result = bookShelves.size();
        assertThat(result).isEqualTo(2);
    }

    @Test
    void 읽고있는_책_book_id로_조회성공() throws Exception {
        Book book = getBook("1-4133-0454-0");

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

        BookShelf readingBook = bookShelfRepository.findOneOnConditionByUserIdAndBookId(
            user.getId(), book.getId(), ReadingStatus.READING);

        assertThat(readingBook).isNotNull();
    }

    @Test
    void 읽고있는_책_book_id로_조회시_없으면_예외발생() throws Exception {
        assertThatThrownBy(() -> {
            bookShelfRepository.findOneOnConditionByUserIdAndBookId(1L, 1L, ReadingStatus.READING);
        }).isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void 책장에있는_책_book_id로_삭제_성공() throws Exception {
        Book book = getBook("1-4133-0454-0");

        bookRepository.save(book);

        User user = User.builder().name("hi").build();
        userRepository.save(user);

        BookShelf bookShelf = BookShelf.builder()
            .book(book)
            .user(user)
            .readingStatus(ReadingStatus.READING)
            .build();

        bookShelfRepository.save(bookShelf);

        bookShelfRepository.deleteBookByUserIdAndBookId(user.getId(), book.getId());

        int result = bookShelfRepository.findAll().size();
        assertThat(result).isZero();
    }

    @Test
    void user_id_book_id로_서재_조회성공() throws Exception {
        Book book = getBook("1-4133-0454-0");

        bookRepository.save(book);

        User user = User.builder().name("hi").build();
        userRepository.save(user);

        BookShelf bookShelf = BookShelf.builder()
            .book(book)
            .user(user)
            .readingStatus(ReadingStatus.READING)
            .build();

        bookShelfRepository.save(bookShelf);

        BookShelf findBookShelf = bookShelfRepository.findByUserIdAndBookId(
            user.getId(), book.getId()).get();
        assertThat(findBookShelf).isEqualTo(bookShelf);
    }

    @Test
    void 사용자가_생성한_책장_전부_삭제성공() throws Exception {
        Book book1 = getBook("1-4133-0454-0");
        Book book2 = getBook("1-4133-0454-1");
        bookRepository.save(book1);
        bookRepository.save(book2);

        User user = User.builder().name("hi").build();
        userRepository.save(user);

        BookShelf bookShelf1 = BookShelf.builder()
            .book(book1)
            .user(user)
            .readingStatus(ReadingStatus.READING)
            .build();

        BookShelf bookShelf2 = BookShelf.builder()
            .book(book2)
            .user(user)
            .readingStatus(ReadingStatus.READING)
            .build();

        bookShelfRepository.save(bookShelf1);
        bookShelfRepository.save(bookShelf2);
        bookShelfRepository.deleteAllByUserId(user.getId());

        List<BookShelf> result = bookShelfRepository.findAll();
        assertThat(result).isEmpty();
    }

    @Test
    void isbn으로_사용자_서재에_등록된_책_조회성공() throws Exception {
        Book book1 = getBook("1-4133-0454-0");
        bookRepository.save(book1);

        User user = User.builder().name("hi").build();
        userRepository.save(user);

        BookShelf bookShelf1 = BookShelf.builder()
            .book(book1)
            .user(user)
            .readingStatus(ReadingStatus.READING)
            .build();

        bookShelfRepository.save(bookShelf1);

        BookShelf findBookShelf = bookShelfRepository.findByUserIdAndIsbn(user.getId(),
            book1.getIsbn()).get();
        
        assertThat(findBookShelf).isEqualTo(bookShelf1);
    }
}
