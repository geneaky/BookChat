package toy.bookchat.bookchat.domain.agony.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import toy.bookchat.bookchat.db_module.agony.AgonyEntity;
import toy.bookchat.bookchat.db_module.agony.repository.AgonyRepository;
import toy.bookchat.bookchat.domain.RepositoryTest;
import toy.bookchat.bookchat.domain.book.BookEntity;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookShelfEntity;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.domain.user.UserEntity;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.exception.badrequest.NotSupportedPagingConditionException;

class AgonyEntityRepositoryTest extends RepositoryTest {

    @Autowired
    AgonyRepository agonyRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookShelfRepository bookShelfRepository;

    private BookEntity getBook() {
        return BookEntity.builder()
            .isbn("1-4133-0454-0")
            .title("effective java")
            .publisher("insight")
            .publishAt(LocalDate.now())
            .bookCoverImageUrl("bookCover@naver.com")
            .authors(List.of("Joshua"))
            .build();
    }

    private UserEntity getUser() {
        return UserEntity.builder().build();
    }

    private BookShelfEntity getBookShelf(UserEntity userEntity, BookEntity bookEntity) {
        return BookShelfEntity.builder()
            .userEntity(userEntity)
            .bookEntity(bookEntity)
            .build();
    }

    private AgonyEntity getAgony(BookShelfEntity bookShelfEntity) {
        return AgonyEntity.builder()
            .title("title")
            .hexColorCode("blue")
            .bookShelfEntity(bookShelfEntity)
            .build();
    }

    @Test
    void 고민_등록_성공() throws Exception {

        BookEntity bookEntity = getBook();
        bookRepository.save(bookEntity);

        UserEntity userEntity = getUser();
        userRepository.save(userEntity);

        BookShelfEntity bookShelfEntity = getBookShelf(userEntity, bookEntity);
        bookShelfRepository.save(bookShelfEntity);

        AgonyEntity agonyEntity = getAgony(bookShelfEntity);
        agonyRepository.save(agonyEntity);

        AgonyEntity findAgonyEntity = agonyRepository.findById(agonyEntity.getId()).get();

        assertThat(findAgonyEntity).isEqualTo(agonyEntity);
    }

    @Test
    void 사용자의_책꽂이에_등록한_고민_조회_성공() throws Exception {
        BookEntity bookEntity = getBook();
        bookRepository.save(bookEntity);

        UserEntity userEntity = getUser();
        userRepository.save(userEntity);

        BookShelfEntity bookShelfEntity = getBookShelf(userEntity, bookEntity);
        bookShelfRepository.save(bookShelfEntity);

        AgonyEntity agonyEntity = getAgony(bookShelfEntity);
        agonyRepository.save(agonyEntity);

        AgonyEntity findAgonyEntity = agonyRepository.findUserBookShelfAgony(bookShelfEntity.getId(), agonyEntity.getId(),
            userEntity.getId()).get();

        assertThat(findAgonyEntity).isEqualTo(agonyEntity);
    }

    @Test
    void 사용자_책꽂이에_등록한_고민들_첫_페이지_조회_성공() throws Exception {
        BookEntity bookEntity = getBook();
        bookRepository.save(bookEntity);

        UserEntity userEntity = getUser();
        userRepository.save(userEntity);

        BookShelfEntity bookShelfEntity = getBookShelf(userEntity, bookEntity);
        bookShelfRepository.save(bookShelfEntity);

        AgonyEntity agonyEntity1 = getAgony(bookShelfEntity);
        AgonyEntity agonyEntity2 = getAgony(bookShelfEntity);
        AgonyEntity agonyEntity3 = getAgony(bookShelfEntity);

        List<AgonyEntity> agonyEntityList = List.of(agonyEntity1, agonyEntity2, agonyEntity3);
        agonyRepository.saveAll(agonyEntityList);

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").descending());
        Slice<AgonyEntity> pageOfAgonies = agonyRepository.findUserBookShelfSliceOfAgonies(
            bookShelfEntity.getId(), userEntity.getId(),
            pageRequest, null);

        List<AgonyEntity> content = pageOfAgonies.getContent();
        assertThat(content).containsExactly(agonyEntity3, agonyEntity2);
    }

    @Test
    void 사용자_책꽂이에_등록한_고민들_두번째_페이지_조회_성공() throws Exception {
        BookEntity bookEntity = getBook();
        bookRepository.save(bookEntity);

        UserEntity userEntity = getUser();
        userRepository.save(userEntity);

        BookShelfEntity bookShelfEntity = getBookShelf(userEntity, bookEntity);
        bookShelfRepository.save(bookShelfEntity);

        AgonyEntity agonyEntity1 = getAgony(bookShelfEntity);
        AgonyEntity agonyEntity2 = getAgony(bookShelfEntity);
        AgonyEntity agonyEntity3 = getAgony(bookShelfEntity);

        agonyRepository.save(agonyEntity1);
        agonyRepository.save(agonyEntity2);
        agonyRepository.save(agonyEntity3);

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").descending());
        Slice<AgonyEntity> pageOfAgonies = agonyRepository.findUserBookShelfSliceOfAgonies(
            bookShelfEntity.getId(), userEntity.getId(),
            pageRequest, agonyEntity3.getId());

        List<AgonyEntity> content = pageOfAgonies.getContent();
        assertThat(content).containsExactly(agonyEntity2, agonyEntity1);
    }

