package toy.bookchat.bookchat.domain.bookshelf.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import toy.bookchat.bookchat.domain.RepositoryTest;
import toy.bookchat.bookchat.domain.book.BookEntity;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookReportEntity;
import toy.bookchat.bookchat.domain.bookshelf.BookShelfEntity;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.Star;
import toy.bookchat.bookchat.domain.user.UserEntity;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;

class BookShelfEntityRepositoryTest extends RepositoryTest {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookReportRepository bookReportRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookShelfRepository bookShelfRepository;

    private BookEntity getBook(String isbn) {
        return BookEntity.builder()
            .isbn(isbn)
            .title("effective java")
            .authors(List.of("Joshua"))
            .publisher("insight")
            .publishAt(LocalDate.now())
            .bookCoverImageUrl("bookCover@naver.com")
            .build();
    }

    @Test
    void 책_저장() throws Exception {
        BookEntity bookEntity = getBook("1-4133-0454-0");

        BookEntity savedBookEntity = bookRepository.save(bookEntity);
        assertThat(bookEntity).isEqualTo(savedBookEntity);
    }

    @Test
    void 책장에_책을_저장() throws Exception {

        BookEntity bookEntity = getBook("1-4133-0454-0");
        bookRepository.save(bookEntity);

        UserEntity userEntity = UserEntity.builder().build();
        userRepository.save(userEntity);

        BookShelfEntity bookShelfEntity = BookShelfEntity.builder()
            .bookEntity(bookEntity)
            .userEntity(userEntity)
            .build();
        bookShelfRepository.save(bookShelfEntity);

        BookShelfEntity findBookShelfEntity = bookShelfRepository.findById(bookShelfEntity.getId()).get();
        BookEntity findBookEntity = findBookShelfEntity.getBookEntity();
        assertThat(bookEntity).isEqualTo(findBookEntity);
    }

    @Test
    void 읽고있는_책을_조회() throws Exception {
        BookEntity bookEntity1 = getBook("1-4133-0454-0");
        BookEntity bookEntity2 = getBook("1-4133-0454-1");

        bookRepository.save(bookEntity1);
        bookRepository.save(bookEntity2);

        UserEntity userEntity = UserEntity.builder().build();
        userRepository.save(userEntity);

        BookShelfEntity bookShelfEntity1 = BookShelfEntity.builder()
            .bookEntity(bookEntity1)
            .userEntity(userEntity)
            .readingStatus(ReadingStatus.READING)
            .star(Star.ZERO)
            .build();

        BookShelfEntity bookShelfEntity2 = BookShelfEntity.builder()
            .bookEntity(bookEntity2)
            .userEntity(userEntity)
            .readingStatus(ReadingStatus.READING)
            .star(Star.ZERO)
            .build();

        bookShelfRepository.save(bookShelfEntity1);
        bookShelfRepository.save(bookShelfEntity2);

        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").descending());
        Page<BookShelfEntity> pagingBookShelves = bookShelfRepository.findSpecificStatusBookByUserId(
            ReadingStatus.READING, pageable, userEntity.getId());
        List<BookShelfEntity> bookShelves = pagingBookShelves.getContent();
        int result = bookShelves.size();
        assertThat(result).isEqualTo(2);
    }

    @Test
    void 읽은_책을_조회() throws Exception {
        BookEntity bookEntity1 = getBook("1-4133-0454-0");
        BookEntity bookEntity2 = getBook("1-4133-0454-1");
        bookRepository.save(bookEntity1);
        bookRepository.save(bookEntity2);

        UserEntity userEntity = UserEntity.builder().build();
        userRepository.save(userEntity);

        BookShelfEntity bookShelfEntity1 = BookShelfEntity.builder()
            .bookEntity(bookEntity1)
            .userEntity(userEntity)
            .readingStatus(ReadingStatus.COMPLETE)
            .star(Star.THREE_HALF)
            .build();

        BookShelfEntity bookShelfEntity2 = BookShelfEntity.builder()
            .bookEntity(bookEntity2)
            .userEntity(userEntity)
            .readingStatus(ReadingStatus.COMPLETE)
            .star(Star.FIVE)
            .build();

        bookShelfRepository.save(bookShelfEntity1);
        bookShelfRepository.save(bookShelfEntity2);

        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").descending());
        Page<BookShelfEntity> pagingBookShelves = bookShelfRepository.findSpecificStatusBookByUserId(
            ReadingStatus.COMPLETE, pageable, userEntity.getId());
        List<BookShelfEntity> bookShelves = pagingBookShelves.getContent();
        int result = bookShelves.size();
        assertThat(result).isEqualTo(2);
    }

