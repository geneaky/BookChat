package toy.bookchat.bookchat.domain.participant.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.participant.ParticipantStatus;
import toy.bookchat.bookchat.domain.participant.service.ParticipantService;
import toy.bookchat.bookchat.domain.participant.service.dto.ChatRoomParticipantsResponse;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPayload;

@RestController
@RequestMapping("/v1/api")
public class ParticipantController {

    private final ParticipantService participantService;

    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @GetMapping("/chatrooms/{roomId}/participants")
    public ChatRoomParticipantsResponse getChatRoomUsers(@PathVariable Long roomId,
        @UserPayload TokenPayload tokenPayload) {
        return participantService.getChatRoomUsers(roomId, tokenPayload.getUserId());
    }

    @PatchMapping("/chatrooms/{roomId}/participants/{userId}")
    public void changeParticipantRights(@PathVariable Long roomId, @PathVariable Long userId,
        ParticipantStatus participantStatus, @UserPayload TokenPayload tokenPayload) {
        participantService.changeParticipantRights(roomId, userId, participantStatus,
            tokenPayload.getUserId());
    }
}
