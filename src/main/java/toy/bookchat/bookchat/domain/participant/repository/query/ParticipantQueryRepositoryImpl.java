package toy.bookchat.bookchat.domain.participant.repository.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import toy.bookchat.bookchat.domain.participant.Participant;

import javax.persistence.LockModeType;
import java.util.Optional;

import static toy.bookchat.bookchat.domain.chatroom.QChatRoom.chatRoom;
import static toy.bookchat.bookchat.domain.participant.QParticipant.participant;
import static toy.bookchat.bookchat.domain.user.QUser.user;

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
    public Optional<Participant> findWithPessimisticLockByUserIdAndChatRoomId(Long userId, Long chatRoomId) {
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
}
