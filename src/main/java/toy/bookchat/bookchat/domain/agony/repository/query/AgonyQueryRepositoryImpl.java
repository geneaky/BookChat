package toy.bookchat.bookchat.domain.agony.repository.query;

import static toy.bookchat.bookchat.domain.agony.QAgony.agony;
import static toy.bookchat.bookchat.domain.bookshelf.QBookShelf.bookShelf;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.extractOrderSpecifierFrom;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.toSlice;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.agony.QAgony;

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
    public Slice<Agony> findUserBookShelfPageOfAgonies(long bookId, long userId,
        Pageable pageable, Optional<Long> postAgonyCursorId) {
        List<Agony> contents = queryFactory.select(agony)
            .from(agony)
            .join(agony.bookShelf, bookShelf).on(bookShelf.user.id.eq(userId)
                .and(bookShelf.book.id.eq(bookId)))
            .where(hasNextCursorId(postAgonyCursorId, pageable))
            .limit(pageable.getPageSize())
            .orderBy(extractOrderSpecifierFrom(agony, pageable))
            .fetch();

        return toSlice(contents, pageable);
    }

    private BooleanExpression hasNextCursorId(Optional<Long> postAgonyCursorId,
        Pageable pageable) {

        if (postAgonyCursorId.isPresent()) {
            Long agonyCursorId = postAgonyCursorId.get();
            return getSortedCursorExpression(pageable, agonyCursorId);
        }

        return null;
    }

    private BooleanExpression getSortedCursorExpression(Pageable pageable, Long agonyCursorId) {
        for (OrderSpecifier orderSpecifier : extractOrderSpecifierFrom(agony, pageable)) {
            if (isSameTargetPath(orderSpecifier, agony.id)) {
                return getSortedAgonyIdExpression(agonyCursorId, orderSpecifier);
            }
        }
        return null;
    }

    private BooleanExpression getSortedAgonyIdExpression(Long agonyCursorId,
        OrderSpecifier orderSpecifier) {
        if (orderSpecifier.isAscending()) {
            return agony.id.gt(agonyCursorId);
        }
        return agony.id.lt(agonyCursorId);
    }

    private boolean isSameTargetPath(OrderSpecifier orderSpecifier, NumberPath<Long> id) {
        if (isSamePath(orderSpecifier, id)) {
            return true;
        }
        return false;
    }

    private boolean isSamePath(OrderSpecifier orderSpecifier, NumberPath<Long> id) {
        return orderSpecifier.getTarget().equals(id);
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
