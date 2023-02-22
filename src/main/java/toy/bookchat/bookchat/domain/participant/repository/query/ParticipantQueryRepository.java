package toy.bookchat.bookchat.domain.participant.repository.query;

import java.util.List;
import java.util.Optional;
import toy.bookchat.bookchat.domain.participant.Participant;

public interface ParticipantQueryRepository {

    List<Participant> findChatRoomUsers(Long roomId, Long userId);

    Optional<Participant> findByUserIdAndChatRoomId(Long userId, Long chatRoomId);
}
