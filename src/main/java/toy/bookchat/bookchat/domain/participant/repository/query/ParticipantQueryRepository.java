package toy.bookchat.bookchat.domain.participant.repository.query;

import toy.bookchat.bookchat.domain.participant.Participant;

import java.util.Optional;

public interface ParticipantQueryRepository {

    Optional<Participant> findByUserIdAndChatRoomId(Long userId, Long chatRoomId);

    Optional<Participant> findWithPessimisticLockByUserIdAndChatRoomId(Long userId, Long chatRoomId);
}
