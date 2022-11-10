package toy.bookchat.bookchat.domain.agony.repository.query;

import static toy.bookchat.bookchat.domain.agony.QAgony.agony;
import static toy.bookchat.bookchat.domain.agony.QAgonyRecord.agonyRecord;
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
import toy.bookchat.bookchat.domain.agony.AgonyRecord;
import toy.bookchat.bookchat.domain.agony.QAgonyRecord;
import toy.bookchat.bookchat.exception.NotSupportedPagingConditionException;

@Repository
public class AgonyRecordQueryRepositoryImpl implements AgonyRecordQueryRepository {

    private final JPAQueryFactory queryFactory;

    public AgonyRecordQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }


    @Override
    public Slice<AgonyRecord> findSliceOfUserAgonyRecords(Long bookId, Long agonyId, Long userId,
        Pageable pageable, Optional<Long> postRecordCursorId) {
        List<AgonyRecord> contents = queryFactory.select(agonyRecord)
            .from(agonyRecord)
            .join(agonyRecord.agony, agony).on(agony.id.eq(agonyId))
            .join(agony.bookShelf, bookShelf).on(bookShelf.book.id.eq(bookId)
                .and(bookShelf.user.id.eq(userId)))
            .where(conditionalNextCursorId(postRecordCursorId, pageable))
            .limit(pageable.getPageSize())
            .orderBy(extractOrderSpecifierFrom(agonyRecord, pageable))
            .fetch();

        return toSlice(contents, pageable);
    }

    private BooleanExpression conditionalNextCursorId(Optional<Long> postRecordCursorId,
        Pageable pageable) {

        return postRecordCursorId.map(
                recordCursorId -> getSortedCursorExpression(pageable, recordCursorId))
            .orElse(null);

    }

    private BooleanExpression getSortedCursorExpression(Pageable pageable, Long recordCursorId) {
        for (OrderSpecifier orderSpecifier : extractOrderSpecifierFrom(agonyRecord, pageable)) {
            if (isSameTargetPath(orderSpecifier, agonyRecord.id)) {
                return getSortedAgonyIdExpression(recordCursorId, orderSpecifier);
            }
        }

        throw new NotSupportedPagingConditionException();
    }

    private BooleanExpression getSortedAgonyIdExpression(Long recordCursorId,
        OrderSpecifier orderSpecifier) {
        if (orderSpecifier.isAscending()) {
            return agonyRecord.id.gt(recordCursorId);
        }
        return agonyRecord.id.lt(recordCursorId);
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
    public void deleteAgony(Long userId, Long bookId, Long agonyId, Long recordId) {
        QAgonyRecord subAgonyRecord = new QAgonyRecord("subAgonyRecord");
        queryFactory.delete(agonyRecord)
            .where(agonyRecord.id.eq(
                JPAExpressions.select(subAgonyRecord.id)
                    .from(subAgonyRecord)
                    .join(subAgonyRecord.agony, agony).on(agony.id.eq(agonyId))
                    .join(agony.bookShelf, bookShelf).on(bookShelf.user.id.eq(userId)
                        .and(bookShelf.book.id.eq(bookId)))
                    .where(subAgonyRecord.id.eq(recordId))
            )).execute();
    }

    @Override
    public void reviseAgonyRecord(Long userId, Long bookId, Long agonyId,
        Long recordId, String recordTitle, String recordContent) {
        QAgonyRecord subAgonyRecord = new QAgonyRecord("subAgonyRecord");
        queryFactory.update(agonyRecord)
            .set(agonyRecord.title, recordTitle)
            .set(agonyRecord.content, recordContent)
            .where(agonyRecord.id.eq(
                JPAExpressions.select(subAgonyRecord.id)
                    .from(subAgonyRecord)
                    .join(subAgonyRecord.agony, agony).on(agony.id.eq(agonyId))
                    .join(agony.bookShelf, bookShelf).on(bookShelf.user.id.eq(userId)
                        .and(bookShelf.book.id.eq(bookId)))
                    .where(subAgonyRecord.id.eq(recordId))
            )).execute();
    }

    @Override
    public void deleteByAgoniesIds(Long bookId, Long userId, List<Long> agoniesIds) {
        queryFactory.delete(agonyRecord)
            .where(agonyRecord.agony.id.in(
                JPAExpressions.select(agony.id)
                    .from(agony)
                    .join(agony.bookShelf, bookShelf)
                    .on(bookShelf.book.id.eq(bookId).and(bookShelf.user.id.eq(userId)))
                    .where(agony.id.in(agoniesIds))
            )).execute();
    }

}
