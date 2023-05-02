package toy.bookchat.bookchat.domain.agony.repository.query;

import static toy.bookchat.bookchat.domain.agony.QAgony.agony;
import static toy.bookchat.bookchat.domain.bookshelf.QBookShelf.bookShelf;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.extractOrderSpecifierFrom;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.numberBasedPagination;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.toSlice;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import toy.bookchat.bookchat.domain.agony.Agony;

@Repository
public class AgonyQueryRepositoryImpl implements AgonyQueryRepository {

    private final JPAQueryFactory queryFactory;

    public AgonyQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Optional<Agony> findUserBookShelfAgony(Long bookShelfId, Long agonyId, Long userId) {
        return Optional.ofNullable(queryFactory.select(agony)
            .from(agony)
            .join(agony.bookShelf, bookShelf)
            .on(bookShelf.id.eq(bookShelfId)
                .and(bookShelf.user.id.eq(userId)))
            .where(agony.id.eq(agonyId))
            .fetchOne());
    }

    @Override
    public Slice<Agony> findUserBookShelfSliceOfAgonies(Long bookShelfId, Long userId,
        Pageable pageable,
        Long postCursorId) {
        List<Agony> contents = queryFactory.select(agony)
            .from(agony)
            .join(agony.bookShelf, bookShelf)
            .on(bookShelf.id.eq(bookShelfId).and(bookShelf.user.id.eq(userId)))
            .where(numberBasedPagination(agony, agony.id, postCursorId, pageable))
            .limit(pageable.getPageSize())
            .orderBy(extractOrderSpecifierFrom(agony, pageable))
            .fetch();

        return toSlice(contents, pageable);
    }

    @Override
    public void deleteByAgoniesIds(Long bookShelfId, Long userId, List<Long> agoniesIds) {
        queryFactory.delete(agony)
            .where(agony.bookShelf.id.in(
                    JPAExpressions.select(bookShelf.id)
                        .from(bookShelf)
                        .where(bookShelf.user.id.eq(userId))),
                agony.id.in(agoniesIds)
            ).execute();
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        queryFactory.delete(agony)
            .where(agony.bookShelf.id.in(
                JPAExpressions.select(bookShelf.id)
                    .from(bookShelf)
                    .where(bookShelf.user.id.eq(userId))
            )).execute();
    }

    @Override
    public void deleteByBookShelfIdAndUserId(Long bookShelfId, Long userId) {
        queryFactory.delete(agony)
            .where(agony.bookShelf.id.in(
                JPAExpressions.select(bookShelf.id)
                    .from(bookShelf)
                    .where(bookShelf.id.eq(bookShelfId)
                        .and(bookShelf.user.id.eq(userId)))
            )).execute();
    }
}
