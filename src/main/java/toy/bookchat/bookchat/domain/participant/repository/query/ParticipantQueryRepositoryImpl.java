package toy.bookchat.bookchat.domain.participant.repository.query;

import static toy.bookchat.bookchat.domain.chatroom.QChatRoom.chatRoom;
import static toy.bookchat.bookchat.domain.participant.QParticipant.participant;
import static toy.bookchat.bookchat.domain.user.QUser.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import toy.bookchat.bookchat.domain.participant.Participant;

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
}
