package toy.bookchat.bookchat.domain.agonyrecord.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import toy.bookchat.bookchat.db_module.agony.AgonyEntity;
import toy.bookchat.bookchat.db_module.agony.repository.AgonyRepository;
import toy.bookchat.bookchat.db_module.agonyrecord.AgonyRecordEntity;
import toy.bookchat.bookchat.db_module.agonyrecord.repository.AgonyRecordRepository;
import toy.bookchat.bookchat.db_module.book.BookEntity;
import toy.bookchat.bookchat.db_module.book.repository.BookRepository;
import toy.bookchat.bookchat.db_module.bookshelf.BookShelfEntity;
import toy.bookchat.bookchat.db_module.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.db_module.user.UserEntity;
import toy.bookchat.bookchat.db_module.user.repository.UserRepository;
import toy.bookchat.bookchat.domain.RepositoryTest;
import toy.bookchat.bookchat.exception.badrequest.NotSupportedPagingConditionException;

class AgonyRecordEntityRepositoryTest extends RepositoryTest {

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

    private BookEntity getBook() {
        return BookEntity.builder()
            .isbn("1-4133-0454-0")
            .title("effective java")
            .publishAt(LocalDate.now())
            .authors(List.of("Joshua"))
            .publisher("insight")
            .bookCoverImageUrl("bookCover@naver.com")
            .build();
    }

    private UserEntity getUser() {
        return UserEntity.builder()
            .build();
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
            .bookShelfId(bookShelfEntity.getId())
            .build();
    }

    private AgonyRecordEntity getAgonyRecord(AgonyEntity agonyEntity) {
        return AgonyRecordEntity.builder()
            .title("recordTitle")
            .content("recordContent")
            .agonyId(agonyEntity.getId())
            .build();
    }

    @Test
    void 고민기록_등록_성공() throws Exception {
        BookEntity bookEntity = getBook();
        bookRepository.save(bookEntity);

        UserEntity userEntity = getUser();
        userRepository.save(userEntity);

        BookShelfEntity bookShelfEntity = getBookShelf(userEntity, bookEntity);
        bookShelfRepository.save(bookShelfEntity);

        AgonyEntity agonyEntity = getAgony(bookShelfEntity);
        agonyRepository.save(agonyEntity);

        AgonyRecordEntity agonyRecordEntity = getAgonyRecord(agonyEntity);
        agonyRecordRepository.save(agonyRecordEntity);

        AgonyRecordEntity findAgonyRecordEntity = agonyRecordRepository.findById(agonyRecordEntity.getId()).get();
        assertThat(agonyRecordEntity).isEqualTo(findAgonyRecordEntity);
    }

    @Test
    void 고민_단_건_조회_성공() throws Exception {
        BookEntity bookEntity = getBook();
        bookRepository.save(bookEntity);

        UserEntity userEntity = getUser();
        userRepository.save(userEntity);

        BookShelfEntity bookShelfEntity = getBookShelf(userEntity, bookEntity);
        bookShelfRepository.save(bookShelfEntity);

        AgonyEntity agonyEntity = getAgony(bookShelfEntity);
        agonyRepository.save(agonyEntity);

        AgonyRecordEntity agonyRecordEntity = getAgonyRecord(agonyEntity);
        agonyRecordRepository.save(agonyRecordEntity);

        Optional<AgonyRecordEntity> findAgonyRecord = agonyRecordRepository.findUserAgonyRecord(bookShelfEntity.getId(), agonyEntity.getId(), agonyRecordEntity.getId(), userEntity.getId());

        assertThat(findAgonyRecord).isPresent();
    }

    @Test
    void 고민_페이징_조회_성공() throws Exception {
        BookEntity bookEntity = getBook();
        bookRepository.save(bookEntity);

        UserEntity userEntity = getUser();
        userRepository.save(userEntity);

        BookShelfEntity bookShelfEntity = getBookShelf(userEntity, bookEntity);
        bookShelfRepository.save(bookShelfEntity);

        AgonyEntity agonyEntity = getAgony(bookShelfEntity);
        agonyRepository.save(agonyEntity);

        AgonyRecordEntity agonyRecordEntity = getAgonyRecord(agonyEntity);
        agonyRecordRepository.save(agonyRecordEntity);

        Pageable pageable = PageRequest.of(0, 1, Sort.by("id").descending());
        List<AgonyRecordEntity> content = agonyRecordRepository.findSliceOfUserAgonyRecords(
            bookShelfEntity.getId(),
            agonyEntity.getId(),
            userEntity.getId(), pageable, null).getContent();
        assertThat(content).containsExactly(agonyRecordEntity);
    }

