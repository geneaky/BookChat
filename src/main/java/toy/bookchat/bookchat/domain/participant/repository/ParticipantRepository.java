package toy.bookchat.bookchat.domain.participant.repository;

import java.util.List;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.repository.query.ParticipantQueryRepository;

public interface ParticipantRepository extends ParticipantQueryRepository,
    JpaRepository<Participant, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Participant> findWithPessimisticLockByChatRoom(ChatRoom chatRoom);

    void deleteByChatRoom(ChatRoom chatRoom);
}
