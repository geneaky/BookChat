package toy.bookchat.bookchat.domain.chatroom.repository.query;

import static toy.bookchat.bookchat.domain.chat.QChat.chat;
import static toy.bookchat.bookchat.domain.chatroom.QChatRoom.chatRoom;
import static toy.bookchat.bookchat.domain.chatroom.QChatRoomHashTag.chatRoomHashTag;
import static toy.bookchat.bookchat.domain.chatroom.QHashTag.hashTag;
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
import toy.bookchat.bookchat.domain.chatroom.repository.query.dto.response.UserChatRoomResponse;
import toy.bookchat.bookchat.domain.chatroom.service.dto.request.ChatRoomRequest;
import toy.bookchat.bookchat.domain.participant.QParticipant;

@Repository
public class ChatRoomQueryRepositoryImpl implements ChatRoomQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ChatRoomQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public Slice<UserChatRoomResponse> findUserChatRoomsWithLastChat(Pageable pageable,
        Optional<Long> postCursorId,
        Long userId) {
        QChat subChat = new QChat("subChat");
        QParticipant subParticipant1 = new QParticipant("subParticipant1");
        QParticipant subParticipant2 = new QParticipant("subParticipant2");

        List<UserChatRoomResponse> contents = queryFactory.select(
                Projections.constructor(UserChatRoomResponse.class,
                    chatRoom.id,
                    chatRoom.roomName,
                    chatRoom.roomSid,
                    subParticipant1.count(),
                    chatRoom.defaultRoomImageType,
                    chatRoom.roomImageUri,
                    chat.id,
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
            .groupBy(chatRoom.id, chat.id)
            .where(afterPostCursorId(postCursorId))
            .limit(pageable.getPageSize())
            .orderBy(chat.id.desc(), chatRoom.id.desc())
            .fetch();

        return toSlice(contents, pageable);
    }

    @Override
    public Slice<ChatRoomResponse> findChatRooms(ChatRoomRequest chatRoomRequest,
        Pageable pageable) {
        QChat subChat = new QChat("subChat");

        List<ChatRoomResponse> contents = queryFactory.select(
                Projections.constructor(ChatRoomResponse.class,
                    chatRoom.id,
                    chatRoom.roomName,
                    chatRoom.roomSid,
                    participant.count(),
                    chatRoom.defaultRoomImageType,
                    chatRoom.roomImageUri,
                    Projections.list(JPAExpressions.select(hashTag.tagName)
                        .from(hashTag)
                        .join(chatRoomHashTag)
                        .where(chatRoomHashTag.chatRoom.id.eq(chatRoom.id)))
                    ,
                    chat.id,
                    chat.createdAt
                ))
            .from(chatRoom)
            .join(participant)
            .join(chatRoomHashTag)
            .join(chatRoomHashTag.hashTag, hashTag)
            .leftJoin(chat).on(chat.id.in(
                JPAExpressions.select(subChat.id.max())
                    .from(subChat)
                    .groupBy(subChat.chatRoom)
                    .having(subChat.chatRoom.id.eq(chatRoom.id))
            ))
            .groupBy(chatRoom.id, chatRoom.roomName)
            .fetch();

        return toSlice(contents, pageable);
    }

    private BooleanExpression afterPostCursorId(Optional<Long> postCursorId) {
        return postCursorId.map(chat.id::lt).orElse(null);
    }
}
