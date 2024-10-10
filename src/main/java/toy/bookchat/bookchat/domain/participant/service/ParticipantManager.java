package toy.bookchat.bookchat.domain.participant.service;

import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.db_module.participant.ParticipantEntity;
import toy.bookchat.bookchat.db_module.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.participant.Host;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.ParticipantWithChatRoom;
import toy.bookchat.bookchat.exception.notfound.pariticipant.ParticipantNotFoundException;

@Component
public class ParticipantManager {

  private final ParticipantRepository participantRepository;
  private final ChatRoomRepository chatRoomRepository;

  public ParticipantManager(ParticipantRepository participantRepository, ChatRoomRepository chatRoomRepository) {
    this.participantRepository = participantRepository;
    this.chatRoomRepository = chatRoomRepository;
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

  @Transactional
  public void deleteAll(List<Participant> guestOrSubHostList) {
    List<Long> participantIds = guestOrSubHostList.stream().map(Participant::getId).collect(Collectors.toList());
    participantRepository.deleteAllByIdIn(participantIds);
  }

  @Transactional
  public void deleteAllWithChatRoom(List<Long> chatRoomIds) {
    participantRepository.deleteAllByChatRoomIdIn(chatRoomIds);
    chatRoomRepository.deleteAllById(chatRoomIds);
  }
}
