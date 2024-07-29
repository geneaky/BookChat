package toy.bookchat.bookchat.domain.participant.service;

import javax.transaction.Transactional;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.participant.ParticipantEntity;
import toy.bookchat.bookchat.db_module.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.participant.Host;
import toy.bookchat.bookchat.domain.participant.ParticipantWithChatRoom;
import toy.bookchat.bookchat.exception.notfound.pariticipant.ParticipantNotFoundException;

@Component
public class ParticipantManager {

  private final ParticipantRepository participantRepository;

  public ParticipantManager(ParticipantRepository participantRepository) {
    this.participantRepository = participantRepository;
  }


  @Transactional
  public void connect(Long userId, String roomSid) {
    ParticipantEntity participantEntity = participantRepository.findByUserIdAndChatRoomSid(userId, roomSid)
        .orElseThrow(ParticipantNotFoundException::new);

    participantEntity.connect();
  }

  @Transactional
  public void disconnect(Long userId, String roomSid) {
    ParticipantEntity participantEntity = participantRepository.findByUserIdAndChatRoomSid(userId, roomSid)
        .orElseThrow(ParticipantNotFoundException::new);

    participantEntity.disconnect();
  }

  @Transactional
  public void disconnectAll(Long userId) {
    participantRepository.disconnectAllByUserId(userId);
  }

  @Transactional
  public void update(ParticipantWithChatRoom participantWithChatRoom) {
    ParticipantEntity participantEntity = participantRepository.findById(participantWithChatRoom.getParticipantId())
        .orElseThrow(ParticipantNotFoundException::new);

    participantEntity.changeStatus(participantWithChatRoom.getStatus());
  }

  @Transactional
  public void update(Host host) {
    ParticipantEntity participantEntity = participantRepository.findById(host.getId())
        .orElseThrow(ParticipantNotFoundException::new);

    participantEntity.changeStatus(host.getStatus());
  }
}
