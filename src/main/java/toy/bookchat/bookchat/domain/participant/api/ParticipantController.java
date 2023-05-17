package toy.bookchat.bookchat.domain.participant.api;

import org.springframework.web.bind.annotation.*;
import toy.bookchat.bookchat.domain.participant.ParticipantStatus;
import toy.bookchat.bookchat.domain.participant.service.ParticipantService;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPayload;

@RestController
@RequestMapping("/v1/api")
public class ParticipantController {

    private final ParticipantService participantService;

    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @PatchMapping("/chatrooms/{roomId}/participants/{userId}")
    public void changeParticipantRights(@PathVariable Long roomId, @PathVariable Long userId,
                                        ParticipantStatus participantStatus, @UserPayload TokenPayload tokenPayload) {
        participantService.changeParticipantRights(roomId, userId, participantStatus,
                tokenPayload.getUserId());
    }

    @DeleteMapping("/chatrooms/{roomId}/participants/{userId}")
    public void deleteParticipant(@PathVariable Long roomId, @PathVariable Long userId,
                                  @UserPayload TokenPayload tokenPayload) {
        participantService.kickParticipant(roomId, userId, tokenPayload.getUserId());
    }
}
