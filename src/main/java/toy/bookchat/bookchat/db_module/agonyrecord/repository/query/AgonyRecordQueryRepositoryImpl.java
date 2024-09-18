package toy.bookchat.bookchat.db_module.agonyrecord.repository.query;

import static toy.bookchat.bookchat.db_module.agony.QAgonyEntity.agonyEntity;
import static toy.bookchat.bookchat.db_module.agonyrecord.QAgonyRecordEntity.agonyRecordEntity;
import static toy.bookchat.bookchat.db_module.bookshelf.QBookShelfEntity.bookShelfEntity;
import static toy.bookchat.bookchat.support.RepositorySupport.extractOrderSpecifierFrom;
import static toy.bookchat.bookchat.support.RepositorySupport.numberBasedPagination;
import static toy.bookchat.bookchat.support.RepositorySupport.toSlice;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import toy.bookchat.bookchat.db_module.agonyrecord.AgonyRecordEntity;

@Repository
public class AgonyRecordQueryRepositoryImpl implements AgonyRecordQueryRepository {

  private final JPAQueryFactory queryFactory;

  public AgonyRecordQueryRepositoryImpl(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  @Override
  public Optional<AgonyRecordEntity> findUserAgonyRecord(Long bookShelfId, Long agonyId, Long recordId, Long userId) {
    return Optional.ofNullable(queryFactory.select(agonyRecordEntity)
        .from(agonyRecordEntity)
        .join(agonyEntity).on(agonyRecordEntity.agonyId.eq(agonyEntity.id)
            .and(agonyEntity.id.eq(agonyId)))
        .join(bookShelfEntity).on(agonyEntity.bookShelfId.eq(bookShelfEntity.id)
            .and(bookShelfEntity.id.eq(bookShelfId))
            .and(bookShelfEntity.userId.eq(userId)))
        .where(agonyRecordEntity.id.eq(recordId))
        .fetchOne());
  }


  @Override
  public Slice<AgonyRecordEntity> findSliceOfUserAgonyRecords(Long bookShelfId, Long agonyId, Long userId,
      Pageable pageable, Long postCursorId) {
    List<AgonyRecordEntity> contents = queryFactory.select(agonyRecordEntity)
        .from(agonyRecordEntity)
        .join(agonyEntity).on(agonyRecordEntity.agonyId.eq(agonyEntity.id)
            .and(agonyEntity.id.eq(agonyId)))
        .join(bookShelfEntity).on(agonyEntity.bookShelfId.eq(bookShelfEntity.id)
            .and(bookShelfEntity.id.eq(bookShelfId))
            .and(bookShelfEntity.userId.eq(userId)))
        .where(numberBasedPagination(agonyRecordEntity, agonyRecordEntity.id, postCursorId, pageable))
        .limit(pageable.getPageSize())
        .orderBy(extractOrderSpecifierFrom(agonyRecordEntity, pageable))
        .fetch();

    return toSlice(contents, pageable);
  }

  @Override
  public void deleteAgonyRecord(Long bookShelfId, Long agonyId, Long recordId, Long userId) {
    queryFactory.delete(agonyRecordEntity)
        .where(agonyRecordEntity.id.eq(fetchCorrespondedAgonyRecordId(bookShelfId, agonyId, recordId, userId)))
        .execute();
  }

  @Override
  public void reviseAgonyRecord(Long bookShelfId, Long agonyId, Long recordId, Long userId, String recordTitle,
      String recordContent) {
    queryFactory.update(agonyRecordEntity)
        .set(agonyRecordEntity.title, recordTitle)
        .set(agonyRecordEntity.content, recordContent)
        .where(agonyRecordEntity.id.eq(fetchCorrespondedAgonyRecordId(bookShelfId, agonyId, recordId, userId)))
        .execute();
  }

  private Long fetchCorrespondedAgonyRecordId(Long bookShelfId, Long agonyId, Long recordId, Long userId) {
    return queryFactory.select(agonyRecordEntity.id)
        .from(agonyRecordEntity)
        .join(agonyEntity).on(agonyRecordEntity.agonyId.eq(agonyEntity.id)
            .and(agonyEntity.id.eq(agonyId)))
        .join(bookShelfEntity).on(agonyEntity.bookShelfId.eq(bookShelfEntity.id)
            .and(bookShelfEntity.id.eq(bookShelfId))
            .and(bookShelfEntity.userId.eq(userId)))
        .where(agonyRecordEntity.id.eq(recordId))
        .fetchOne();
  }

  @Override
  public void deleteByAgoniesIds(Long bookShelfId, Long userId, List<Long> agoniesIds) {
    queryFactory.delete(agonyRecordEntity)
        .where(agonyRecordEntity.agonyId.in(
            JPAExpressions.select(agonyEntity.id)
                .from(agonyEntity)
                .join(bookShelfEntity)
                .on(agonyEntity.bookShelfId.eq(bookShelfEntity.id)
                    .and(bookShelfEntity.id.eq(bookShelfId))
                    .and(bookShelfEntity.userId.eq(userId)))
                .where(agonyEntity.id.in(agoniesIds))
        )).execute();
  }

  @Override
  public void deleteAllByUserId(Long userId) {
    queryFactory.delete(agonyRecordEntity)
        .where(agonyRecordEntity.agonyId.in(
            JPAExpressions.select(agonyEntity.id)
                .from(agonyEntity)
                .join(bookShelfEntity)
                .on(agonyEntity.bookShelfId.eq(bookShelfEntity.id)
                    .and(bookShelfEntity.userId.eq(userId)))
        )).execute();
  }

  @Override
  public void deleteByBookShelfIdAndUserId(Long bookShelfId, Long userId) {
    queryFactory.delete(agonyRecordEntity)
        .where(agonyRecordEntity.agonyId.in(
            JPAExpressions.select(agonyEntity.id)
                .from(agonyEntity)
                .join(bookShelfEntity)
                .on(agonyEntity.bookShelfId.eq(bookShelfEntity.id)
                    .and(bookShelfEntity.id.eq(bookShelfId))
                    .and(bookShelfEntity.userId.eq(userId)))
        )).execute();
  }

}
