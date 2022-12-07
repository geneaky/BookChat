package toy.bookchat.bookchat.domain.agonyrecord.repository.query;

import static toy.bookchat.bookchat.domain.agony.QAgony.agony;
import static toy.bookchat.bookchat.domain.agonyrecord.QAgonyRecord.agonyRecord;
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
import toy.bookchat.bookchat.domain.agonyrecord.AgonyRecord;
import toy.bookchat.bookchat.domain.agonyrecord.QAgonyRecord;

@Repository
public class AgonyRecordQueryRepositoryImpl implements AgonyRecordQueryRepository {

    private final JPAQueryFactory queryFactory;

    public AgonyRecordQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }


    @Override
    public Slice<AgonyRecord> findSliceOfUserAgonyRecords(Long agonyId, Long userId,
        Pageable pageable, Optional<Long> postCursorId) {
        List<AgonyRecord> contents = queryFactory.select(agonyRecord)
            .from(agonyRecord)
            .join(agonyRecord.agony, agony).on(agony.id.eq(agonyId))
            .join(agony.user, user).on(user.id.eq(userId))
            .where(numberBasedPagination(agonyRecord, agonyRecord.id, postCursorId, pageable))
            .limit(pageable.getPageSize())
            .orderBy(extractOrderSpecifierFrom(agonyRecord, pageable))
            .fetch();

        return toSlice(contents, pageable);
    }

    @Override
    public void deleteAgony(Long userId, Long agonyId, Long recordId) {
        QAgonyRecord subAgonyRecord = new QAgonyRecord("subAgonyRecord");
        queryFactory.delete(agonyRecord)
            .where(agonyRecord.id.eq(
                JPAExpressions.select(subAgonyRecord.id)
                    .from(subAgonyRecord)
                    .join(subAgonyRecord.agony, agony).on(agony.id.eq(agonyId))
                    .join(agony.user, user).on(user.id.eq(userId))
                    .where(subAgonyRecord.id.eq(recordId))
            )).execute();
    }

    @Override
    public void reviseAgonyRecord(Long userId, Long agonyId, Long recordId, String recordTitle,
        String recordContent) {
        QAgonyRecord subAgonyRecord = new QAgonyRecord("subAgonyRecord");
        queryFactory.update(agonyRecord)
            .set(agonyRecord.title, recordTitle)
            .set(agonyRecord.content, recordContent)
            .where(agonyRecord.id.eq(
                JPAExpressions.select(subAgonyRecord.id)
                    .from(subAgonyRecord)
                    .join(subAgonyRecord.agony, agony).on(agony.id.eq(agonyId))
                    .join(agony.user, user).on(user.id.eq(userId))
                    .where(subAgonyRecord.id.eq(recordId))
            )).execute();
    }

    @Override
    public void deleteByAgoniesIds(Long userId, List<Long> agoniesIds) {
        queryFactory.delete(agonyRecord)
            .where(agonyRecord.agony.id.in(
                JPAExpressions.select(agony.id)
                    .from(agony)
                    .join(agony.user, user)
                    .on(user.id.eq(userId))
                    .where(agony.id.in(agoniesIds))
            )).execute();
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        queryFactory.delete(agonyRecord)
            .where(agonyRecord.agony.id.in(
                JPAExpressions.select(agony.id)
                    .from(agony)
                    .where(agony.user.id.eq(userId))
            )).execute();
    }

}
