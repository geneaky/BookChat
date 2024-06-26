package toy.bookchat.bookchat.domain.participant.repository.query;

import static toy.bookchat.bookchat.domain.chatroom.QChatRoom.chatRoom;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.SUBHOST;
import static toy.bookchat.bookchat.domain.participant.QParticipant.participant;
import static toy.bookchat.bookchat.domain.user.QUser.user;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.stereotype.Repository;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.QParticipant;
import toy.bookchat.bookchat.exception.notfound.pariticipant.ParticipantNotFoundException;

@Repository
public class ParticipantQueryRepositoryImpl implements ParticipantQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ParticipantQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Optional<Participant> findByUserIdAndChatRoomId(Long userId, Long chatRoomId) {
        return Optional.ofNullable(
            queryFactory.select(participant)
                .from(participant)
                .join(participant.user, user).fetchJoin()
                .join(participant.chatRoom, chatRoom).fetchJoin()
                .where(participant.user.id.eq(userId)
                    .and(participant.chatRoom.id.eq(chatRoomId)))
                .fetchOne()
        );
    }

    @Override
    public Optional<Participant> findWithPessimisticLockByUserIdAndChatRoomId(Long userId,
        Long chatRoomId) {
        return Optional.ofNullable(
            queryFactory.select(participant)
                .from(participant)
                .join(participant.user, user).fetchJoin()
                .join(participant.chatRoom, chatRoom).fetchJoin()
                .where(participant.user.id.eq(userId)
                    .and(participant.chatRoom.id.eq(chatRoomId)))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetchOne()
        );
    }

    @Override
    public Long countSubHostByRoomId(Long roomId) {
        return queryFactory.select(participant.count())
            .from(participant)
            .where(
                participant.chatRoom.id.eq(roomId).and(participant.participantStatus.eq(SUBHOST)))
            .fetchOne();
    }

    @Override
    public void disconnectAllByUserId(Long userId) {
        queryFactory.update(participant)
            .set(participant.isConnected, false)
            .where(participant.user.id.eq(userId))
            .execute();
    }

    @Override
    public void connect(Long userId, String roomSid) {
        Participant participant = queryFactory.select(QParticipant.participant)
            .from(QParticipant.participant)
            .join(QParticipant.participant.chatRoom, chatRoom)
            .where(QParticipant.participant.user.id.eq(userId)
                .and(chatRoom.roomSid.eq(roomSid)))
            .fetchOne();

        if (participant == null) {
            throw new ParticipantNotFoundException();
        }

        participant.connect();
    }

    @Override
    public void disconnect(Long userId, String roomSid) {
        queryFactory.update(participant)
            .set(participant.isConnected, false)
            .where(participant.user.id.eq(userId)
                .and(participant.chatRoom.id.eq(
                    JPAExpressions.select(chatRoom.id)
                        .from(chatRoom)
                        .where(chatRoom.roomSid.eq(roomSid)))))
            .execute();
    }
}
