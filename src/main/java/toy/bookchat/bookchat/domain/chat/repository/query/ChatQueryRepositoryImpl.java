package toy.bookchat.bookchat.domain.chat.repository.query;

import static toy.bookchat.bookchat.domain.chat.QChat.chat;
import static toy.bookchat.bookchat.domain.chatroom.QChatRoom.chatRoom;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.extractOrderSpecifierFrom;
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
    public Slice<Chat> findUserChatRoomsWithLastChat(Optional<Long> postChatRoomCursorId,
        Pageable pageable, Long userId) {
        QChat subChat = new QChat("subChat");

        List<Chat> contents = queryFactory.select(chat)
            .from(chat)
            .join(chat.chatRoom, chatRoom).fetchJoin()
            .where(chat.id.in(
                JPAExpressions.select(subChat.id)
                    .from(subChat)
                    .join(participant).on(subChat.chatRoom.eq(participant.chatRoom)
                        .and(participant.user.id.eq(userId)))
                    .groupBy(subChat.chatRoom)
                    .limit(1)))
            .orderBy(extractOrderSpecifierFrom(chat, pageable))
            .fetch();

        return toSlice(contents, pageable);
    }
}
