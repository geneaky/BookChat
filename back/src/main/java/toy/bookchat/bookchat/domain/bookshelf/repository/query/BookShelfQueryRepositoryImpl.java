package toy.bookchat.bookchat.domain.bookshelf.repository.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;

@Repository
@RequiredArgsConstructor
public class BookShelfQueryRepositoryImpl implements
    BookShelfQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<BookShelf> findSpecificStatusBookByUserId(
        ReadingStatus readingStatus, Pageable pageable, Long userId) {

        jpaQueryFactory.
    }
}
