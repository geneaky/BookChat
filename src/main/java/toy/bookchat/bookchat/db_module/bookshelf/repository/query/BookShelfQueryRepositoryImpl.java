package toy.bookchat.bookchat.db_module.bookshelf.repository.query;

import static toy.bookchat.bookchat.db_module.book.QBookEntity.bookEntity;
import static toy.bookchat.bookchat.db_module.bookshelf.QBookShelfEntity.bookShelfEntity;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.extractOrderSpecifierFrom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import toy.bookchat.bookchat.db_module.book.BookEntity;
import toy.bookchat.bookchat.db_module.bookshelf.BookShelfEntity;
import toy.bookchat.bookchat.db_module.bookshelf.BookShelfWithBook;
import toy.bookchat.bookchat.db_module.bookshelf.QBookShelfWithBook;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;

@Repository
public class BookShelfQueryRepositoryImpl implements BookShelfQueryRepository {

  private final JPAQueryFactory queryFactory;

  public BookShelfQueryRepositoryImpl(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  @Override
  public Page<BookShelfWithBook> findBookShelfWithBook(Long userId, ReadingStatus readingStatus, Pageable pageable) {
    List<BookShelfWithBook> bookShelfWithBookList = queryFactory.select(new QBookShelfWithBook(
            bookShelfEntity.id,
            bookEntity.id,
            bookEntity.title,
            bookEntity.isbn,
            bookEntity.bookCoverImageUrl,
            bookEntity.publishAt,
            bookEntity.publisher,
            bookShelfEntity.star,
            bookShelfEntity.pages,
            bookShelfEntity.updatedAt
        ))
        .from(bookShelfEntity)
        .join(bookEntity).on(bookShelfEntity.bookId.eq(bookEntity.id))
        .where(bookShelfEntity.readingStatus.eq(readingStatus)
            .and(bookShelfEntity.userId.eq(userId)))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(extractOrderSpecifierFrom(bookShelfEntity, pageable))
        .fetch();

    List<Long> bookIds = bookShelfWithBookList.stream().map(BookShelfWithBook::getBookId).collect(Collectors.toList());

    Map<Long, List<String>> bookAuthorMap = queryFactory.select(bookEntity)
        .from(bookEntity)
        .where(bookEntity.id.in(bookIds))
        .fetch()
        .stream()
        .collect(Collectors.toMap(BookEntity::getId, BookEntity::getAuthors));

    bookShelfWithBookList.forEach(
        bookShelfWithBook -> bookShelfWithBook.setAuthors(bookAuthorMap.get(bookShelfWithBook.getBookId())));

    return new PageImpl<>(bookShelfWithBookList, pageable, bookShelfWithBookList.size());
  }

  @Override
  public Optional<BookShelfWithBook> findByUserIdAndIsbnAndPublishAt(Long userId, String isbn, LocalDate publishAt) {
    BookShelfWithBook bookShelfWithBook = queryFactory.select(new QBookShelfWithBook(
            bookShelfEntity.id,
            bookEntity.id,
            bookEntity.title,
            bookEntity.isbn,
            bookEntity.bookCoverImageUrl,
            bookEntity.publishAt,
            bookEntity.publisher,
            bookShelfEntity.star,
            bookShelfEntity.pages,
            bookShelfEntity.updatedAt
        ))
        .from(bookShelfEntity)
        .innerJoin(bookEntity).on(bookShelfEntity.bookId.eq(bookEntity.id))
        .where(bookShelfEntity.userId.eq(userId)
            .and(bookEntity.isbn.eq(isbn))
            .and(bookEntity.publishAt.eq(publishAt)))
        .fetchOne();

    BookEntity findBookEntity = queryFactory.select(bookEntity)
        .from(bookEntity)
        .where(bookEntity.id.eq(bookShelfWithBook.getBookId()))
        .fetchOne();
    bookShelfWithBook.setAuthors(findBookEntity.getAuthors());

    return Optional.ofNullable(bookShelfWithBook);
  }

  @Override
  public Optional<BookShelfWithBook> findBookShelfWithBook(Long userId, Long bookShelfId) {
    BookShelfWithBook bookShelfWithBook = queryFactory.select(new QBookShelfWithBook(
            bookShelfEntity.id,
            bookEntity.id,
            bookEntity.title,
            bookEntity.isbn,
            bookEntity.bookCoverImageUrl,
            bookEntity.publishAt,
            bookEntity.publisher,
            bookShelfEntity.star,
            bookShelfEntity.pages,
            bookShelfEntity.updatedAt
        ))
        .from(bookShelfEntity)
        .innerJoin(bookEntity).on(bookShelfEntity.bookId.eq(bookEntity.id))
        .where(bookShelfEntity.userId.eq(userId)
            .and(bookShelfEntity.id.eq(bookShelfId)))
        .fetchOne();

    BookEntity findBookEntity = queryFactory.select(bookEntity)
        .from(bookEntity)
        .where(bookEntity.id.eq(bookShelfWithBook.getBookId()))
        .fetchOne();
    bookShelfWithBook.setAuthors(findBookEntity.getAuthors());

    return Optional.ofNullable(bookShelfWithBook);
  }

  @Override
  public void deleteBookShelfByIdAndUserId(Long bookShelfId, Long userId) {
    queryFactory.delete(bookShelfEntity)
        .where(bookShelfEntity.id.eq(bookShelfId)
            .and(bookShelfEntity.userId.eq(userId)))
        .execute();
  }

  @Override
  public void deleteAllByUserId(Long userId) {
    queryFactory.delete(bookShelfEntity)
        .where(bookShelfEntity.userId.eq(userId)).execute();
  }

  @Override
  public Optional<BookShelfEntity> findByIdAndUserId(Long bookShelfId, Long userId) {
    return Optional.ofNullable(queryFactory.select(bookShelfEntity)
        .from(bookShelfEntity)
        .where(bookShelfEntity.id.eq(bookShelfId)
            .and(bookShelfEntity.userId.eq(userId)))
        .fetchOne());
  }
}
