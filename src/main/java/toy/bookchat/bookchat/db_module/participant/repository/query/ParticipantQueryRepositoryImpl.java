package toy.bookchat.bookchat.db_module.participant.repository.query;

import static toy.bookchat.bookchat.db_module.chatroom.QChatRoomEntity.chatRoomEntity;
import static toy.bookchat.bookchat.db_module.participant.QParticipantEntity.participantEntity;
import static toy.bookchat.bookchat.db_module.user.QUserEntity.userEntity;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.SUBHOST;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.stereotype.Repository;
import toy.bookchat.bookchat.db_module.participant.ParticipantEntity;

@Repository
public class ParticipantQueryRepositoryImpl implements ParticipantQueryRepository {

  private final JPAQueryFactory queryFactory;

  public ParticipantQueryRepositoryImpl(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  @Override
  public Optional<ParticipantEntity> findByUserIdAndChatRoomId(Long userId, Long chatRoomId) {
    return Optional.ofNullable(
        queryFactory.select(participantEntity)
            .from(participantEntity)
            .join(userEntity).on(participantEntity.userId.eq(userEntity.id))
            .join(chatRoomEntity).on(participantEntity.chatRoomId.eq(chatRoomEntity.id))
            .where(participantEntity.userId.eq(userId)
                .and(participantEntity.chatRoomId.eq(chatRoomId)))
            .fetchOne()
    );
  }

  @Override
  public Optional<ParticipantEntity> findHostWithPessimisticLockByUserIdAndChatRoomId(Long userId, Long chatRoomId) {
    return Optional.ofNullable(
        queryFactory.select(participantEntity)
            .from(participantEntity)
            .join(userEntity).on(participantEntity.userId.eq(userEntity.id))
            .join(chatRoomEntity).on(participantEntity.chatRoomId.eq(chatRoomEntity.id)
                .and(chatRoomEntity.hostId.eq(userEntity.id)))
            .where(participantEntity.userId.eq(userId)
                .and(participantEntity.chatRoomId.eq(chatRoomId)))
            .setLockMode(LockModeType.PESSIMISTIC_WRITE)
            .fetchOne()
    );
  }

  @Override
  public Long countSubHostByRoomId(Long roomId) {
    return queryFactory.select(participantEntity.count())
        .from(participantEntity)
        .where(participantEntity.chatRoomId.eq(roomId).and(participantEntity.participantStatus.eq(SUBHOST)))
        .fetchOne();
  }

  @Override
  public void disconnectAllByUserId(Long userId) {
    queryFactory.update(participantEntity)
        .set(participantEntity.isConnected, false)
        .where(participantEntity.userId.eq(userId))
        .execute();
  }

  @Override
  public Optional<ParticipantEntity> findByUserIdAndChatRoomSid(Long userId, String roomSid) {
    return Optional.ofNullable(queryFactory.select(participantEntity)
        .from(participantEntity)
        .join(userEntity).on(participantEntity.userId.eq(userEntity.id))
        .join(chatRoomEntity).on(participantEntity.chatRoomId.eq(chatRoomEntity.id))
        .where(participantEntity.userId.eq(userId)
            .and(chatRoomEntity.roomSid.eq(roomSid)))
        .fetchOne());
  }
}
