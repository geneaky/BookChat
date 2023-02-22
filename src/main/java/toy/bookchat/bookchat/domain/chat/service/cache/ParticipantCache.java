package toy.bookchat.bookchat.domain.chat.service.cache;

import lombok.Getter;
import toy.bookchat.bookchat.domain.participant.Participant;

@Getter
public class ParticipantCache {

    private final Long participantId;

    private ParticipantCache(Long participantId) {
        this.participantId = participantId;
    }

    public static ParticipantCache of(Participant participant) {
        return new ParticipantCache(participant.getId());
    }
}
