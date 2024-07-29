package toy.bookchat.bookchat.db_module.participant.repository.query;

import java.util.Optional;
import toy.bookchat.bookchat.db_module.participant.ParticipantEntity;

public interface ParticipantQueryRepository {

  Optional<ParticipantEntity> findByUserIdAndChatRoomId(Long userId, Long chatRoomId);

  Optional<ParticipantEntity> findHostWithPessimisticLockByUserIdAndChatRoomId(Long userId, Long chatRoomId);

  Long countSubHostByRoomId(Long roomId);

  void disconnectAllByUserId(Long userId);

  Optional<ParticipantEntity> findByUserIdAndChatRoomSid(Long userId, String roomSid);
}
