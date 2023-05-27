package toy.bookchat.bookchat.domain.chat.repository.query;

import static toy.bookchat.bookchat.domain.chat.QChat.chat;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.extractOrderSpecifierFrom;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.numberBasedPagination;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.toSlice;
import static toy.bookchat.bookchat.domain.participant.QParticipant.participant;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import toy.bookchat.bookchat.domain.chat.Chat;

@Repository
public class ChatQueryRepositoryImpl implements ChatQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ChatQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Slice<Chat> getChatRoomChats(Long roomId, Long postCursorId, Pageable pageable,
        Long userId) {
        return toSlice(queryFactory.select(chat)
            .from(chat)
            .where(chat.chatRoom.id.eq(JPAExpressions.select(participant.chatRoom.id)
                    .from(participant)
                    .where(participant.user.id.eq(userId)
                        .and(participant.chatRoom.id.eq(roomId)))),
                numberBasedPagination(chat, chat.id, postCursorId, pageable)
            )
            .limit(pageable.getPageSize())
            .orderBy(extractOrderSpecifierFrom(chat, pageable)).fetch(), pageable);
    }
}
