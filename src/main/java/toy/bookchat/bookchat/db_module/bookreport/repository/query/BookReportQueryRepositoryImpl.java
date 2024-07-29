package toy.bookchat.bookchat.db_module.bookreport.repository.query;

import static toy.bookchat.bookchat.db_module.bookreport.QBookReportEntity.bookReportEntity;
import static toy.bookchat.bookchat.db_module.bookshelf.QBookShelfEntity.bookShelfEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import toy.bookchat.bookchat.db_module.bookreport.BookReportEntity;

@Repository
public class BookReportQueryRepositoryImpl implements BookReportQueryRepository {

  private final JPAQueryFactory queryFactory;

  public BookReportQueryRepositoryImpl(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  @Override
  public BookReportEntity findByUserIdAndBookShelfId(Long userId, Long bookShelfId) {
    return queryFactory.select(bookReportEntity)
        .from(bookReportEntity)
        .join(bookShelfEntity).on(bookReportEntity.bookShelfId.eq(bookShelfEntity.id))
        .where(bookReportEntity.bookShelfId.eq(bookShelfId))
        .fetchOne();
  }
}
