package toy.bookchat.bookchat.domain.participant.api.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.participant.ParticipantStatus;
import toy.bookchat.bookchat.domain.participant.service.ParticipantService;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPayload;

@RequiredArgsConstructor

@RestController
@RequestMapping("/v1/api/chatrooms")
public class ParticipantController {

  private final ParticipantService participantService;

  @PatchMapping("/{roomId}/participants/{userId}")
  public void changeParticipantRights(@PathVariable Long roomId, @PathVariable Long userId,
      ParticipantStatus participantStatus, @UserPayload TokenPayload tokenPayload) {
    participantService.changeParticipantRights(roomId, userId, participantStatus, tokenPayload.getUserId());
  }

  @DeleteMapping("/{roomId}/participants/{userId}")
  public void deleteParticipant(@PathVariable Long roomId, @PathVariable Long userId,
      @UserPayload TokenPayload tokenPayload) {
    participantService.kickParticipant(roomId, userId, tokenPayload.getUserId());
  }
}
