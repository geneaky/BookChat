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
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.HOST;

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
import toy.bookchat.bookchat.db_module.chat.QChatEntity;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomEntity;
import toy.bookchat.bookchat.db_module.chatroom.QHashTagEntity;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.ChatRoomParticipantModel;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.ChatRoomResponse;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.QChatRoomParticipantModel;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.QChatRoomResponse;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.QUserChatRoomResponse;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.UserChatRoomResponse;
import toy.bookchat.bookchat.db_module.participant.QParticipantEntity;
import toy.bookchat.bookchat.db_module.user.QUserEntity;
import toy.bookchat.bookchat.domain.chatroom.api.v1.request.ChatRoomRequest;
import toy.bookchat.bookchat.domain.chatroom.api.v1.response.ChatRoomDetails;
import toy.bookchat.bookchat.domain.participant.ParticipantStatus;
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
    QParticipantEntity subParticipantEntity1 = new QParticipantEntity("subParticipantEntity1");
    QParticipantEntity subParticipantEntity2 = new QParticipantEntity("subParticipantEntity2");
    QChatEntity subChatEntity = new QChatEntity("subChatEntity");
    QUserEntity subUserEntity = new QUserEntity("subUserEntity");

    List<UserChatRoomResponse> contents = queryFactory.select(
            new QUserChatRoomResponse(
                chatRoomEntity.id,
                chatRoomEntity.roomName,
                chatRoomEntity.roomSid,
                subParticipantEntity2.count(),
                chatRoomEntity.defaultRoomImageType,
                chatRoomEntity.roomImageUri,
                userEntity.id,
                userEntity.nickname,
                userEntity.profileImageUrl,
                userEntity.defaultProfileImageType,
                subUserEntity.id,
                subUserEntity.nickname,
                subUserEntity.profileImageUrl,
                subUserEntity.defaultProfileImageType,
                chatEntity.id,
                chatEntity.message,
                chatEntity.createdAt
            ))
        .from(chatRoomEntity)
        .join(participantEntity)
        .on(participantEntity.chatRoomId.eq(chatRoomEntity.id).and(participantEntity.userId.eq(userId))) //사용자 채팅방
        .join(subParticipantEntity1)
        .on(subParticipantEntity1.chatRoomId.eq(chatRoomEntity.id)
            .and(subParticipantEntity1.participantStatus.eq(HOST)))
        .join(userEntity).on(userEntity.id.eq(subParticipantEntity1.userId)) //방장
        .leftJoin(chatEntity).on(chatEntity.id.eq( // 마지막 채팅, 채팅 내
                JPAExpressions.select(subChatEntity.id.max())
                    .from(subChatEntity)
                    .where(subChatEntity.chatRoomId.eq(chatRoomEntity.id))
            )
        )
        .leftJoin(subUserEntity).on(subUserEntity.id.eq(chatEntity.userId))
        .leftJoin(subParticipantEntity2).on(subParticipantEntity2.chatRoomId.eq(chatRoomEntity.id)) //채팅방 인원수
        .groupBy(chatRoomEntity.id, participantEntity.id, subParticipantEntity1.id, userEntity.id, subUserEntity.id,
            chatEntity.id)
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
  public Slice<ChatRoomResponse> findChatRooms(ChatRoomRequest chatRoomRequest, Pageable pageable) {
    QChatEntity subChatEntity = new QChatEntity("subChatEntity");
    QParticipantEntity subParticipantEntity = new QParticipantEntity("subParticipantEntity");

    List<ChatRoomResponse> contents = queryFactory.select(
            new QChatRoomResponse(
                chatRoomEntity.id,
                chatRoomEntity.roomName,
                chatRoomEntity.roomSid,
                chatRoomEntity.defaultRoomImageType,
                chatRoomEntity.roomImageUri,
                chatRoomEntity.roomSize,
                JPAExpressions.select(subParticipantEntity.count()).from(subParticipantEntity)
                    .where(subParticipantEntity.chatRoomId.eq(chatRoomEntity.id)),
                bookEntity.title,
                bookEntity.bookCoverImageUrl,
                userEntity.id,
                userEntity.nickname,
                userEntity.defaultProfileImageType,
                userEntity.profileImageUrl,
                Expressions.stringTemplate("group_concat({0})", hashTagEntity.tagName),
                chatEntity.userId,
                chatEntity.id,
                chatEntity.message,
                chatEntity.createdAt)
        )
        .from(chatRoomEntity)
        .join(chatRoomHashTagEntity).on(chatRoomHashTagEntity.chatRoomId.eq(chatRoomEntity.id))
        .join(hashTagEntity).on(hashTagEntity.id.eq(chatRoomHashTagEntity.hashTagId))
        .join(participantEntity)
        .on(participantEntity.chatRoomId.eq(chatRoomEntity.id).and(participantEntity.participantStatus.eq(HOST)))
        .join(userEntity).on(userEntity.id.eq(participantEntity.userId))
        .join(bookEntity).on(chatRoomEntity.bookId.eq(bookEntity.id))
        .leftJoin(chatEntity).on(chatEntity.id.in(
            JPAExpressions.select(subChatEntity.id.max())
                .from(subChatEntity)
                .where(subChatEntity.chatRoomId.eq(chatRoomEntity.id)))
        )
        .groupBy(chatRoomEntity.id, userEntity.id, chatEntity.id)
        .where(
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

    List<ChatRoomParticipantModel> chatRoomParticipantModels = queryFactory.select(
            new QChatRoomParticipantModel(
                userEntity.id,
                userEntity.nickname,
                userEntity.profileImageUrl,
                userEntity.defaultProfileImageType,
                participantEntity.participantStatus
            )
        ).from(participantEntity)
        .join(userEntity).on(userEntity.id.eq(participantEntity.userId))
        .where(participantEntity.chatRoomId.eq(
            JPAExpressions.select(subParticipant.chatRoomId)
                .from(subParticipant)
                .where(subParticipant.chatRoomId.eq(roomId)
                    .and(subParticipant.userId.eq(userId)))))
        .fetch();

    if (chatRoomParticipantModels.isEmpty()) {
      throw new ParticipantNotFoundException();
    }

    List<String> roomTags = queryFactory.select(hashTagEntity.tagName)
        .from(hashTagEntity)
        .join(chatRoomHashTagEntity).on(chatRoomHashTagEntity.hashTagId.eq(hashTagEntity.id))
        .where(chatRoomHashTagEntity.chatRoomId.eq(roomId))
        .fetch();

    BookEntity book = queryFactory.select(bookEntity)
        .from(bookEntity)
        .join(chatRoomEntity).on(chatRoomEntity.bookId.eq(bookEntity.id))
        .where(chatRoomEntity.id.eq(roomId))
        .fetchOne();

    ChatRoomEntity chatRoom = queryFactory.select(chatRoomEntity)
        .from(chatRoomEntity)
        .where(chatRoomEntity.id.eq(roomId))
        .fetchOne();

    return ChatRoomDetails.from(chatRoomParticipantModels, roomTags, book, chatRoom);
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
