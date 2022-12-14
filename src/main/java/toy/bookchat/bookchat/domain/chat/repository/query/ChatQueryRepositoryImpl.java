package toy.bookchat.bookchat.domain.chat.repository.query;

import static toy.bookchat.bookchat.domain.chat.QChat.chat;
import static toy.bookchat.bookchat.domain.chatroom.QChatRoom.chatRoom;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.extractOrderSpecifierFrom;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.numberBasedPagination;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.toSlice;
import static toy.bookchat.bookchat.domain.participant.QParticipant.participant;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chat.QChat;

@Repository
public class ChatQueryRepositoryImpl implements ChatQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ChatQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Slice<Chat> findUserChatRoomsWithLastChat(Optional<Long> postCursorId,
        Pageable pageable, Long userId) {
        QChat subChat = new QChat("subChat");

        List<Chat> contents = queryFactory.select(chat)
            .from(chat)
            .join(chat.chatRoom, chatRoom).fetchJoin()
            .join(participant)
            .on(participant.chatRoom.eq(chat.chatRoom).and(participant.user.id.eq(userId)))
            .where(numberBasedPagination(chat, chat.id, postCursorId, pageable), chat.id.in(
                JPAExpressions.select(subChat.id.max())
                    .from(subChat)
                    .join(participant)
                    .on(participant.chatRoom.eq(subChat.chatRoom)
                        .and(participant.user.id.eq(userId)))
                    .groupBy(subChat.chatRoom).fetch()
            ))
            .limit(pageable.getPageSize())
            .orderBy(extractOrderSpecifierFrom(chat, pageable))
            .fetch();

        return toSlice(contents, pageable);
    }
}
