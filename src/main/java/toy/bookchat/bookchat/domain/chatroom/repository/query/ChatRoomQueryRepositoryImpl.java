package toy.bookchat.bookchat.domain.chatroom.repository.query;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static toy.bookchat.bookchat.domain.book.QBook.book;
import static toy.bookchat.bookchat.domain.chat.QChat.chat;
import static toy.bookchat.bookchat.domain.chatroom.QChatRoom.chatRoom;
import static toy.bookchat.bookchat.domain.chatroom.QChatRoomHashTag.chatRoomHashTag;
import static toy.bookchat.bookchat.domain.chatroom.QHashTag.hashTag;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.toSlice;
import static toy.bookchat.bookchat.domain.participant.QParticipant.participant;
import static toy.bookchat.bookchat.domain.user.QUser.user;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.chat.QChat;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.QChatRoomHashTag;
import toy.bookchat.bookchat.domain.chatroom.repository.query.dto.response.ChatRoomResponse;
import toy.bookchat.bookchat.domain.chatroom.repository.query.dto.response.UserChatRoomResponse;
import toy.bookchat.bookchat.domain.chatroom.service.dto.request.ChatRoomRequest;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.QParticipant;
import toy.bookchat.bookchat.domain.participant.service.dto.response.ChatRoomDetails;
import toy.bookchat.bookchat.exception.participant.ParticipantNotFoundException;

