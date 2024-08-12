package toy.bookchat.bookchat.db_module.chatroom.repository.query;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static toy.bookchat.bookchat.db_module.book.QBookEntity.bookEntity;
import static toy.bookchat.bookchat.db_module.chat.QChatEntity.chatEntity;
import static toy.bookchat.bookchat.db_module.chatroom.QChatRoomEntity.chatRoomEntity;
import static toy.bookchat.bookchat.db_module.chatroom.QChatRoomHashTagEntity.chatRoomHashTagEntity;
import static toy.bookchat.bookchat.db_module.chatroom.QHashTagEntity.hashTagEntity;
import static toy.bookchat.bookchat.db_module.participant.QParticipantEntity.participantEntity;
import static toy.bookchat.bookchat.db_module.user.QUserEntity.userEntity;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.toSlice;

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
import toy.bookchat.bookchat.db_module.book.BookEntity;
import toy.bookchat.bookchat.db_module.book.QBookEntity;
import toy.bookchat.bookchat.db_module.chat.QChatEntity;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomEntity;
import toy.bookchat.bookchat.db_module.chatroom.QChatRoomHashTagEntity;
import toy.bookchat.bookchat.db_module.chatroom.QHashTagEntity;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.response.ChatRoomResponse;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.response.QChatRoomResponse;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.response.UserChatRoomResponse;
import toy.bookchat.bookchat.db_module.participant.ParticipantEntity;
import toy.bookchat.bookchat.db_module.participant.QParticipantEntity;
import toy.bookchat.bookchat.db_module.user.QUserEntity;
import toy.bookchat.bookchat.db_module.user.UserEntity;
import toy.bookchat.bookchat.domain.chatroom.api.v1.request.ChatRoomRequest;
import toy.bookchat.bookchat.domain.participant.ParticipantStatus;
import toy.bookchat.bookchat.domain.participant.api.v1.response.ChatRoomDetails;
import toy.bookchat.bookchat.exception.notfound.pariticipant.ParticipantNotFoundException;

@Repository
public class ChatRoomQueryRepositoryImpl implements ChatRoomQueryRepository {

  private final JPAQueryFactory queryFactory;

