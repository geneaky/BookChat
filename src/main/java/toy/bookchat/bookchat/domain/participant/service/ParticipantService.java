package toy.bookchat.bookchat.domain.participant.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.participant.service.dto.ChatRoomParticipantsResponse;

@Service
public class ParticipantService {

    private final ParticipantRepository participantRepository;

    public ParticipantService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    @Transactional(readOnly = true)
    public ChatRoomParticipantsResponse getChatRoomUsers(Long roomId, Long userId) {
        return ChatRoomParticipantsResponse.from(
            participantRepository.findChatRoomUsers(roomId, userId));
    }
}
