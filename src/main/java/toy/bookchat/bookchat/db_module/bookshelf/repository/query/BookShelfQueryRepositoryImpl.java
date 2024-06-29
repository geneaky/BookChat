package toy.bookchat.bookchat.db_module.bookshelf.repository.query;

import static toy.bookchat.bookchat.db_module.book.QBookEntity.bookEntity;
import static toy.bookchat.bookchat.db_module.bookreport.QBookReportEntity.bookReportEntity;
import static toy.bookchat.bookchat.db_module.bookshelf.QBookShelfEntity.bookShelfEntity;
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
import toy.bookchat.bookchat.db_module.bookshelf.BookShelfEntity;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;

@Repository
public class BookShelfQueryRepositoryImpl implements BookShelfQueryRepository {

    private final JPAQueryFactory queryFactory;

    public BookShelfQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<BookShelfEntity> findSpecificStatusBookByUserId(
        ReadingStatus readingStatus, Pageable pageable, Long userId) {

        JPAQuery<BookShelfEntity> jpaQuery = queryFactory.select(bookShelfEntity)
            .from(bookShelfEntity).join(bookShelfEntity.bookEntity, bookEntity).fetchJoin()
            .where(bookShelfEntity.readingStatus.eq(readingStatus)
                .and(bookShelfEntity.userEntity.id.eq(userId)));

        List<BookShelfEntity> bookShelves = jpaQuery.offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(extractOrderSpecifierFrom(bookShelfEntity, pageable))
            .fetch();

        Long size = queryFactory.select(bookShelfEntity.count())
            .from(bookShelfEntity)
            .where(bookShelfEntity.readingStatus.eq(readingStatus)
                .and(bookShelfEntity.userEntity.id.eq(userId)))
            .fetchOne();

        return new PageImpl<>(bookShelves, pageable, size);
    }

    @Override
    public void deleteBookShelfByIdAndUserId(Long bookShelfId, Long userId) {
        queryFactory.delete(bookShelfEntity)
            .where(bookShelfEntity.id.eq(bookShelfId)
                .and(bookShelfEntity.userEntity.id.eq(userId)))
            .execute();
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        queryFactory.delete(bookShelfEntity)
            .where(bookShelfEntity.userEntity.id.eq(userId)).execute();
    }

    @Override
    public Optional<BookShelfEntity> findByUserIdAndIsbnAndPublishAt(Long userId, String isbn,
        LocalDate publishAt) {
        return Optional.ofNullable(queryFactory.select(bookShelfEntity)
            .from(bookShelfEntity).join(bookShelfEntity.bookEntity, bookEntity).fetchJoin()
            .where(bookShelfEntity.userEntity.id.eq(userId)
                .and(bookShelfEntity.bookEntity.isbn.eq(isbn))
                .and(bookShelfEntity.bookEntity.publishAt.eq(publishAt)))
            .fetchOne());
    }

    @Override
    public Optional<BookShelfEntity> findByIdAndUserId(Long bookShelfId, Long userId) {
        return Optional.ofNullable(queryFactory.select(bookShelfEntity)
            .from(bookShelfEntity)
            .where(bookShelfEntity.id.eq(bookShelfId)
                .and(bookShelfEntity.userEntity.id.eq(userId)))
            .fetchOne());
    }

    @Override
    public Optional<BookShelfEntity> findWithReportByIdAndUserId(Long bookShelfId, Long userId) {
        return Optional.ofNullable(queryFactory.select(bookShelfEntity)
            .from(bookShelfEntity)
            .join(bookShelfEntity.bookReportEntity, bookReportEntity).fetchJoin()
            .where(bookShelfEntity.id.eq(bookShelfId)
                .and(bookShelfEntity.userEntity.id.eq(userId)))
            .fetchOne());

    }
}