  public ChatRoomQueryRepositoryImpl(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  private BooleanExpression inTags(QHashTagEntity hashTagEntity, List<String> tags) {
    if (tags.isEmpty()) {
      return null;
    }
    return hashTagEntity.tagName.in(tags);
  }

  private BooleanExpression eqIsbn(String isbn) {
    return isbn == null ? null : bookEntity.isbn.eq(isbn);
  }

  private BooleanExpression containsTitle(String title) {
    return title == null ? null : bookEntity.title.contains(title);
  }

  private BooleanExpression containsRoomName(String roomName) {
    return roomName == null ? null : chatRoomEntity.roomName.contains(roomName);
  }

  private BooleanExpression afterChatRoomId(Long postCursorId) {
    return postCursorId == null ? null : chatRoomEntity.id.lt(postCursorId);
  }

  @Override
  public Slice<UserChatRoomResponse> findUserChatRoomsWithLastChat(Pageable pageable, Long bookId, Long postCursorId,
      Long userId) {
    QChatEntity subChat = new QChatEntity("subChat");
    QParticipantEntity subParticipant = new QParticipantEntity("subParticipant1");
    QUserEntity subUser = new QUserEntity("subUser");

    List<UserChatRoomResponse> contents = queryFactory.select(
            Projections.constructor(UserChatRoomResponse.class,
                chatRoomEntity.id,
                chatRoomEntity.roomName,
                chatRoomEntity.roomSid,
                subParticipant.count(),
                chatRoomEntity.defaultRoomImageType,
                chatRoomEntity.roomImageUri,
                subUser.id,
                subUser.nickname,
                subUser.profileImageUrl,
                subUser.defaultProfileImageType,
                userEntity.id,
                userEntity.nickname,
                userEntity.profileImageUrl,
                userEntity.defaultProfileImageType,
                chatEntity.id,
                chatEntity.message,
                chatEntity.createdAt
            ))
        .from(chatRoomEntity)
        .join(subUser).on(subUser.id.eq(chatRoomEntity.hostId))
        .join(participantEntity)
        .on(participantEntity.chatRoomId.eq(chatRoomEntity.id).and(participantEntity.userId.eq(userId))) //사용자 채팅방
        .leftJoin(subParticipant).on(subParticipant.chatRoomId.eq(chatRoomEntity.id)) // 채팅방 인원수
        .leftJoin(chatEntity).on(chatEntity.id.eq( // 마지막 채팅, 채팅 내
                JPAExpressions.select(subChat.id.max())
                    .from(subChat)
                    .where(subChat.chatRoomId.eq(chatRoomEntity.id))
            )
        )
        .leftJoin(userEntity).on(userEntity.id.eq(chatEntity.userId))
        .groupBy(chatRoomEntity.id, chatEntity.id)
        .where(afterChatRoomId(postCursorId), eqBookId(bookId))
        .orderBy(chatEntity.id.desc())
        .limit(pageable.getPageSize())
        .fetch();

    List<Long> chatRoomIds = contents.stream().map(UserChatRoomResponse::getRoomId)
        .collect(toList());

    List<ChatRoomEntity> chatRoomEntities = queryFactory.select(chatRoomEntity)
        .from(chatRoomEntity)
        .where(chatRoomEntity.id.in(chatRoomIds))
        .fetch();
    Map<Long, Long> chatRoomIdBookIdMap = chatRoomEntities.stream()
        .collect(toMap(ChatRoomEntity::getId, ChatRoomEntity::getBookId));

    List<BookEntity> bookEntities = queryFactory.select(bookEntity)
        .from(bookEntity)
        .join(chatRoomEntity).on(chatRoomEntity.bookId.eq(bookEntity.id))
        .where(chatRoomEntity.id.in(chatRoomIds))
        .fetch();
    Map<Long, BookEntity> bookIdBookEntityMap = bookEntities.stream().collect(toMap(BookEntity::getId, identity()));

    contents.forEach(c -> c.setBookInfo(bookIdBookEntityMap.get(chatRoomIdBookIdMap.get(c.getRoomId()))));

    return toSlice(contents, pageable);
  }

  private BooleanExpression eqBookId(Long bookId) {
    return bookId == null ? null : chatRoomEntity.bookId.eq(bookId);
  }

  @Override
  public Slice<ChatRoomResponse> findChatRooms(Long userId, ChatRoomRequest chatRoomRequest, Pageable pageable) {
    QChatEntity subChat = new QChatEntity("subChat");
    QChatRoomHashTagEntity subChatRoomHashTagEntity = new QChatRoomHashTagEntity("subChatRoomHashTagEntity");
    QHashTagEntity subHashTagEntity = new QHashTagEntity("subHashTagEntity");

    List<ChatRoomResponse> contents = queryFactory.select(
            new QChatRoomResponse(
                chatRoomEntity.id,
                chatRoomEntity.roomName,
                chatRoomEntity.roomSid,
                bookEntity.title,
                bookEntity.bookCoverImageUrl,
                chatRoomEntity.hostId,
                userEntity.nickname,
                userEntity.defaultProfileImageType,
                userEntity.profileImageUrl,
                // TODO: 2023/03/19 scalar subquery 성능 문제가 있다면 대체 방법 고려
                JPAExpressions.select(participantEntity.count()).from(participantEntity)
                    .where(participantEntity.chatRoomId.eq(chatRoomEntity.id)),
                chatRoomEntity.roomSize,
                chatRoomEntity.defaultRoomImageType,
                chatRoomEntity.roomImageUri,
                Expressions.stringTemplate("group_concat({0})", hashTagEntity.tagName),
                chatEntity.userId,
                chatEntity.id,
                chatEntity.message,
                chatEntity.createdAt)
        )
        .from(chatRoomEntity)
        .join(userEntity).on(chatRoomEntity.hostId.eq(userEntity.id))
        .join(bookEntity).on(chatRoomEntity.bookId.eq(bookEntity.id))
        .join(chatRoomHashTagEntity).on(chatRoomHashTagEntity.chatRoomId.in(
            JPAExpressions.select(subChatRoomHashTagEntity.chatRoomId)
                .from(subChatRoomHashTagEntity)
                .join(subHashTagEntity).on(subHashTagEntity.id.eq(subChatRoomHashTagEntity.hashTagId))
                .where(subChatRoomHashTagEntity.chatRoomId.eq(chatRoomEntity.id),
                    inTags(subHashTagEntity, chatRoomRequest.getTags())
                )
        ))
        .join(hashTagEntity).on(hashTagEntity.id.eq(chatRoomHashTagEntity.hashTagId))
        .leftJoin(participantEntity)
        .on(chatRoomEntity.id.eq(participantEntity.chatRoomId).and(participantEntity.userId.eq(userId)))
        .leftJoin(chatEntity).on(chatEntity.id.in(
            JPAExpressions.select(subChat.id.max())
                .from(subChat)
                .groupBy(subChat.chatRoomId)
                .having(subChat.chatRoomId.eq(chatRoomEntity.id))))
        .groupBy(chatRoomEntity.id, chatEntity.id)
        .where(chatEntity.chatRoomId.eq(chatRoomEntity.id),
            ltCursorId(chatRoomRequest.getPostCursorId()),
            eqIsbn(chatRoomRequest.getIsbn()),
            containsTitle(chatRoomRequest.getTitle()),
            containsRoomName(chatRoomRequest.getRoomName())
        )
        .limit(pageable.getPageSize())
        .orderBy(chatEntity.id.desc(), chatRoomEntity.id.desc())
        .fetch();

    List<Long> chatRoomIds = contents.stream().map(ChatRoomResponse::getRoomId)
        .collect(toList());

    List<ChatRoomEntity> chatRoomEntities = queryFactory.select(chatRoomEntity)
        .from(chatRoomEntity)
        .where(chatRoomEntity.id.in(chatRoomIds))
        .fetch();
    Map<Long, Long> chatRoomBookIdMap = chatRoomEntities.stream()
        .collect(toMap(ChatRoomEntity::getId, ChatRoomEntity::getBookId));

    List<BookEntity> bookEntities = queryFactory.select(bookEntity)
        .from(bookEntity)
        .join(chatRoomEntity).on(chatRoomEntity.bookId.eq(bookEntity.id))
        .where(chatRoomEntity.id.in(chatRoomIds))
        .fetch();
    Map<Long, List<String>> bookIdAuthorsMap = bookEntities.stream()
        .collect(toMap(BookEntity::getId, BookEntity::getAuthors));

    contents.forEach(c -> c.setBookAuthors(bookIdAuthorsMap.get(chatRoomBookIdMap.get(c.getRoomId()))));

    return toSlice(contents, pageable);
  }

  private BooleanExpression ltCursorId(Long cursorId) {
    return cursorId == null ? null : chatRoomEntity.id.lt(cursorId);
  }

  @Override
  public ChatRoomDetails findChatRoomDetails(Long roomId, Long userId) {
    QParticipantEntity subParticipant = new QParticipantEntity("subParticipant");

    List<ParticipantEntity> participantEntities = queryFactory.select(participantEntity)
        .from(participantEntity)
        .join(userEntity).on(userEntity.id.eq(participantEntity.userId))
        .join(chatRoomEntity).on(chatRoomEntity.id.eq(participantEntity.chatRoomId))
        .where(participantEntity.chatRoomId.eq(JPAExpressions.select(subParticipant.chatRoomId)
            .from(subParticipant)
            .where(subParticipant.chatRoomId.eq(roomId)
                .and(subParticipant.userId.eq(userId)))))
        .fetch();

    if (participantEntities.isEmpty()) {
      throw new ParticipantNotFoundException();
    }

    List<String> roomTags = queryFactory.select(hashTagEntity.tagName)
        .from(hashTagEntity)
        .join(chatRoomHashTagEntity).on(chatRoomHashTagEntity.hashTagId.eq(hashTagEntity.id))
        .where(chatRoomHashTagEntity.chatRoomId.eq(roomId))
        .fetch();

    BookEntity bookEntity = queryFactory.select(QBookEntity.bookEntity)
        .from(QBookEntity.bookEntity)
        .join(chatRoomEntity).on(chatRoomEntity.bookId.eq(QBookEntity.bookEntity.id))
        .where(chatRoomEntity.id.eq(roomId))
        .fetchOne();

    ChatRoomEntity chatRoom = queryFactory.select(chatRoomEntity).from(chatRoomEntity)
        .where(chatRoomEntity.id.eq(roomId)).fetchOne();

    UserEntity host = queryFactory.select(userEntity).from(userEntity)
        .join(chatRoomEntity).on(chatRoomEntity.hostId.eq(userEntity.id))
        .where(chatRoomEntity.id.eq(roomId)).fetchOne();

    return ChatRoomDetails.from(participantEntities, roomTags, bookEntity, chatRoom, host);
  }

  @Override
  public Optional<ChatRoomEntity> findUserChatRoom(Long roomId, Long userId, ParticipantStatus participantStatus) {
    return Optional.ofNullable(queryFactory.select(chatRoomEntity)
        .from(chatRoomEntity)
        .join(participantEntity)
        .on(participantEntity.chatRoomId.eq(chatRoomEntity.id).and(participantEntity.userId.eq(userId)))
        .where(chatRoomEntity.id.eq(roomId), eqParticipantStatus(participantStatus))
        .fetchOne());
  }

  private BooleanExpression eqParticipantStatus(ParticipantStatus participantStatus) {
    return participantStatus == null ? null : participantEntity.participantStatus.eq(participantStatus);
  }
}