    @Test
    void 읽을_책을_조회() throws Exception {
        BookEntity bookEntity1 = getBook("1-4133-0454-0");
        BookEntity bookEntity2 = getBook("1-4133-0454-1");

        bookRepository.save(bookEntity1);
        bookRepository.save(bookEntity2);

        UserEntity userEntity = UserEntity.builder().name("hi").build();
        userRepository.save(userEntity);

        BookShelfEntity bookShelfEntity1 = BookShelfEntity.builder()
            .bookEntity(bookEntity1)
            .userEntity(userEntity)
            .readingStatus(ReadingStatus.WISH)
            .star(Star.ZERO)
            .build();

        BookShelfEntity bookShelfEntity2 = BookShelfEntity.builder()
            .bookEntity(bookEntity2)
            .userEntity(userEntity)
            .readingStatus(ReadingStatus.WISH)
            .star(Star.ZERO)
            .build();

        bookShelfRepository.save(bookShelfEntity1);
        bookShelfRepository.save(bookShelfEntity2);

        userEntity.updateImageUrl("hi");
        bookShelfEntity1.getUserEntity().updateImageUrl("by");

        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").descending());
        Page<BookShelfEntity> pagingBookShelves = bookShelfRepository.findSpecificStatusBookByUserId(
            ReadingStatus.WISH, pageable, userEntity.getId());

        List<BookShelfEntity> bookShelves = pagingBookShelves.getContent();
        int result = bookShelves.size();
        assertThat(result).isEqualTo(2);
    }

    @Test
    void 책장에있는_책_book_id로_삭제_성공() throws Exception {
        BookEntity bookEntity = getBook("1-4133-0454-0");

        bookRepository.save(bookEntity);

        UserEntity userEntity = UserEntity.builder().name("hi").build();
        userRepository.save(userEntity);

        BookShelfEntity bookShelfEntity = BookShelfEntity.builder()
            .bookEntity(bookEntity)
            .userEntity(userEntity)
            .readingStatus(ReadingStatus.READING)
            .build();

        bookShelfRepository.save(bookShelfEntity);

        bookShelfRepository.deleteBookShelfByIdAndUserId(bookShelfEntity.getId(), userEntity.getId());

        int result = bookShelfRepository.findAll().size();
        assertThat(result).isZero();
    }

    @Test
    void bookShelfId_userId로_서재_조회_성공() throws Exception {
        BookEntity bookEntity = getBook("1-4133-0454-0");

        bookRepository.save(bookEntity);

        UserEntity userEntity = UserEntity.builder().name("hi").build();
        userRepository.save(userEntity);

        BookShelfEntity bookShelfEntity = BookShelfEntity.builder()
            .bookEntity(bookEntity)
            .userEntity(userEntity)
            .readingStatus(ReadingStatus.READING)
            .build();

        bookShelfRepository.save(bookShelfEntity);

        BookShelfEntity findBookShelfEntity = bookShelfRepository.findByIdAndUserId(bookShelfEntity.getId(),
            userEntity.getId()).get();
        assertThat(findBookShelfEntity).isEqualTo(bookShelfEntity);

    }

