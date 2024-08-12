package toy.bookchat.bookchat.db_module.participant.repository.query;

import java.util.Optional;
import toy.bookchat.bookchat.db_module.participant.ParticipantEntity;
import toy.bookchat.bookchat.domain.participant.ParticipantStatus;

public interface ParticipantQueryRepository {

  Optional<ParticipantEntity> findByUserIdAndChatRoomId(Long userId, Long chatRoomId);

  Optional<ParticipantEntity> findHostWithPessimisticLockByUserIdAndChatRoomId(Long userId, Long chatRoomId);

  Long countByRoomIdAndParticipantStatus(Long roomId, ParticipantStatus participantStatus);

  void disconnectAllByUserId(Long userId);

  Optional<ParticipantEntity> findByUserIdAndChatRoomSid(Long userId, String roomSid);
}
