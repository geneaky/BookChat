package toy.bookchat.bookchat.domain.bookshelf.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import toy.bookchat.bookchat.db_module.bookshelf.BookShelfWithBook;
import toy.bookchat.bookchat.db_module.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.Star;

@ExtendWith(MockitoExtension.class)
class BookShelfReaderTest {

  @Mock
  private BookShelfRepository bookShelfRepository;
  @InjectMocks
  private BookShelfReader bookShelfReader;

  @Test
  void 사용자_도서_조회_성공() throws Exception {
    BookShelfWithBook bookShelfWithBook = BookShelfWithBook.builder()
        .bookShelfId(1L)
        .isbn("QWvtJSy")
        .pages(123)
        .star(Star.FIVE)
        .authors(List.of("author1", "author2"))
        .publisher("nVGmlwFrv")
        .publishAt(LocalDate.now())
        .bookCoverImageUrl("fivev2JUf")
        .title("dWYntPma")
        .lastUpdatedAt(LocalDateTime.now())
        .build();
    given(bookShelfRepository.findBookShelfWithBook(any(), any())).willReturn(bookShelfWithBook);

    BookShelf bookShelf = bookShelfReader.readBookShelf(1L, 1L);

    Assertions.assertThat(bookShelf).usingRecursiveComparison().isEqualTo(bookShelfWithBook.toBookShelf());
  }

  @Test
  void 사용자_서재에서_isbn과_발행일자가_일치하는_도서_조회_성공() throws Exception {
    BookShelfWithBook bookShelfWithBook = BookShelfWithBook.builder()
        .bookShelfId(1L)
        .isbn("QWvtJSy")
        .pages(123)
        .star(Star.FIVE)
        .authors(List.of("author1", "author2"))
        .publisher("nVGmlwFrv")
        .publishAt(LocalDate.now())
        .bookCoverImageUrl("fivev2JUf")
        .title("dWYntPma")
        .lastUpdatedAt(LocalDateTime.now())
        .build();
    given(bookShelfRepository.findByUserIdAndIsbnAndPublishAt(any(), any(), any())).willReturn(bookShelfWithBook);

    BookShelf bookShelf = bookShelfReader.readBookShelf(705L, "isbn", LocalDate.now());

    assertThat(bookShelf).isEqualTo(bookShelfWithBook.toBookShelf());
  }

  @Test
  void 사요자_도서_페이징_조회_성공() throws Exception {
    BookShelfWithBook bookShelfWithBook = BookShelfWithBook.builder()
        .bookShelfId(1L)
        .isbn("QWvtJSy")
        .pages(123)
        .star(Star.FIVE)
        .authors(List.of("author1", "author2"))
        .publisher("nVGmlwFrv")
        .publishAt(LocalDate.now())
        .bookCoverImageUrl("fivev2JUf")
        .title("dWYntPma")
        .lastUpdatedAt(LocalDateTime.now())
        .build();
    Page<BookShelfWithBook> pagedBookShelfWithBook = new PageImpl<>(List.of(bookShelfWithBook), mock(Pageable.class),
        1);
    given(bookShelfRepository.findBookShelfWithBook(any(), any(), any())).willReturn(pagedBookShelfWithBook);

    Page<BookShelf> bookShelves = bookShelfReader.readPagedBookShelves(1L, ReadingStatus.WISH, mock(Pageable.class));

    assertThat(bookShelves.getContent()).extracting(BookShelf::getId, BookShelf::getStar)
        .containsExactly(tuple(bookShelfWithBook.getBookShelfId(), bookShelfWithBook.getStar()));
  }

}