    @Test
    void bookShelfId_userId로_서재_독후감_조회_성공() throws Exception {
        BookEntity bookEntity = getBook("1-4133-0454-0");
        bookRepository.save(bookEntity);

        UserEntity userEntity = UserEntity.builder().name("hi").build();
        userRepository.save(userEntity);

        BookReportEntity bookReportEntity = BookReportEntity.builder()
            .title("test report")
            .content("test report content")
            .build();
        bookReportRepository.save(bookReportEntity);

        BookShelfEntity bookShelfEntity = BookShelfEntity.builder()
            .bookEntity(bookEntity)
            .userEntity(userEntity)
            .bookReportEntity(bookReportEntity)
            .readingStatus(ReadingStatus.READING)
            .build();
        bookShelfRepository.save(bookShelfEntity);

        BookShelfEntity findBookShelfEntity = bookShelfRepository.findWithReportByIdAndUserId(bookShelfEntity.getId(),
            userEntity.getId()).get();

        assertThat(findBookShelfEntity.getBookReportEntity().getTitle()).isEqualTo(bookReportEntity.getTitle());
    }

    @Test
    void 사용자가_생성한_책장_전부_삭제성공() throws Exception {
        BookEntity bookEntity1 = getBook("1-4133-0454-0");
        BookEntity bookEntity2 = getBook("1-4133-0454-1");
        bookRepository.save(bookEntity1);
        bookRepository.save(bookEntity2);

        UserEntity userEntity = UserEntity.builder().name("hi").build();
        userRepository.save(userEntity);

        BookShelfEntity bookShelfEntity1 = BookShelfEntity.builder()
            .bookEntity(bookEntity1)
            .userEntity(userEntity)
            .readingStatus(ReadingStatus.READING)
            .build();

        BookShelfEntity bookShelfEntity2 = BookShelfEntity.builder()
            .bookEntity(bookEntity2)
            .userEntity(userEntity)
            .readingStatus(ReadingStatus.READING)
            .build();

        bookShelfRepository.save(bookShelfEntity1);
        bookShelfRepository.save(bookShelfEntity2);
        bookShelfRepository.deleteAllByUserId(userEntity.getId());

        List<BookShelfEntity> result = bookShelfRepository.findAll();
        assertThat(result).isEmpty();
    }

    @Test
    void isbn으로_사용자_서재에_등록된_책_조회성공() throws Exception {
        BookEntity bookEntity = getBook("1-4133-0454-0");
        bookRepository.save(bookEntity);

        UserEntity userEntity = UserEntity.builder().name("hi").build();
        userRepository.save(userEntity);

        BookShelfEntity bookShelfEntity1 = BookShelfEntity.builder()
            .bookEntity(bookEntity)
            .userEntity(userEntity)
            .readingStatus(ReadingStatus.READING)
            .build();

        bookShelfRepository.save(bookShelfEntity1);

        BookShelfEntity findBookShelfEntity = bookShelfRepository.findByUserIdAndIsbnAndPublishAt(userEntity.getId(),
            bookEntity.getIsbn(), bookEntity.getPublishAt()).get();

        assertThat(findBookShelfEntity).isEqualTo(bookShelfEntity1);
    }

    @Test
    void 서재에_도서_중복_저장_실패() throws Exception {
        BookEntity bookEntity = getBook("1-4133-0454-0");
        bookRepository.save(bookEntity);

        UserEntity userEntity = UserEntity.builder().name("hi").build();
        userRepository.save(userEntity);

        BookShelfEntity bookShelfEntity1 = BookShelfEntity.builder()
            .bookEntity(bookEntity)
            .userEntity(userEntity)
            .readingStatus(ReadingStatus.READING)
            .build();

        bookShelfRepository.save(bookShelfEntity1);

        BookShelfEntity bookShelfEntity2 = BookShelfEntity.builder()
            .bookEntity(bookEntity)
            .userEntity(userEntity)
            .readingStatus(ReadingStatus.READING)
            .build();

        assertThatThrownBy(() -> bookShelfRepository.save(bookShelfEntity2))
            .isInstanceOf(DataIntegrityViolationException.class);
    }
}
