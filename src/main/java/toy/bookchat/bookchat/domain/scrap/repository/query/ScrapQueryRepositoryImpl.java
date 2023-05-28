package toy.bookchat.bookchat.domain.scrap.repository.query;

import static toy.bookchat.bookchat.domain.bookshelf.QBookShelf.bookShelf;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.toSlice;
import static toy.bookchat.bookchat.domain.scrap.QScrap.scrap;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import toy.bookchat.bookchat.domain.scrap.service.dto.response.ScrapResponse;

@Repository
public class ScrapQueryRepositoryImpl implements ScrapQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ScrapQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Slice<ScrapResponse> findScraps(Long bookShelfId, Long postCursorId, Pageable pageable,
        Long userId) {

        List<ScrapResponse> scrapList = queryFactory.select(
                Projections.constructor(ScrapResponse.class,
                    scrap.id, scrap.scrapContent))
            .from(scrap)
            .join(bookShelf)
            .on(scrap.bookShelf.id.eq(bookShelf.id).and(bookShelf.id.eq(bookShelfId))
                .and(bookShelf.user.id.eq(userId)))
            .where(gtCursorId(postCursorId))
            .limit(pageable.getPageSize())
            .orderBy(scrap.id.asc())
            .fetch();

        return toSlice(scrapList, pageable);
    }

    private BooleanExpression gtCursorId(Long cursorId) {
        return cursorId == null ? null : scrap.id.gt(cursorId);
    }
}
