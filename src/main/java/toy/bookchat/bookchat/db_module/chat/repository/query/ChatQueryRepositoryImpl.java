package toy.bookchat.bookchat.db_module.chat.repository.query;

import static toy.bookchat.bookchat.db_module.chat.QChatEntity.chatEntity;
import static toy.bookchat.bookchat.db_module.participant.QParticipantEntity.participantEntity;
import static toy.bookchat.bookchat.db_module.user.QUserEntity.userEntity;
import static toy.bookchat.bookchat.support.RepositorySupport.extractOrderSpecifierFrom;
import static toy.bookchat.bookchat.support.RepositorySupport.numberBasedPagination;
import static toy.bookchat.bookchat.support.RepositorySupport.toSlice;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import toy.bookchat.bookchat.db_module.chat.ChatEntity;

@Repository
public class ChatQueryRepositoryImpl implements ChatQueryRepository {

  private final JPAQueryFactory queryFactory;

  public ChatQueryRepositoryImpl(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  @Override
  public Slice<ChatEntity> getChatRoomChats(Long roomId, Long postCursorId, Pageable pageable, Long userId) {
    return toSlice(queryFactory.select(chatEntity)
        .from(chatEntity)
        .where(chatEntity.chatRoomId.eq(JPAExpressions.select(participantEntity.chatRoomId)
                .from(participantEntity)
                .where(participantEntity.userId.eq(userId)
                    .and(participantEntity.chatRoomId.eq(roomId)))),
            numberBasedPagination(chatEntity, chatEntity.id, postCursorId, pageable)
        )
        .limit(pageable.getPageSize())
        .orderBy(extractOrderSpecifierFrom(chatEntity, pageable)).fetch(), pageable);
  }

  @Override
  public Optional<ChatEntity> getUserChatRoomChat(Long chatId, Long userId) {
    return Optional.ofNullable(queryFactory.select(chatEntity)
        .from(chatEntity)
        .join(userEntity).on(chatEntity.userId.eq(userEntity.id))
        .where(chatEntity.id.eq(chatId)
            .and(chatEntity.chatRoomId.in(
                JPAExpressions.select(participantEntity.chatRoomId)
                    .from(participantEntity)
                    .where(participantEntity.userId.eq(userId)))
            ))
        .fetchOne());
  }
}