    @Test
    void 사용자_책꽂이에_등록한_고민들_asc_정렬조건_조회_성공() throws Exception {
        BookEntity bookEntity = getBook();
        bookRepository.save(bookEntity);

        UserEntity userEntity = getUser();
        userRepository.save(userEntity);

        BookShelfEntity bookShelfEntity = getBookShelf(userEntity, bookEntity);
        bookShelfRepository.save(bookShelfEntity);

        AgonyEntity agonyEntity1 = getAgony(bookShelfEntity);
        AgonyEntity agonyEntity2 = getAgony(bookShelfEntity);
        AgonyEntity agonyEntity3 = getAgony(bookShelfEntity);

        List<AgonyEntity> agonyEntityList = List.of(agonyEntity1, agonyEntity2, agonyEntity3);
        agonyRepository.saveAllAndFlush(agonyEntityList);

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").ascending());
        Slice<AgonyEntity> pageOfAgonies = agonyRepository.findUserBookShelfSliceOfAgonies(
            bookShelfEntity.getId(), userEntity.getId(),
            pageRequest, agonyEntity1.getId());

        List<AgonyEntity> content = pageOfAgonies.getContent();
        assertThat(content).containsExactly(agonyEntity2, agonyEntity3);
    }

    @Test
    void 지원하지않는_정렬조건으로_조회시_예외발생() throws Exception {
        BookEntity bookEntity = getBook();
        bookRepository.save(bookEntity);

        UserEntity userEntity = getUser();
        userRepository.save(userEntity);

        BookShelfEntity bookShelfEntity = getBookShelf(userEntity, bookEntity);
        bookShelfRepository.save(bookShelfEntity);

        AgonyEntity agonyEntity1 = getAgony(bookShelfEntity);
        AgonyEntity agonyEntity2 = getAgony(bookShelfEntity);
        AgonyEntity agonyEntity3 = getAgony(bookShelfEntity);

        List<AgonyEntity> agonyEntityList = List.of(agonyEntity1, agonyEntity2, agonyEntity3);
        agonyRepository.saveAllAndFlush(agonyEntityList);

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("title").ascending());
        assertThatThrownBy(() -> {
            agonyRepository.findUserBookShelfSliceOfAgonies(bookShelfEntity.getId(), userEntity.getId(),
                pageRequest, agonyEntity1.getId());
        }).isInstanceOf(NotSupportedPagingConditionException.class);
    }

    @Test
    void 고민_여러개_삭제_성공() throws Exception {
        BookEntity bookEntity = getBook();
        bookRepository.save(bookEntity);

        UserEntity userEntity = getUser();
        userRepository.save(userEntity);

        BookShelfEntity bookShelfEntity = getBookShelf(userEntity, bookEntity);
        bookShelfRepository.save(bookShelfEntity);

        AgonyEntity agonyEntity1 = getAgony(bookShelfEntity);
        AgonyEntity agonyEntity2 = getAgony(bookShelfEntity);
        AgonyEntity agonyEntity3 = getAgony(bookShelfEntity);

        List<AgonyEntity> agonyEntityList = List.of(agonyEntity1, agonyEntity2, agonyEntity3);
        agonyRepository.saveAll(agonyEntityList);

        List<Long> agoniesIds = List.of(agonyEntity1.getId(), agonyEntity2.getId(), agonyEntity3.getId());
        agonyRepository.deleteByAgoniesIds(bookShelfEntity.getId(), userEntity.getId(), agoniesIds);

        List<AgonyEntity> result = agonyRepository.findAll();
        assertThat(result).isEmpty();
    }

    @Test
    void 사용자_고민폴더_전부_삭제성공() throws Exception {
        BookEntity bookEntity = getBook();
        bookRepository.save(bookEntity);

        UserEntity userEntity = getUser();
        userRepository.save(userEntity);

        BookShelfEntity bookShelfEntity = getBookShelf(userEntity, bookEntity);
        bookShelfRepository.save(bookShelfEntity);

        AgonyEntity agonyEntity1 = getAgony(bookShelfEntity);
        AgonyEntity agonyEntity2 = getAgony(bookShelfEntity);
        AgonyEntity agonyEntity3 = getAgony(bookShelfEntity);

        List<AgonyEntity> agonyEntityList = List.of(agonyEntity1, agonyEntity2, agonyEntity3);
        agonyRepository.saveAll(agonyEntityList);

        agonyRepository.deleteAllByUserId(userEntity.getId());

        List<AgonyEntity> result = agonyRepository.findAll();
        assertThat(result).isEmpty();
    }

    @Test
    void 서재에_할당된_고민_삭제_성공() throws Exception {
        BookEntity bookEntity1 = getBook();
        BookEntity bookEntity2 = BookEntity.builder()
            .isbn("123")
            .publishAt(LocalDate.of(2020, 1, 26))
            .build();
        bookRepository.save(bookEntity1);
        bookRepository.save(bookEntity2);

        UserEntity userEntity = getUser();
        userRepository.save(userEntity);

        BookShelfEntity bookShelfEntity1 = getBookShelf(userEntity, bookEntity1);
        BookShelfEntity bookShelfEntity2 = getBookShelf(userEntity, bookEntity2);
        bookShelfRepository.save(bookShelfEntity1);
        bookShelfRepository.save(bookShelfEntity2);

        AgonyEntity agonyEntity1 = getAgony(bookShelfEntity1);
        AgonyEntity agonyEntity2 = getAgony(bookShelfEntity1);
        AgonyEntity agonyEntity3 = getAgony(bookShelfEntity2);

        List<AgonyEntity> agonyEntityList = List.of(agonyEntity1, agonyEntity2, agonyEntity3);
        agonyRepository.saveAll(agonyEntityList);

        agonyRepository.deleteByBookShelfIdAndUserId(bookShelfEntity1.getId(), userEntity.getId());
        List<AgonyEntity> result = agonyRepository.findAll();
        assertThat(result.size()).isOne();
    }
}