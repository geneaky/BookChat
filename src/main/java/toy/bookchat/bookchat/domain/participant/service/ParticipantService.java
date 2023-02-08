package toy.bookchat.bookchat.domain.participant.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.participant.service.dto.ChatRoomUsersResponse;

@Service
public class ParticipantService {

    @Transactional(readOnly = true)
    public ChatRoomUsersResponse getChatRoomUsers(Long roomId, Long userId) {
        return null;
    }
}
