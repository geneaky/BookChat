package toy.bookchat.bookchat.db_module.agony.repository.query;

import static toy.bookchat.bookchat.db_module.agony.QAgonyEntity.agonyEntity;
import static toy.bookchat.bookchat.domain.bookshelf.QBookShelfEntity.bookShelfEntity;
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
import toy.bookchat.bookchat.db_module.agony.AgonyEntity;

@Repository
public class AgonyQueryRepositoryImpl implements AgonyQueryRepository {

    private final JPAQueryFactory queryFactory;

    public AgonyQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Optional<AgonyEntity> findUserBookShelfAgony(Long bookShelfId, Long agonyId, Long userId) {
        return Optional.ofNullable(queryFactory.select(agonyEntity)
            .from(agonyEntity)
            .join(agonyEntity.bookShelfEntity, bookShelfEntity)
            .on(bookShelfEntity.id.eq(bookShelfId)
                .and(bookShelfEntity.userEntity.id.eq(userId)))
            .where(agonyEntity.id.eq(agonyId))
            .fetchOne());
    }

    @Override
    public Slice<AgonyEntity> findUserBookShelfSliceOfAgonies(Long bookShelfId, Long userId,
        Pageable pageable,
        Long postCursorId) {
        List<AgonyEntity> contents = queryFactory.select(agonyEntity)
            .from(agonyEntity)
            .join(agonyEntity.bookShelfEntity, bookShelfEntity)
            .on(bookShelfEntity.id.eq(bookShelfId).and(bookShelfEntity.userEntity.id.eq(userId)))
            .where(numberBasedPagination(agonyEntity, agonyEntity.id, postCursorId, pageable))
            .limit(pageable.getPageSize())
            .orderBy(extractOrderSpecifierFrom(agonyEntity, pageable))
            .fetch();

        return toSlice(contents, pageable);
    }

    @Override
    public void deleteByAgoniesIds(Long bookShelfId, Long userId, List<Long> agoniesIds) {
        queryFactory.delete(agonyEntity)
            .where(agonyEntity.bookShelfEntity.id.in(
                    JPAExpressions.select(bookShelfEntity.id)
                        .from(bookShelfEntity)
                        .where(bookShelfEntity.userEntity.id.eq(userId))),
                agonyEntity.id.in(agoniesIds)
            ).execute();
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        queryFactory.delete(agonyEntity)
            .where(agonyEntity.bookShelfEntity.id.in(
                JPAExpressions.select(bookShelfEntity.id)
                    .from(bookShelfEntity)
                    .where(bookShelfEntity.userEntity.id.eq(userId))
            )).execute();
    }

    @Override
    public void deleteByBookShelfIdAndUserId(Long bookShelfId, Long userId) {
        queryFactory.delete(agonyEntity)
            .where(agonyEntity.bookShelfEntity.id.in(
                JPAExpressions.select(bookShelfEntity.id)
                    .from(bookShelfEntity)
                    .where(bookShelfEntity.id.eq(bookShelfId)
                        .and(bookShelfEntity.userEntity.id.eq(userId)))
            )).execute();
    }
}