@Repository
public class ChatRoomQueryRepositoryImpl implements ChatRoomQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ChatRoomQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    private BooleanExpression inTags(ChatRoomRequest chatRoomRequest) {
        if (chatRoomRequest.getTags().isEmpty()) {
            return null;
        }
        return hashTag.tagName.in(chatRoomRequest.getTags());
    }

    private BooleanExpression eqIsbn(ChatRoomRequest chatRoomRequest) {
        return chatRoomRequest.getIsbn().map(book.isbn::eq).orElse(null);
    }

    private BooleanExpression containsTitle(ChatRoomRequest chatRoomRequest) {
        return chatRoomRequest.getTitle().map(book.title::contains).orElse(null);
    }

    private BooleanExpression containsRoomName(ChatRoomRequest chatRoomRequest) {
        return chatRoomRequest.getRoomName().map(chatRoom.roomName::contains).orElse(null);
    }

    private BooleanExpression afterPostCursorId(Long postCursorId) {
        return postCursorId == null ? null : chat.id.lt(postCursorId);
    }

    @Override
    public Slice<UserChatRoomResponse> findUserChatRoomsWithLastChat(Pageable pageable,
        Long bookId, Long postCursorId, Long userId) {
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
            .groupBy(chatRoom.id, chat.id)
            .where(afterPostCursorId(postCursorId),
                eqBookId(bookId))
            .limit(pageable.getPageSize())
            .orderBy(chat.id.desc(), chatRoom.id.desc())
            .fetch();

        List<Long> chatRoomIds = contents.stream().map(UserChatRoomResponse::getRoomId)
            .collect(toList());
        List<ChatRoom> chatRooms = queryFactory.select(chatRoom)
            .from(chatRoom)
            .join(chatRoom.book, book).fetchJoin()
            .where(chatRoom.id.in(chatRoomIds))
            .fetch();

        Map<Long, Book> mapBooksByChatRoomId = chatRooms.stream()
            .collect(toMap(ChatRoom::getId, ChatRoom::getBook));
        contents.forEach(c -> c.setBookInfo(mapBooksByChatRoomId.get(c.getRoomId())));

        return toSlice(contents, pageable);
    }

    private BooleanExpression eqBookId(Long bookId) {
        return bookId == null ? null : chatRoom.book.id.eq(bookId);
    }

    @Override
    public Slice<ChatRoomResponse> findChatRooms(ChatRoomRequest chatRoomRequest,
        Pageable pageable) {
        QChat subChat = new QChat("subChat");
        QChatRoomHashTag subChatRoomHashTag = new QChatRoomHashTag("subChatRoomHashTag");

        List<ChatRoomResponse> contents = queryFactory.select(
                Projections.constructor(ChatRoomResponse.class,
                    chatRoom.id,
                    chatRoom.roomName,
                    chatRoom.roomSid,
                    book.title,
                    book.bookCoverImageUrl,
                    chatRoom.host.nickname,
                    chatRoom.host.defaultProfileImageType,
                    chatRoom.host.profileImageUrl,
                    // TODO: 2023/03/19 scalar subquery 성능 문제가 있다면 대체 방법 고려
                    JPAExpressions.select(participant.count()).from(participant)
                        .where(participant.chatRoom.id.eq(chatRoom.id)),
                    chatRoom.defaultRoomImageType,
                    chatRoom.roomImageUri,
                    Expressions.stringTemplate("group_concat({0})", hashTag.tagName),
                    chat.id,
                    chat.createdAt))
            .from(chatRoom)
            .join(user).on(chatRoom.host.id.eq(user.id))
            .join(book).on(chatRoom.book.id.eq(book.id))
            .join(chatRoomHashTag).on(chatRoomHashTag.chatRoom.id.in(
                JPAExpressions.select(subChatRoomHashTag.chatRoom.id)
                    .from(subChatRoomHashTag)
                    .where(subChatRoomHashTag.chatRoom.id.eq(chatRoom.id),
                        inTags(chatRoomRequest))))
            .join(hashTag).on(hashTag.id.eq(chatRoomHashTag.hashTag.id))
            .leftJoin(chat).on(chat.id.in(
                JPAExpressions.select(subChat.id.max())
                    .from(subChat)
                    .groupBy(subChat.chatRoom)
                    .having(subChat.chatRoom.id.eq(chatRoom.id))))
            .groupBy(chatRoom.id, chat.id)
            .where(chat.chatRoom.id.eq(chatRoom.id),
                chatRoomRequest.getPostCursorId().map(chatRoom.id::lt).orElse(null),
                eqIsbn(chatRoomRequest),
                containsTitle(chatRoomRequest),
                containsRoomName(chatRoomRequest)
            )
            .limit(pageable.getPageSize())
            .orderBy(chat.id.desc(), chatRoom.id.desc())
            .fetch();

        List<Long> chatRoomIds = contents.stream().map(ChatRoomResponse::getRoomId)
            .collect(toList());
        List<ChatRoom> chatRooms = queryFactory.select(chatRoom)
            .from(chatRoom)
            .join(chatRoom.book, book).fetchJoin()
            .where(chatRoom.id.in(chatRoomIds))
            .fetch();

        Map<Long, List<String>> authorsMap = chatRooms.stream()
            .collect(toMap(ChatRoom::getId, ChatRoom::getBookAuthors));
        contents.forEach(c -> c.setBookAuthors(authorsMap.get(c.getRoomId())));

        return toSlice(contents, pageable);
    }

    @Override
    public ChatRoomDetails findChatRoomDetails(Long roomId, Long userId) {
        QParticipant subParticipant = new QParticipant("subParticipant");

        List<Participant> participants = queryFactory.select(participant)
            .from(participant)
            .join(participant.user, user).fetchJoin()
            .join(participant.chatRoom, chatRoom).fetchJoin()
            .join(chatRoom.book, book).fetchJoin()
            .where(participant.chatRoom.id.eq(JPAExpressions.select(subParticipant.chatRoom.id)
                .from(subParticipant)
                .where(subParticipant.chatRoom.id.eq(roomId)
                    .and(subParticipant.user.id.eq(userId)))))
            .fetch();

        if (participants.isEmpty()) {
            throw new ParticipantNotFoundException();
        }

        List<String> roomTags = queryFactory.select(hashTag.tagName)
            .from(hashTag)
            .join(chatRoomHashTag).on(chatRoomHashTag.hashTag.id.eq(hashTag.id))
            .where(chatRoomHashTag.chatRoom.id.eq(roomId))
            .fetch();

        return ChatRoomDetails.from(participants, roomTags);
    }
}
