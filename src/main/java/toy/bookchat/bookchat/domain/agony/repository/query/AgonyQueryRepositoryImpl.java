package toy.bookchat.bookchat.domain.agony.repository.query;

import static toy.bookchat.bookchat.domain.agony.QAgony.agony;
import static toy.bookchat.bookchat.domain.bookshelf.QBookShelf.bookShelf;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.extractOrderSpecifierFrom;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.toSlice;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.agony.QAgony;
import toy.bookchat.bookchat.exception.NotSupportedPagingConditionException;

@Repository
public class AgonyQueryRepositoryImpl implements AgonyQueryRepository {

    private final JPAQueryFactory queryFactory;

    public AgonyQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Optional<Agony> findUserBookShelfAgony(Long userId, Long bookId, Long agonyId) {
        return Optional.ofNullable(queryFactory.select(agony)
            .from(agony)
            .join(agony.bookShelf, bookShelf).on(bookShelf.user.id.eq(userId)
                .and(bookShelf.book.id.eq(bookId)))
            .where(agony.id.eq(agonyId))
            .fetchOne());
    }

    @Override
    public Slice<Agony> findUserBookShelfSliceOfAgonies(long bookId, long userId,
        Pageable pageable, Optional<Long> postAgonyCursorId) {
        List<Agony> contents = queryFactory.select(agony)
            .from(agony)
            .join(agony.bookShelf, bookShelf).on(bookShelf.user.id.eq(userId)
                .and(bookShelf.book.id.eq(bookId)))
            .where(conditionalNextCursorId(postAgonyCursorId, pageable))
            .limit(pageable.getPageSize())
            .orderBy(extractOrderSpecifierFrom(agony, pageable))
            .fetch();

        return toSlice(contents, pageable);
    }

    private BooleanExpression conditionalNextCursorId(Optional<Long> postAgonyCursorId,
        Pageable pageable) {

        return postAgonyCursorId.map(
                agonyCursorId -> getSortedCursorExpression(pageable, agonyCursorId))
            .orElse(null);

    }

    private BooleanExpression getSortedCursorExpression(Pageable pageable, Long agonyCursorId) {
        for (OrderSpecifier orderSpecifier : extractOrderSpecifierFrom(agony, pageable)) {
            if (isSameTargetPath(orderSpecifier, agony.id)) {
                return getSortedAgonyIdExpression(agonyCursorId, orderSpecifier);
            }
        }

        throw new NotSupportedPagingConditionException();
    }

    private BooleanExpression getSortedAgonyIdExpression(Long agonyCursorId,
        OrderSpecifier orderSpecifier) {
        if (orderSpecifier.isAscending()) {
            return agony.id.gt(agonyCursorId);
        }
        return agony.id.lt(agonyCursorId);
    }

    private boolean isSameTargetPath(OrderSpecifier orderSpecifier, Path<?> path) {
        if (isSamePath(orderSpecifier, path)) {
            return true;
        }
        return false;
    }

    private boolean isSamePath(OrderSpecifier orderSpecifier, Path<?> path) {
        return orderSpecifier.getTarget().equals(path);
    }


    @Override
    public void deleteByAgoniesIds(Long bookId, Long userId, List<Long> agoniesIds) {
        QAgony subAgony = new QAgony("subAgony");

        queryFactory.delete(agony)
            .where(agony.id.in(
                JPAExpressions.select(subAgony.id)
                    .from(subAgony)
                    .join(subAgony.bookShelf, bookShelf)
                    .on(bookShelf.book.id.eq(bookId).and(bookShelf.user.id.eq(userId)))
                    .where(subAgony.id.in(agoniesIds))
            )).execute();
    }
}
