package toy.bookchat.bookchat.domain.participant.repository.query;

import java.util.List;
import toy.bookchat.bookchat.domain.participant.Participant;

public interface ParticipantQueryRepository {

    List<Participant> findChatRoomUsers(Long roomId, Long userId);
}
