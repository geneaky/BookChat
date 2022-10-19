package toy.bookchat.bookchat.domain.bookshelf.repository.query;

import static toy.bookchat.bookchat.domain.book.QBook.book;
import static toy.bookchat.bookchat.domain.bookshelf.QBookShelf.bookShelf;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import toy.bookchat.bookchat.domain.book.exception.BookNotFoundException;
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
            .from(bookShelf)
            .where(bookShelf.readingStatus.eq(readingStatus)
                .and(bookShelf.user.id.eq(userId)));

        List<BookShelf> bookShelves = jpaQuery.offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long size = queryFactory.select(bookShelf.count())
            .from(bookShelf)
            .where(bookShelf.readingStatus.eq(readingStatus)
                .and(bookShelf.user.id.eq(userId)))
            .fetchOne();

        return new PageImpl<>(bookShelves, pageable, size);
    }

    @Override
    public BookShelf findReadingBookByUserIdAndIsbn(Long userId, String isbn) {
        return Optional.ofNullable(queryFactory.select(bookShelf)
            .from(bookShelf).innerJoin(bookShelf.book, book).fetchJoin()
            .where(bookShelf.user.id.eq(userId)
                .and(bookShelf.readingStatus.eq(ReadingStatus.READING))
                .and(bookShelf.book.isbn.eq(isbn)))
            .fetchOne()).orElseThrow(() -> {
            throw new BookNotFoundException("Can't Find Book On BookShelf");
        });
    }

    @Override
    public void deleteBookByUserIdAndIsbn(Long userId, String isbn) {
        queryFactory.delete(bookShelf)
            .where(bookShelf.user.id.eq(userId)
                .and(bookShelf.book.id.eq(
                    JPAExpressions.select(book.id)
                        .from(book)
                        .where(book.isbn.eq(isbn))
                )))
            .execute();
    }
}