    @Test
    void 고민기록_DESC_커서ID로_조회_성공() throws Exception {
        BookEntity bookEntity = getBook();
        bookRepository.save(bookEntity);

        UserEntity userEntity = getUser();
        userRepository.save(userEntity);

        BookShelfEntity bookShelfEntity = getBookShelf(userEntity, bookEntity);
        bookShelfRepository.save(bookShelfEntity);

        AgonyEntity agonyEntity = getAgony(bookShelfEntity);
        agonyRepository.save(agonyEntity);

        AgonyRecordEntity agonyRecordEntity1 = getAgonyRecord(agonyEntity);
        AgonyRecordEntity agonyRecordEntity2 = getAgonyRecord(agonyEntity);
        AgonyRecordEntity agonyRecordEntity3 = getAgonyRecord(agonyEntity);
        agonyRecordRepository.save(agonyRecordEntity1);
        agonyRecordRepository.save(agonyRecordEntity2);
        agonyRecordRepository.save(agonyRecordEntity3);

        Pageable pageable = PageRequest.of(0, 1, Sort.by("id").descending());
        List<AgonyRecordEntity> content = agonyRecordRepository.findSliceOfUserAgonyRecords(
                bookShelfEntity.getId(),
                agonyEntity.getId(),
                userEntity.getId(), pageable, agonyRecordEntity3.getId())
            .getContent();
        assertThat(content).containsExactly(agonyRecordEntity2);
    }

    @Test
    void 고민기록_ASC_커서ID로_조회_성공() throws Exception {
        BookEntity bookEntity = getBook();
        bookRepository.save(bookEntity);

        UserEntity userEntity = getUser();
        userRepository.save(userEntity);

        BookShelfEntity bookShelfEntity = getBookShelf(userEntity, bookEntity);
        bookShelfRepository.save(bookShelfEntity);

        AgonyEntity agonyEntity = getAgony(bookShelfEntity);
        agonyRepository.save(agonyEntity);

        AgonyRecordEntity agonyRecordEntity1 = getAgonyRecord(agonyEntity);
        AgonyRecordEntity agonyRecordEntity2 = getAgonyRecord(agonyEntity);
        AgonyRecordEntity agonyRecordEntity3 = getAgonyRecord(agonyEntity);
        agonyRecordRepository.save(agonyRecordEntity1);
        agonyRecordRepository.save(agonyRecordEntity2);
        agonyRecordRepository.save(agonyRecordEntity3);

        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").ascending());
        List<AgonyRecordEntity> content = agonyRecordRepository.findSliceOfUserAgonyRecords(
                bookShelfEntity.getId(),
                agonyEntity.getId(),
                userEntity.getId(), pageable, agonyRecordEntity1.getId())
            .getContent();
        assertThat(content).containsExactly(agonyRecordEntity2, agonyRecordEntity3);
    }

    @Test
    void 고민기록_지원하지않는_커서ID로_조회시_예외발생() throws Exception {
        BookEntity bookEntity = getBook();
        bookRepository.save(bookEntity);

        UserEntity userEntity = getUser();
        userRepository.save(userEntity);

        BookShelfEntity bookShelfEntity = getBookShelf(userEntity, bookEntity);
        bookShelfRepository.save(bookShelfEntity);

        AgonyEntity agonyEntity = getAgony(bookShelfEntity);
        agonyRepository.save(agonyEntity);

        AgonyRecordEntity agonyRecordEntity1 = getAgonyRecord(agonyEntity);
        AgonyRecordEntity agonyRecordEntity2 = getAgonyRecord(agonyEntity);
        AgonyRecordEntity agonyRecordEntity3 = getAgonyRecord(agonyEntity);
        agonyRecordRepository.save(agonyRecordEntity1);
        agonyRecordRepository.save(agonyRecordEntity2);
        agonyRecordRepository.save(agonyRecordEntity3);

        Pageable pageable = PageRequest.of(0, 2, Sort.by("title").ascending());
        assertThatThrownBy(() -> {
            agonyRecordRepository.findSliceOfUserAgonyRecords(bookShelfEntity.getId(), agonyEntity.getId(),
                userEntity.getId(), pageable, agonyRecordEntity1.getId());
        }).isInstanceOf(NotSupportedPagingConditionException.class);
    }

    @Test
    void 사용자_고민기록_삭제_성공() throws Exception {
        BookEntity bookEntity = getBook();
        bookRepository.save(bookEntity);

        UserEntity userEntity = getUser();
        userRepository.save(userEntity);

        BookShelfEntity bookShelfEntity = getBookShelf(userEntity, bookEntity);
        bookShelfRepository.save(bookShelfEntity);

        AgonyEntity agonyEntity = getAgony(bookShelfEntity);
        agonyRepository.save(agonyEntity);

        AgonyRecordEntity agonyRecordEntity = getAgonyRecord(agonyEntity);
        agonyRecordRepository.save(agonyRecordEntity);

        agonyRecordRepository.deleteAgonyRecord(bookShelfEntity.getId(), agonyEntity.getId(),
            agonyRecordEntity.getId(), userEntity.getId());

        List<AgonyRecordEntity> all = agonyRecordRepository.findAll();
        assertThat(all).isEmpty();
    }

