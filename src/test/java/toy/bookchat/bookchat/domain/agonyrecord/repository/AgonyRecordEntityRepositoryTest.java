package toy.bookchat.bookchat.domain.agonyrecord.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
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
  private AgonyRecordRepository agonyRecordRepository;
  @Autowired
  private AgonyRepository agonyRepository;
  @Autowired
  private BookRepository bookRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private BookShelfRepository bookShelfRepository;
  @Autowired
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
        .bookId(bookEntity.getId())
        .userId(userEntity.getId())
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
  @DisplayName("고민기록 등록 성공")
  void save() throws Exception {
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
  @DisplayName("고민 단 건 조회 성공")
  void findUserAgonyRecord() throws Exception {
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

    Optional<AgonyRecordEntity> findAgonyRecord = agonyRecordRepository.findUserAgonyRecord(bookShelfEntity.getId(),
        agonyEntity.getId(), agonyRecordEntity.getId(), userEntity.getId());

    assertThat(findAgonyRecord).isPresent();
  }

  @Test
  @DisplayName("고민 페이징 조회 성공")
  void findSliceOfUserAgonyRecords1() throws Exception {
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
  @DisplayName("고민기록 내림차순 Cursor ID 조회 성공")
  void findSliceOfUserAgonyRecords2() throws Exception {
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
  @DisplayName("고민기록 오름차순 Cursor Id 조회 성공")
  void findSliceOfUserAgonyRecords3() throws Exception {
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
  @DisplayName("고민기록을 지원하지 않는 Cursor Id로 조회시 예외발생")
  void findSliceOfUserAgonyRecords4() throws Exception {
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
  @DisplayName("사용자 고민기록 삭제 성공")
  void deletAgonyRecord() throws Exception {
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
  @DisplayName("고민기록 수정 성공")
  void reviseAgonyRecord() throws Exception {
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

    em.clear();
    String result = agonyRecordRepository.findById(agonyRecordEntity.getId()).get().getTitle();
    assertThat(result).isEqualTo("수정 제목");
  }

  @Test
  @DisplayName("고민기록 여러개 삭제 성공")
  void deleteByAgoniesIds() throws Exception {
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
  @DisplayName("사용자 고민기록 전부 삭제 성공")
  void deleteAllByUserId() throws Exception {
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
  @DisplayName("서재에 할당된 고민기록 삭제 성공")
  void deleteByBookShelfIdAndUserId() throws Exception {
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