package toy.bookchat.bookchat.domain.agony.repository.query;

import static toy.bookchat.bookchat.domain.agony.QAgony.agony;
import static toy.bookchat.bookchat.domain.bookshelf.QBookShelf.bookShelf;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.extractOrderSpecifierFrom;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
    public Optional<Agony> findUserBookShelfAgony(Long userId, Long bookId, Long agonyId) {
        return Optional.ofNullable(queryFactory.select(agony)
            .from(agony)
            .join(agony.bookShelf, bookShelf).on(bookShelf.user.id.eq(userId)
                .and(bookShelf.book.id.eq(bookId)))
            .where(agony.id.eq(agonyId))
            .fetchOne());
    }

    @Override
    public Page<Agony> findUserBookShelfPageOfAgonies(long bookId, long userId, Pageable pageable) {
        List<Agony> contents = queryFactory.select(agony)
            .from(agony)
            .join(agony.bookShelf, bookShelf).on(bookShelf.user.id.eq(userId)
                .and(bookShelf.book.id.eq(bookId)))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(extractOrderSpecifierFrom(agony, pageable))
            .fetch();

        return new PageImpl<Agony>(contents, pageable, contents.size());
    }

    @Override
    public void deleteByAgoniesIds(Long bookId, Long userId, List<Long> agoniesIds) {
        QAgony subAgony = new QAgony("subAgony");

        queryFactory.delete(agony)
            .where(agony.id.in(
                JPAExpressions.select(subAgony.id)
                    .from(subAgony)
                    .join(subAgony.bookShelf, bookShelf)
                    .on(bookShelf.book.id.eq(bookId).and(bookShelf.user.id.eq(userId)))
                    .where(subAgony.id.in(agoniesIds))
            )).execute();
    }
}
