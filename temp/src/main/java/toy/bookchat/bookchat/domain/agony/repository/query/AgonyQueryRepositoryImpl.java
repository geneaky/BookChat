package toy.bookchat.bookchat.domain.agony.repository.query;

import static toy.bookchat.bookchat.domain.agony.QAgony.agony;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.extractOrderSpecifierFrom;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.numberBasedPagination;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.toSlice;
import static toy.bookchat.bookchat.domain.user.QUser.user;

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
    public Optional<Agony> findUserBookShelfAgony(Long userId, Long agonyId) {
        return Optional.ofNullable(queryFactory.select(agony)
            .from(agony)
            .join(agony.user, user).on(user.id.eq(userId))
            .where(agony.id.eq(agonyId))
            .fetchOne());
    }

    @Override
    public Slice<Agony> findUserBookShelfSliceOfAgonies(long userId, Pageable pageable,
        Optional<Long> postAgonyCursorId) {
        List<Agony> contents = queryFactory.select(agony)
            .from(agony)
            .join(agony.user, user).on(user.id.eq(userId))
            .where(numberBasedPagination(agony, agony.id, postAgonyCursorId, pageable))
            .limit(pageable.getPageSize())
            .orderBy(extractOrderSpecifierFrom(agony, pageable))
            .fetch();

        return toSlice(contents, pageable);
    }


    @Override
    public void deleteByAgoniesIds(Long userId, List<Long> agoniesIds) {
        QAgony subAgony = new QAgony("subAgony");

        queryFactory.delete(agony)
            .where(agony.id.in(
                JPAExpressions.select(subAgony.id)
                    .from(subAgony)
                    .join(subAgony.user, user)
                    .on(user.id.eq(userId))
                    .where(subAgony.id.in(agoniesIds))
            )).execute();
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        queryFactory.delete(agony)
            .where(agony.user.id.eq(userId)).execute();
    }
}
