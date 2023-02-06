package toy.bookchat.bookchat.domain.agonyrecord.repository.query;

import static toy.bookchat.bookchat.domain.agony.QAgony.agony;
import static toy.bookchat.bookchat.domain.agonyrecord.QAgonyRecord.agonyRecord;
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
import toy.bookchat.bookchat.domain.agonyrecord.AgonyRecord;
import toy.bookchat.bookchat.domain.agonyrecord.QAgonyRecord;

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
        QAgonyRecord subAgonyRecord = new QAgonyRecord("subAgonyRecord");
        queryFactory.delete(agonyRecord)
            .where(agonyRecord.id.eq(
                JPAExpressions.select(subAgonyRecord.id)
                    .from(subAgonyRecord)
                    .join(subAgonyRecord.agony, agony).on(agony.id.eq(agonyId))
                    .join(agony.bookShelf, bookShelf).on(bookShelf.id.eq(bookShelfId)
                        .and(bookShelf.user.id.eq(userId)))
                    .where(subAgonyRecord.id.eq(recordId))
            )).execute();
    }

    @Override
    public void reviseAgonyRecord(Long bookShelfId, Long agonyId, Long recordId, Long userId,
        String recordTitle,
        String recordContent) {
        QAgonyRecord subAgonyRecord = new QAgonyRecord("subAgonyRecord");
        queryFactory.update(agonyRecord)
            .set(agonyRecord.title, recordTitle)
            .set(agonyRecord.content, recordContent)
            .where(agonyRecord.id.eq(
                JPAExpressions.select(subAgonyRecord.id)
                    .from(subAgonyRecord)
                    .join(subAgonyRecord.agony, agony).on(agony.id.eq(agonyId))
                    .join(agony.bookShelf, bookShelf).on(bookShelf.id.eq(bookShelfId)
                        .and(bookShelf.user.id.eq(userId)))
                    .where(subAgonyRecord.id.eq(recordId))
            )).execute();
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

}
