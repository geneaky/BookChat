package toy.bookchat.bookchat.domain.bookshelf.repository.query;

import static toy.bookchat.bookchat.domain.bookshelf.QBookShelf.bookShelf;
import static toy.bookchat.bookchat.domain.user.QUser.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
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
    public List<BookShelf> findSpecificStatusBookByUserId(
        ReadingStatus readingStatus, Pageable pageable, Long userId) {

        return queryFactory.select(bookShelf)
            .from(bookShelf)
            .join(bookShelf.user, user).on(user.id.eq(userId))
            .where(bookShelf.readingStatus.eq(readingStatus))
//                .and(bookShelf.user.id.eq(userId)))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }
}
