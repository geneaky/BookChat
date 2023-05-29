package toy.bookchat.bookchat.domain.participant.repository.query;

import java.util.Optional;
import toy.bookchat.bookchat.domain.participant.Participant;

public interface ParticipantQueryRepository {

    Optional<Participant> findByUserIdAndChatRoomId(Long userId, Long chatRoomId);

    Optional<Participant> findWithPessimisticLockByUserIdAndChatRoomId(Long userId,
        Long chatRoomId);

    Long countSubHostByRoomId(Long roomId);
}
