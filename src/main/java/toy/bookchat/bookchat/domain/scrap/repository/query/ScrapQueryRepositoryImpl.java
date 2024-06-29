package toy.bookchat.bookchat.domain.scrap.repository.query;

import static toy.bookchat.bookchat.domain.bookshelf.QBookShelfEntity.bookShelfEntity;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.toSlice;
import static toy.bookchat.bookchat.domain.scrap.QScrapEntity.scrapEntity;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import toy.bookchat.bookchat.domain.scrap.ScrapEntity;
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
                    scrapEntity.id, scrapEntity.scrapContent))
            .from(scrapEntity)
            .join(bookShelfEntity)
            .on(scrapEntity.bookShelfEntity.id.eq(bookShelfEntity.id).and(bookShelfEntity.id.eq(bookShelfId))
                .and(bookShelfEntity.userEntity.id.eq(userId)))
            .where(gtCursorId(postCursorId))
            .limit(pageable.getPageSize())
            .orderBy(scrapEntity.id.asc())
            .fetch();

        return toSlice(scrapList, pageable);
    }

    @Override
    public Optional<ScrapEntity> findUserScrap(Long scrapId, Long userId) {
        return Optional.ofNullable(
            queryFactory.select(scrapEntity)
                .from(scrapEntity)
                .join(bookShelfEntity).on(scrapEntity.bookShelfEntity.id.eq(bookShelfEntity.id).and(bookShelfEntity.userEntity.id.eq(userId)))
                .where(scrapEntity.id.eq(scrapId))
                .fetchOne()
        );
    }

    private BooleanExpression gtCursorId(Long cursorId) {
        return cursorId == null ? null : scrapEntity.id.gt(cursorId);
    }
}
