package toy.bookchat.bookchat.domain.participant.service;

import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.participant.Participant;

@Component
public class ParticipantCleaner {

  private final ParticipantRepository participantRepository;

  public ParticipantCleaner(ParticipantRepository participantRepository) {
    this.participantRepository = participantRepository;
  }


  public void cleanBy(ChatRoom chatRoom) {
    participantRepository.deleteByChatRoomId(chatRoom.getId());
  }

  public void clean(Participant participant) {
    participantRepository.deleteById(participant.getId());
  }
}
