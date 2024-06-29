package toy.bookchat.bookchat.db_module.participant.repository.query;

import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.db_module.participant.ParticipantEntity;

public interface ParticipantQueryRepository {

    Optional<ParticipantEntity> findByUserIdAndChatRoomId(Long userId, Long chatRoomId);

    Optional<ParticipantEntity> findWithPessimisticLockByUserIdAndChatRoomId(Long userId,
        Long chatRoomId);

    Long countSubHostByRoomId(Long roomId);

    @Transactional
    void disconnectAllByUserId(Long userId);

    @Transactional
    void connect(Long userId, String roomSid);

    @Transactional
    void disconnect(Long userId, String roomSid);
}
