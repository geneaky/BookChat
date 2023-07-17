package toy.bookchat.bookchat.domain.participant.repository.query;

import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.participant.Participant;

public interface ParticipantQueryRepository {

    Optional<Participant> findByUserIdAndChatRoomId(Long userId, Long chatRoomId);

    Optional<Participant> findWithPessimisticLockByUserIdAndChatRoomId(Long userId,
        Long chatRoomId);

    Long countSubHostByRoomId(Long roomId);

    @Transactional
    void disconnectAll(String name);

    @Transactional
    void connect(Long userId, String roomSid);

    @Transactional
    void disconnect(Long userId, String roomSid);
}
