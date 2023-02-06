package toy.bookchat.bookchat.domain.bookshelf.repository.query;

import static toy.bookchat.bookchat.domain.book.QBook.book;
import static toy.bookchat.bookchat.domain.bookreport.QBookReport.bookReport;
import static toy.bookchat.bookchat.domain.bookshelf.QBookShelf.bookShelf;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.extractOrderSpecifierFrom;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;

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
    public void deleteBookShelfByIdAndUserId(Long bookShelfId, Long userId) {
        queryFactory.delete(bookShelf)
            .where(bookShelf.id.eq(bookShelfId)
                .and(bookShelf.user.id.eq(userId)))
            .execute();
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        queryFactory.delete(bookShelf)
            .where(bookShelf.user.id.eq(userId)).execute();
    }

    @Override
    public Optional<BookShelf> findByUserIdAndIsbnAndPublishAt(Long userId, String isbn,
        LocalDate publishAt) {
        return Optional.ofNullable(queryFactory.select(bookShelf)
            .from(bookShelf).join(bookShelf.book, book).fetchJoin()
            .where(bookShelf.user.id.eq(userId)
                .and(bookShelf.book.isbn.eq(isbn))
                .and(bookShelf.book.publishAt.eq(publishAt)))
            .fetchOne());
    }

    @Override
    public Optional<BookShelf> findByIdAndUserId(Long bookShelfId, Long userId) {
        return Optional.ofNullable(queryFactory.select(bookShelf)
            .from(bookShelf)
            .where(bookShelf.id.eq(bookShelfId)
                .and(bookShelf.user.id.eq(userId)))
            .fetchOne());
    }

    @Override
    public Optional<BookShelf> findWithReportByIdAndUserId(Long bookShelfId, Long userId) {
        return Optional.ofNullable(queryFactory.select(bookShelf)
            .from(bookShelf)
            .join(bookShelf.bookReport, bookReport).fetchJoin()
            .where(bookShelf.id.eq(bookShelfId)
                .and(bookShelf.user.id.eq(userId)))
            .fetchOne());

    }
}