    @Test
    void 고민기록_수정_성공() throws Exception {
        BookEntity bookEntity = getBook();
        bookRepository.save(bookEntity);

        UserEntity userEntity = getUser();
        userRepository.save(userEntity);

        BookShelfEntity bookShelfEntity = getBookShelf(userEntity, bookEntity);
        bookShelfRepository.save(bookShelfEntity);

        AgonyEntity agonyEntity = getAgony(bookShelfEntity);
        agonyRepository.save(agonyEntity);

        AgonyRecordEntity agonyRecordEntity = getAgonyRecord(agonyEntity);
        agonyRecordRepository.save(agonyRecordEntity);

        agonyRecordRepository.reviseAgonyRecord(bookShelfEntity.getId(), agonyEntity.getId(),
            agonyRecordEntity.getId(), userEntity.getId(), "수정 제목", "수정 내용");

        em.flush();
        em.clear();
        String result = agonyRecordRepository.findById(agonyRecordEntity.getId()).get().getTitle();
        assertThat(result).isEqualTo("수정 제목");
    }

    @Test
    void 고민_기록_여러개_삭제_성공() throws Exception {
        BookEntity bookEntity = getBook();
        bookRepository.save(bookEntity);

        UserEntity userEntity = getUser();
        userRepository.save(userEntity);

        BookShelfEntity bookShelfEntity = getBookShelf(userEntity, bookEntity);
        bookShelfRepository.save(bookShelfEntity);

        AgonyEntity agonyEntity = getAgony(bookShelfEntity);
        agonyRepository.save(agonyEntity);

        AgonyRecordEntity agonyRecordEntity1 = getAgonyRecord(agonyEntity);
        AgonyRecordEntity agonyRecordEntity2 = getAgonyRecord(agonyEntity);
        AgonyRecordEntity agonyRecordEntity3 = getAgonyRecord(agonyEntity);
        List<AgonyRecordEntity> agonyRecordEntities = List.of(agonyRecordEntity1, agonyRecordEntity2, agonyRecordEntity3);
        agonyRecordRepository.saveAll(agonyRecordEntities);

        agonyRecordRepository.deleteByAgoniesIds(bookShelfEntity.getId(), userEntity.getId(),
            List.of(agonyEntity.getId()));

        List<AgonyRecordEntity> result = agonyRecordRepository.findAll();
        assertThat(result).isEmpty();
    }

    @Test
    void 사용자가_생성한_고민기록_전부_삭제성공() throws Exception {
        BookEntity bookEntity = getBook();
        bookRepository.save(bookEntity);

        UserEntity userEntity = getUser();
        userRepository.save(userEntity);

        BookShelfEntity bookShelfEntity = getBookShelf(userEntity, bookEntity);
        bookShelfRepository.save(bookShelfEntity);

        AgonyEntity agonyEntity = getAgony(bookShelfEntity);
        agonyRepository.save(agonyEntity);

        AgonyRecordEntity agonyRecordEntity1 = getAgonyRecord(agonyEntity);
        AgonyRecordEntity agonyRecordEntity2 = getAgonyRecord(agonyEntity);
        AgonyRecordEntity agonyRecordEntity3 = getAgonyRecord(agonyEntity);
        List<AgonyRecordEntity> agonyRecordEntities = List.of(agonyRecordEntity1, agonyRecordEntity2, agonyRecordEntity3);
        agonyRecordRepository.saveAll(agonyRecordEntities);

        agonyRecordRepository.deleteAllByUserId(userEntity.getId());
        List<AgonyRecordEntity> result = agonyRecordRepository.findAll();
        assertThat(result).isEmpty();
    }

    @Test
    void 서재에_할당된_고민기록_삭제_성공() throws Exception {
        BookEntity bookEntity1 = getBook();
        BookEntity bookEntity2 = BookEntity.builder()
            .isbn("123")
            .publishAt(LocalDate.of(2020, 1, 26))
            .build();
        ;
        bookRepository.save(bookEntity1);
        bookRepository.save(bookEntity2);

        UserEntity userEntity = getUser();
        userRepository.save(userEntity);

        BookShelfEntity bookShelfEntity1 = getBookShelf(userEntity, bookEntity1);
        BookShelfEntity bookShelfEntity2 = getBookShelf(userEntity, bookEntity2);
        bookShelfRepository.save(bookShelfEntity1);
        bookShelfRepository.save(bookShelfEntity2);

        AgonyEntity agonyEntity1 = getAgony(bookShelfEntity1);
        AgonyEntity agonyEntity2 = getAgony(bookShelfEntity2);
        agonyRepository.save(agonyEntity1);
        agonyRepository.save(agonyEntity2);

        AgonyRecordEntity agonyRecordEntity1 = getAgonyRecord(agonyEntity1);
        AgonyRecordEntity agonyRecordEntity2 = getAgonyRecord(agonyEntity1);
        AgonyRecordEntity agonyRecordEntity3 = getAgonyRecord(agonyEntity2);
        List<AgonyRecordEntity> agonyRecordEntities = List.of(agonyRecordEntity1, agonyRecordEntity2, agonyRecordEntity3);
        agonyRecordRepository.saveAll(agonyRecordEntities);

        agonyRecordRepository.deleteByBookShelfIdAndUserId(bookShelfEntity1.getId(), userEntity.getId());
        List<AgonyRecordEntity> result = agonyRecordRepository.findAll();
        assertThat(result.size()).isOne();
    }
}