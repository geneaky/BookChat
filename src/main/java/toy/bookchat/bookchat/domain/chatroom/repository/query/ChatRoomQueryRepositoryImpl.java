package toy.bookchat.bookchat.domain.chatroom.repository.query;

import static toy.bookchat.bookchat.domain.chat.QChat.chat;
import static toy.bookchat.bookchat.domain.chatroom.QChatRoom.chatRoom;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.toSlice;
import static toy.bookchat.bookchat.domain.participant.QParticipant.participant;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import toy.bookchat.bookchat.domain.chat.QChat;
import toy.bookchat.bookchat.domain.chatroom.repository.query.dto.response.ChatRoomResponse;
import toy.bookchat.bookchat.domain.participant.QParticipant;

@Repository
public class ChatRoomQueryRepositoryImpl implements ChatRoomQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ChatRoomQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public Slice<ChatRoomResponse> test2(Pageable pageable, Optional<Long> postCursorId,
        Long userId) {
        QChat subChat = new QChat("subChat");
        QParticipant subParticipant1 = new QParticipant("subParticipant1");
        QParticipant subParticipant2 = new QParticipant("subParticipant2");

        List<ChatRoomResponse> contents = queryFactory.select(
                Projections.constructor(ChatRoomResponse.class,
                    chatRoom.id,
                    chatRoom.roomName,
                    chatRoom.roomSid,
                    subParticipant1.count(),
                    chatRoom.defaultRoomImageType,
                    chatRoom.roomImageUri,
                    chat.createdAt,
                    chat.message
                ))
            .from(chatRoom)
            .join(participant)
            .on(participant.chatRoom.id.eq(chatRoom.id).and(participant.user.id.eq(userId)))
            .leftJoin(subParticipant1).on(subParticipant1.chatRoom.id.eq(chatRoom.id))
            .leftJoin(chat).on(chat.id.in(
                JPAExpressions.select(subChat.id.max())
                    .from(subChat).join(subParticipant2)
                    .on(subChat.chatRoom.id.eq(subParticipant2.chatRoom.id)
                        .and(subParticipant2.user.id.eq(userId)))
                    .groupBy(subParticipant2.chatRoom.id)).and(chat.chatRoom.id.eq(chatRoom.id)))
            .fetchJoin()
            .groupBy(chatRoom.id)
            .where(afterPostCursorId(postCursorId))
            .limit(pageable.getPageSize())
            .orderBy(chatRoom.id.desc())
            .fetch();

        return toSlice(contents, pageable);
    }

    private BooleanExpression afterPostCursorId(Optional<Long> postCursorId) {
        return postCursorId.map(chatRoom.id::lt).orElse(null);
    }
}
