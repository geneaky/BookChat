package toy.bookchat.bookchat.domain.agony.repository;

import static toy.bookchat.bookchat.domain.agony.QAgony.agony;
import static toy.bookchat.bookchat.domain.agony.QAgonyRecord.agonyRecord;
import static toy.bookchat.bookchat.domain.bookshelf.QBookShelf.bookShelf;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.extractOrderSpecifierFrom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import toy.bookchat.bookchat.domain.agony.AgonyRecord;

@Repository
public class AgonyRecordQueryRepositoryImpl implements AgonyRecordQueryRepository {

    private final JPAQueryFactory queryFactory;

    public AgonyRecordQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }


    @Override
    public Page<AgonyRecord> findPageOfUserAgonyRecords(Long bookId, Long agonyId, Long userId,
        Pageable pageable) {
        List<AgonyRecord> contents = queryFactory.select(agonyRecord)
            .from(agonyRecord)
            .join(agonyRecord.agony, agony).on(agony.id.eq(agonyId))
            .join(agony.bookShelf, bookShelf).on(bookShelf.book.id.eq(bookId)
                .and(bookShelf.user.id.eq(userId)))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(extractOrderSpecifierFrom(agonyRecord, pageable))
            .fetch();

        return new PageImpl<>(contents, pageable, contents.size());
    }
}
