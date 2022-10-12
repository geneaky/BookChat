package toy.bookchat.bookchat.domain.bookshelf.repository.query;

import static toy.bookchat.bookchat.domain.bookshelf.QBookShelf.bookShelf;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;

@Repository
public class BookShelfQueryRepositoryImpl extends QuerydslRepositorySupport implements
    BookShelfQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public BookShelfQueryRepositoryImpl() {
        super(BookShelf.class);
        this.jpaQueryFactory = new JPAQueryFactory(getEntityManager());
    }

    @Override
    public List<BookShelf> findSpecificStatusBookByUserId(
        ReadingStatus readingStatus, Pageable pageable, Long userId) {

        JPQLQuery<BookShelf> jpqlQuery = from(bookShelf)
            .where(bookShelf.readingStatus.eq(readingStatus));

        return getQuerydsl().applyPagination(pageable, jpqlQuery).fetch();

    }
}
