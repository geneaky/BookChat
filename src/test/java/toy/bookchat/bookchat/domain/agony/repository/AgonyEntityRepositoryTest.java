package toy.bookchat.bookchat.domain.agony.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import toy.bookchat.bookchat.db_module.agony.AgonyEntity;
import toy.bookchat.bookchat.db_module.agony.repository.AgonyRepository;
import toy.bookchat.bookchat.db_module.book.BookEntity;
import toy.bookchat.bookchat.db_module.book.repository.BookRepository;
import toy.bookchat.bookchat.db_module.bookshelf.BookShelfEntity;
import toy.bookchat.bookchat.db_module.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.db_module.user.UserEntity;
import toy.bookchat.bookchat.db_module.user.repository.UserRepository;
import toy.bookchat.bookchat.domain.RepositoryTest;
import toy.bookchat.bookchat.exception.badrequest.NotSupportedPagingConditionException;

class AgonyEntityRepositoryTest extends RepositoryTest {

  @Autowired
  private AgonyRepository agonyRepository;
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

  @Test
  @DisplayName("고민 등록 성공")
  void save() throws Exception {

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
  @DisplayName("사용자의 책꽂이에 등록한 고민 조회 성공")
  void findUserBookShelfAgony() throws Exception {
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
  @DisplayName("사용자 책꽂이에 등록한 고민들 첫 페이지 조회 성공")
  void findUserBookShelfSliceOfAgonies1() throws Exception {
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
  @DisplayName("사용자 책꽂이에 등록한 고민들의 두번째 페이지 조회 성공")
  void findUserBookShelfSliceOfAgonies2() throws Exception {
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
  @DisplayName("사용자 책꽂이에 등록한 고민들을 오름차순 조회 성공")
  void findUserBookShelfSliceOfAgonies3() throws Exception {
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

    PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").ascending());
    Slice<AgonyEntity> pageOfAgonies = agonyRepository.findUserBookShelfSliceOfAgonies(
        bookShelfEntity.getId(), userEntity.getId(),
        pageRequest, agonyEntity1.getId());

    List<AgonyEntity> content = pageOfAgonies.getContent();
    assertThat(content).containsExactly(agonyEntity2, agonyEntity3);
  }

  @Test
  @DisplayName("지원하지 않는 정렬조건을 조회시 예외발생")
  void findUserBookShelfSliceOfAgonies4() throws Exception {
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

    PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("title").ascending());
    assertThatThrownBy(() -> {
      agonyRepository.findUserBookShelfSliceOfAgonies(bookShelfEntity.getId(), userEntity.getId(),
          pageRequest, agonyEntity1.getId());
    }).isInstanceOf(NotSupportedPagingConditionException.class);
  }

  @Test
  @DisplayName("고민 여러개 삭제 성공")
  void deleteByAgoniesIds() throws Exception {
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
  @DisplayName("사용자 고민폴더 전체 삭제 성공")
  void deleteAllByUserId() throws Exception {
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
  @DisplayName("서재에 할당된 고민 삭제 성공")
  void deleteByBookShelfIdAndUserId() throws Exception {
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