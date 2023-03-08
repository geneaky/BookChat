package toy.bookchat.bookchat.domain.agony.repository.query;

import static toy.bookchat.bookchat.domain.agony.QAgony.agony;
import static toy.bookchat.bookchat.domain.agony.QAgonyRecord.agonyRecord;
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
import toy.bookchat.bookchat.domain.agony.AgonyRecord;

@Repository
public class AgonyRecordQueryRepositoryImpl implements AgonyRecordQueryRepository {

    private final JPAQueryFactory queryFactory;

    public AgonyRecordQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Slice<AgonyRecord> findSliceOfUserAgonyRecords(Long bookShelfId, Long agonyId,
        Long userId,
        Pageable pageable, Optional<Long> postCursorId) {
        List<AgonyRecord> contents = queryFactory.select(agonyRecord)
            .from(agonyRecord)
            .join(agonyRecord.agony, agony).on(agony.id.eq(agonyId))
            .join(agony.bookShelf, bookShelf).on(bookShelf.id.eq(bookShelfId)
                .and(bookShelf.user.id.eq(userId)))
            .where(numberBasedPagination(agonyRecord, agonyRecord.id, postCursorId, pageable))
            .limit(pageable.getPageSize())
            .orderBy(extractOrderSpecifierFrom(agonyRecord, pageable))
            .fetch();

        return toSlice(contents, pageable);
    }

    @Override
    public void deleteAgonyRecord(Long bookShelfId, Long agonyId, Long recordId, Long userId) {
        queryFactory.delete(agonyRecord)
            .where(agonyRecord.id.eq(
                fetchCorrespondedAgonyRecordId(bookShelfId, agonyId, recordId, userId))
            ).execute();
    }

    @Override
    public void reviseAgonyRecord(Long bookShelfId, Long agonyId, Long recordId, Long userId,
        String recordTitle, String recordContent) {
        queryFactory.update(agonyRecord)
            .set(agonyRecord.title, recordTitle)
            .set(agonyRecord.content, recordContent)
            .where(agonyRecord.id.eq(
                fetchCorrespondedAgonyRecordId(bookShelfId, agonyId, recordId, userId)))
            .execute();
    }

    private Long fetchCorrespondedAgonyRecordId(Long bookShelfId, Long agonyId, Long recordId,
        Long userId) {
        /* TODO: 2023-02-13 mysql error:1093 insert, update, delete시 같은 테이블에서
            서브쿼리를 가져오는 경우 발생하는 문제로 where절 조건을 subquery로 한 번 더 래핑해서
            from절 서브쿼리를 통해 해결할 수 있지만 querydsl from절 서브쿼리가 불가능하기 때문에
             쿼리를 분리
             추후 이부분 성능 문제시 mybatis나 jdbc template으로 변경
         */
        return queryFactory.select(agonyRecord.id)
            .from(agonyRecord)
            .join(agonyRecord.agony, agony).on(agony.id.eq(agonyId))
            .join(agony.bookShelf, bookShelf).on(bookShelf.id.eq(bookShelfId)
                .and(bookShelf.user.id.eq(userId)))
            .where(agonyRecord.id.eq(recordId))
            .fetchOne();
    }

    @Override
    public void deleteByAgoniesIds(Long bookShelfId, Long userId, List<Long> agoniesIds) {
        queryFactory.delete(agonyRecord)
            .where(agonyRecord.agony.id.in(
                JPAExpressions.select(agony.id)
                    .from(agony)
                    .join(agony.bookShelf, bookShelf)
                    .on(bookShelf.id.eq(bookShelfId)
                        .and(bookShelf.user.id.eq(userId)))
                    .where(agony.id.in(agoniesIds))
            )).execute();
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        queryFactory.delete(agonyRecord)
            .where(agonyRecord.agony.id.in(
                JPAExpressions.select(agony.id)
                    .from(agony)
                    .join(agony.bookShelf, bookShelf)
                    .on(bookShelf.user.id.eq(userId))
            )).execute();
    }

    @Override
    public void deleteByBookShelfIdAndUserId(Long bookShelfId, Long userId) {
        queryFactory.delete(agonyRecord)
            .where(agonyRecord.agony.id.in(
                JPAExpressions.select(agony.id)
                    .from(agony)
                    .join(agony.bookShelf, bookShelf)
                    .on(bookShelf.id.eq(bookShelfId)
                        .and(bookShelf.user.id.eq(userId)))
            )).execute();
    }

}
