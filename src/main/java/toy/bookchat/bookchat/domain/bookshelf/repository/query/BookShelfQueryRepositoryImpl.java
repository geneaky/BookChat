package toy.bookchat.bookchat.domain.bookshelf.repository.query;

import static toy.bookchat.bookchat.domain.book.QBook.book;
import static toy.bookchat.bookchat.domain.bookreport.QBookReport.bookReport;
import static toy.bookchat.bookchat.domain.bookshelf.QBookShelf.bookShelf;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.extractOrderSpecifierFrom;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.exception.book.BookNotFoundException;

@Repository
public class BookShelfQueryRepositoryImpl implements BookShelfQueryRepository {

    private final JPAQueryFactory queryFactory;

    public BookShelfQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }


    @Override
    public Page<BookShelf> findSpecificStatusBookByUserId(
        ReadingStatus readingStatus, Pageable pageable, Long userId) {

        JPAQuery<BookShelf> jpaQuery = queryFactory.select(bookShelf)
            .from(bookShelf).join(bookShelf.book, book).fetchJoin()
            .join(book.authors).fetchJoin()
            .where(bookShelf.readingStatus.eq(readingStatus)
                .and(bookShelf.user.id.eq(userId)));

        List<BookShelf> bookShelves = jpaQuery.offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(extractOrderSpecifierFrom(bookShelf, pageable))
            .fetch();

        Long size = queryFactory.select(bookShelf.count())
            .from(bookShelf)
            .where(bookShelf.readingStatus.eq(readingStatus)
                .and(bookShelf.user.id.eq(userId)))
            .fetchOne();

        return new PageImpl<>(bookShelves, pageable, size);
    }

    @Override
    public BookShelf findOneOnConditionByUserIdAndBookId(Long userId, Long bookId,
        ReadingStatus readingStatus) {
        return Optional.ofNullable(queryFactory.select(bookShelf)
            .from(bookShelf).join(bookShelf.book, book).fetchJoin()
            .where(bookShelf.readingStatus.eq(readingStatus)
                .and(bookShelf.user.id.eq(userId))
                .and(bookShelf.book.id.eq(bookId)))
            .fetchOne()).orElseThrow(BookNotFoundException::new);

    }

    @Override
    public void deleteBookByUserIdAndBookId(Long userId, Long bookId) {
        queryFactory.delete(bookShelf)
            .where(bookShelf.user.id.eq(userId)
                .and(bookShelf.book.id.eq(bookId)))
            .execute();
    }

    @Override
    public Optional<BookShelf> findByUserIdAndBookId(Long userId, Long bookId) {
        return Optional.ofNullable(queryFactory.select(bookShelf)
            .from(bookShelf).join(bookShelf.book, book).fetchJoin()
            .leftJoin(bookShelf.bookReport, bookReport).fetchJoin()
            .where(bookShelf.user.id.eq(userId)
                .and(bookShelf.book.id.eq(bookId)))
            .fetchOne());
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        queryFactory.delete(bookShelf)
            .where(bookShelf.user.id.eq(userId)).execute();
    }

    @Override
    public Optional<BookShelf> findByUserIdAndIsbn(Long userId, String isbn) {
        return Optional.ofNullable(queryFactory.select(bookShelf)
            .from(bookShelf).join(bookShelf.book, book).fetchJoin()
            .where(bookShelf.user.id.eq(userId)
                .and(bookShelf.book.isbn.eq(isbn)))
            .fetchOne());
    }
}
