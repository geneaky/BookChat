package toy.bookchat.bookchat.domain.agony.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import toy.bookchat.bookchat.domain.RepositoryTest;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.exception.NotSupportedPagingConditionException;

@RepositoryTest
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

        Agony findAgony = agonyRepository.findUserBookShelfAgony(bookShelf.getId(), agony.getId(),
            user.getId()).get();

        assertThat(findAgony).isEqualTo(agony);
    }

    @Test
    void 사용자_책꽂이에_등록한_고민들_첫_페이지_조회_성공() throws Exception {
        Book book = getBook();
        bookRepository.save(book);

        User user = getUser();
        userRepository.save(user);

        BookShelf bookShelf = getBookShelf(user, book);
        bookShelfRepository.save(bookShelf);

        Agony agony1 = getAgony(bookShelf);
        Agony agony2 = getAgony(bookShelf);
        Agony agony3 = getAgony(bookShelf);

        List<Agony> agonyList = List.of(agony1, agony2, agony3);
        agonyRepository.saveAll(agonyList);

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").descending());
        Slice<Agony> pageOfAgonies = agonyRepository.findUserBookShelfSliceOfAgonies(
            bookShelf.getId(), user.getId(),
            pageRequest, Optional.empty());

        List<Agony> content = pageOfAgonies.getContent();
        assertThat(content).containsExactly(agony3, agony2);
    }

    @Test
    void 사용자_책꽂이에_등록한_고민들_두번째_페이지_조회_성공() throws Exception {
        Book book = getBook();
        bookRepository.save(book);

        User user = getUser();
        userRepository.save(user);

        BookShelf bookShelf = getBookShelf(user, book);
        bookShelfRepository.save(bookShelf);

        Agony agony1 = getAgony(bookShelf);
        Agony agony2 = getAgony(bookShelf);
        Agony agony3 = getAgony(bookShelf);

        agonyRepository.save(agony1);
        agonyRepository.save(agony2);
        agonyRepository.save(agony3);

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").descending());
        Slice<Agony> pageOfAgonies = agonyRepository.findUserBookShelfSliceOfAgonies(
            bookShelf.getId(), user.getId(),
            pageRequest, Optional.of(agony3.getId()));

        List<Agony> content = pageOfAgonies.getContent();
        assertThat(content).containsExactly(agony2, agony1);
    }

    @Test
    void 사용자_책꽂이에_등록한_고민들_asc_정렬조건_조회_성공() throws Exception {
        Book book = getBook();
        bookRepository.save(book);

        User user = getUser();
        userRepository.save(user);

        BookShelf bookShelf = getBookShelf(user, book);
        bookShelfRepository.save(bookShelf);

        Agony agony1 = getAgony(bookShelf);
        Agony agony2 = getAgony(bookShelf);
        Agony agony3 = getAgony(bookShelf);

        List<Agony> agonyList = List.of(agony1, agony2, agony3);
        agonyRepository.saveAllAndFlush(agonyList);

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").ascending());
        Slice<Agony> pageOfAgonies = agonyRepository.findUserBookShelfSliceOfAgonies(
            bookShelf.getId(), user.getId(),
            pageRequest, Optional.of(agony1.getId()));

        List<Agony> content = pageOfAgonies.getContent();
        assertThat(content).containsExactly(agony2, agony3);
    }

    @Test
    void 지원하지않는_정렬조건으로_조회시_예외발생() throws Exception {
        Book book = getBook();
        bookRepository.save(book);

        User user = getUser();
        userRepository.save(user);

        BookShelf bookShelf = getBookShelf(user, book);
        bookShelfRepository.save(bookShelf);

        Agony agony1 = getAgony(bookShelf);
        Agony agony2 = getAgony(bookShelf);
        Agony agony3 = getAgony(bookShelf);

        List<Agony> agonyList = List.of(agony1, agony2, agony3);
        agonyRepository.saveAllAndFlush(agonyList);

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("title").ascending());
        assertThatThrownBy(() -> {
            agonyRepository.findUserBookShelfSliceOfAgonies(bookShelf.getId(), user.getId(),
                pageRequest,
                Optional.of(agony1.getId()));
        }).isInstanceOf(NotSupportedPagingConditionException.class);
    }

    @Test
    void 고민_여러개_삭제_성공() throws Exception {
        Book book = getBook();
        bookRepository.save(book);

        User user = getUser();
        userRepository.save(user);

        BookShelf bookShelf = getBookShelf(user, book);
        bookShelfRepository.save(bookShelf);

        Agony agony1 = getAgony(bookShelf);
        Agony agony2 = getAgony(bookShelf);
        Agony agony3 = getAgony(bookShelf);

        List<Agony> agonyList = List.of(agony1, agony2, agony3);
        agonyRepository.saveAll(agonyList);

        List<Long> agoniesIds = List.of(agony1.getId(), agony2.getId(), agony3.getId());
        agonyRepository.deleteByAgoniesIds(bookShelf.getId(), user.getId(), agoniesIds);

        List<Agony> result = agonyRepository.findAll();
        assertThat(result).isEmpty();
    }

    @Test
    void 사용자_고민폴더_전부_삭제성공() throws Exception {
        Book book = getBook();
        bookRepository.save(book);

        User user = getUser();
        userRepository.save(user);

        BookShelf bookShelf = getBookShelf(user, book);
        bookShelfRepository.save(bookShelf);

        Agony agony1 = getAgony(bookShelf);
        Agony agony2 = getAgony(bookShelf);
        Agony agony3 = getAgony(bookShelf);

        List<Agony> agonyList = List.of(agony1, agony2, agony3);
        agonyRepository.saveAll(agonyList);

        agonyRepository.deleteAllByUserId(user.getId());

        List<Agony> result = agonyRepository.findAll();
        assertThat(result).isEmpty();
    }
}