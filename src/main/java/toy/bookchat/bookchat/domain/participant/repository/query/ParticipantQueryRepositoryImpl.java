package toy.bookchat.bookchat.domain.participant.repository.query;

import static toy.bookchat.bookchat.domain.chatroom.QChatRoomEntity.chatRoomEntity;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.SUBHOST;
import static toy.bookchat.bookchat.domain.participant.QParticipantEntity.participantEntity;
import static toy.bookchat.bookchat.domain.user.QUserEntity.userEntity;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.stereotype.Repository;
import toy.bookchat.bookchat.domain.participant.ParticipantEntity;
import toy.bookchat.bookchat.domain.participant.QParticipantEntity;
import toy.bookchat.bookchat.exception.notfound.pariticipant.ParticipantNotFoundException;

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
                .join(participantEntity.userEntity, userEntity).fetchJoin()
                .join(participantEntity.chatRoomEntity, chatRoomEntity).fetchJoin()
                .where(participantEntity.userEntity.id.eq(userId)
                    .and(participantEntity.chatRoomEntity.id.eq(chatRoomId)))
                .fetchOne()
        );
    }

    @Override
    public Optional<ParticipantEntity> findWithPessimisticLockByUserIdAndChatRoomId(Long userId,
        Long chatRoomId) {
        return Optional.ofNullable(
            queryFactory.select(participantEntity)
                .from(participantEntity)
                .join(participantEntity.userEntity, userEntity).fetchJoin()
                .join(participantEntity.chatRoomEntity, chatRoomEntity).fetchJoin()
                .where(participantEntity.userEntity.id.eq(userId)
                    .and(participantEntity.chatRoomEntity.id.eq(chatRoomId)))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetchOne()
        );
    }

    @Override
    public Long countSubHostByRoomId(Long roomId) {
        return queryFactory.select(participantEntity.count())
            .from(participantEntity)
            .where(
                participantEntity.chatRoomEntity.id.eq(roomId).and(participantEntity.participantStatus.eq(SUBHOST)))
            .fetchOne();
    }

    @Override
    public void disconnectAllByUserId(Long userId) {
        queryFactory.update(participantEntity)
            .set(participantEntity.isConnected, false)
            .where(participantEntity.userEntity.id.eq(userId))
            .execute();
    }

    @Override
    public void connect(Long userId, String roomSid) {
        ParticipantEntity participantEntity = queryFactory.select(QParticipantEntity.participantEntity)
            .from(QParticipantEntity.participantEntity)
            .join(QParticipantEntity.participantEntity.chatRoomEntity, chatRoomEntity)
            .where(QParticipantEntity.participantEntity.userEntity.id.eq(userId)
                .and(chatRoomEntity.roomSid.eq(roomSid)))
            .fetchOne();

        if (participantEntity == null) {
            throw new ParticipantNotFoundException();
        }

        participantEntity.connect();
    }

    @Override
    public void disconnect(Long userId, String roomSid) {
        queryFactory.update(participantEntity)
            .set(participantEntity.isConnected, false)
            .where(participantEntity.userEntity.id.eq(userId)
                .and(participantEntity.chatRoomEntity.id.eq(
                    JPAExpressions.select(chatRoomEntity.id)
                        .from(chatRoomEntity)
                        .where(chatRoomEntity.roomSid.eq(roomSid)))))
            .execute();
    }
}
