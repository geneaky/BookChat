package toy.bookchat.bookchat.domain.participant.repository.query;

import static toy.bookchat.bookchat.domain.chatroom.QChatRoom.chatRoom;
import static toy.bookchat.bookchat.domain.participant.QParticipant.participant;
import static toy.bookchat.bookchat.domain.user.QUser.user;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.springframework.stereotype.Repository;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.QParticipant;

@Repository
public class ParticipantQueryRepositoryImpl implements ParticipantQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ParticipantQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<Participant> findChatRoomUsers(Long roomId, Long userId) {
        QParticipant subParticipant = new QParticipant("subParticipant");

        return queryFactory.select(participant)
            .from(participant)
            .join(participant.user, user).fetchJoin()
            .join(participant.chatRoom, chatRoom).fetchJoin()
            .join(chatRoom.host, user).fetchJoin() //넣고 빼고 테스트
            .where(participant.chatRoom.id.eq(JPAExpressions.select(subParticipant.chatRoom.id)
                .from(subParticipant)
                .where(subParticipant.chatRoom.id.eq(roomId)
                    .and(subParticipant.user.id.eq(userId)))))
            .fetch();
    }
}
