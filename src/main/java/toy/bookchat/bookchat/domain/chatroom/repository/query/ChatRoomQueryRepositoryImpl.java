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
import java.util.Optional;
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
import toy.bookchat.bookchat.exception.notfound.pariticipant.ParticipantNotFoundException;

@Repository
public class ChatRoomQueryRepositoryImpl implements ChatRoomQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ChatRoomQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    private BooleanExpression inTags(List<String> tags) {
        if (tags.isEmpty()) {
            return null;
        }
        return hashTag.tagName.in(tags);
    }

    private BooleanExpression eqIsbn(String isbn) {
        return isbn == null ? null : book.isbn.eq(isbn);
    }

    private BooleanExpression containsTitle(String title) {
        return title == null ? null : book.title.contains(title);
    }

    private BooleanExpression containsRoomName(String roomName) {
        return roomName == null ? null : chatRoom.roomName.contains(roomName);
    }

    private BooleanExpression afterChatRoomId(Long postCursorId) {
        return postCursorId == null ? null : chatRoom.id.lt(postCursorId);
    }

    @Override
    public Slice<UserChatRoomResponse> findUserChatRoomsWithLastChat(Pageable pageable, Long bookId, Long postCursorId, Long userId) {
        QChat subChat = new QChat("subChat");
        QParticipant subParticipant = new QParticipant("subParticipant1");

        List<UserChatRoomResponse> contents = queryFactory.select(
                Projections.constructor(UserChatRoomResponse.class,
                    chatRoom.id,
                    chatRoom.roomName,
                    chatRoom.roomSid,
                    subParticipant.count(),
                    chatRoom.defaultRoomImageType,
                    chatRoom.roomImageUri,
                    user.id,
                    user.nickname,
                    user.profileImageUrl,
                    user.defaultProfileImageType,
                    chat.id,
                    chat.message,
                    chat.createdAt
                ))
            .from(chatRoom)
            .join(participant).on(participant.chatRoom.eq(chatRoom).and(participant.user.id.eq(userId))) //사용자 채팅방
            .leftJoin(subParticipant).on(subParticipant.chatRoom.eq(chatRoom)) // 채팅방 인원수
            .leftJoin(chat).on(chat.id.eq( // 마지막 채팅, 채팅 내
                    JPAExpressions.select(subChat.id.max())
                        .from(subChat)
                        .where(subChat.chatRoom.eq(chatRoom))
                )
            )
            .leftJoin(user).on(user.eq(chat.user))
            .groupBy(chatRoom.id, chat.id)
            .where(afterChatRoomId(postCursorId), eqBookId(bookId))
            .orderBy(chat.id.desc())
            .limit(pageable.getPageSize())
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
    public Slice<ChatRoomResponse> findChatRooms(Long userId, ChatRoomRequest chatRoomRequest,
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
                    chatRoom.host.id,
                    chatRoom.host.nickname,
                    chatRoom.host.defaultProfileImageType,
                    chatRoom.host.profileImageUrl,
                    // TODO: 2023/03/19 scalar subquery 성능 문제가 있다면 대체 방법 고려
                    JPAExpressions.select(participant.count()).from(participant)
                        .where(participant.chatRoom.id.eq(chatRoom.id)),
                    chatRoom.roomSize,
                    chatRoom.defaultRoomImageType,
                    chatRoom.roomImageUri,
                    Expressions.stringTemplate("group_concat({0})", hashTag.tagName),
                    chat.user.id,
                    chat.id,
                    chat.message,
                    chat.createdAt
                ))
            .from(chatRoom)
            .join(user).on(chatRoom.host.id.eq(user.id))
            .join(book).on(chatRoom.book.id.eq(book.id))
            .join(chatRoomHashTag).on(chatRoomHashTag.chatRoom.id.in(
                JPAExpressions.select(subChatRoomHashTag.chatRoom.id)
                    .from(subChatRoomHashTag)
                    .where(subChatRoomHashTag.chatRoom.id.eq(chatRoom.id),
                        inTags(chatRoomRequest.getTags()))))
            .join(hashTag).on(hashTag.id.eq(chatRoomHashTag.hashTag.id))
            .leftJoin(participant).on(chatRoom.id.eq(participant.chatRoom.id).and(participant.user.id.eq(userId)))
            .leftJoin(chat).on(chat.id.in(
                JPAExpressions.select(subChat.id.max())
                    .from(subChat)
                    .groupBy(subChat.chatRoom)
                    .having(subChat.chatRoom.id.eq(chatRoom.id))))
            .groupBy(chatRoom.id, chat.id)
            .where(chat.chatRoom.id.eq(chatRoom.id),
                ltCursorId(chatRoomRequest.getPostCursorId()),
                eqIsbn(chatRoomRequest.getIsbn()),
                containsTitle(chatRoomRequest.getTitle()),
                containsRoomName(chatRoomRequest.getRoomName())
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

    private BooleanExpression ltCursorId(Long cursorId) {
        return cursorId == null ? null : chatRoom.id.lt(cursorId);
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

    @Override
    public Optional<ChatRoom> findUserChatRoom(Long roomId, Long userId) {
        return Optional.ofNullable(queryFactory.select(chatRoom)
            .from(chatRoom)
            .join(participant).on(participant.chatRoom.eq(chatRoom).and(participant.user.id.eq(userId)))
            .where(chatRoom.id.eq(roomId))
            .fetchOne());
    }
}
