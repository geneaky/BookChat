package toy.bookchat.bookchat.domain.participant.service;

import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.GUEST;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.participant.ParticipantEntity;
import toy.bookchat.bookchat.db_module.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.service.ChatRoomReader;
import toy.bookchat.bookchat.domain.participant.Host;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.ParticipantAdmin;
import toy.bookchat.bookchat.domain.participant.ParticipantStatus;
import toy.bookchat.bookchat.domain.participant.ParticipantWithChatRoom;
import toy.bookchat.bookchat.exception.forbidden.participant.NoPermissionParticipantException;
import toy.bookchat.bookchat.exception.notfound.pariticipant.ParticipantNotFoundException;

@Component
public class ParticipantReader {

  private final ParticipantRepository participantRepository;
  private final ChatRoomReader chatRoomReader;

  public ParticipantReader(ParticipantRepository participantRepository, ChatRoomReader chatRoomReader) {
    this.participantRepository = participantRepository;
    this.chatRoomReader = chatRoomReader;
  }

  public Participant readParticipant(Long userId, Long roomId) {
    ParticipantEntity participantEntity = participantRepository.findByUserIdAndChatRoomId(userId, roomId)
        .orElseThrow(ParticipantNotFoundException::new);

    return Participant.builder()
        .id(participantEntity.getId())
        .userId(participantEntity.getUserId())
        .chatRoomId(participantEntity.getChatRoomId())
        .build();
  }

  public ParticipantWithChatRoom readParticipantWithChatRoom(Long userId, Long roomId) {
    ParticipantEntity participantEntity = participantRepository.findByUserIdAndChatRoomId(userId, roomId)
        .orElseThrow(ParticipantNotFoundException::new);
    ChatRoom chatRoom = chatRoomReader.readChatRoom(roomId);

    Participant participant = Participant.builder()
        .id(participantEntity.getId())
        .userId(participantEntity.getUserId())
        .chatRoomId(roomId)
        .status(participantEntity.getParticipantStatus())
        .build();

    return ParticipantWithChatRoom.builder()
        .participant(participant)
        .chatRoom(chatRoom)
        .build();
  }

  public List<Participant> readParticipantWithChatRoom(Long userId) {
    return participantRepository.findByUserId(userId).stream().map(participantEntity ->
        Participant.builder()
            .id(participantEntity.getId())
            .userId(participantEntity.getUserId())
            .chatRoomId(participantEntity.getChatRoomId())
            .status(participantEntity.getParticipantStatus())
            .build()).collect(Collectors.toList());
  }

  public Host readHostForUpdate(Long roomId, Long requesterId) {
    ParticipantEntity participantEntity = participantRepository.findHostWithPessimisticLockByUserIdAndChatRoomId(
        requesterId, roomId).orElseThrow(NoPermissionParticipantException::new);

    return Host.builder()
        .id(participantEntity.getId())
        .userId(participantEntity.getUserId())
        .chatRoomId(participantEntity.getChatRoomId())
        .build();
  }

  public Long readParticipantCount(Long roomId, ParticipantStatus participantStatus) {
    return participantRepository.countByRoomIdAndParticipantStatus(roomId, participantStatus);
  }

  public ParticipantAdmin readAdmin(Long adminId, Long roomId) {
    ParticipantEntity participantEntity = participantRepository.findByUserIdAndChatRoomId(adminId, roomId)
        .orElseThrow(ParticipantNotFoundException::new);

    if (participantEntity.getParticipantStatus() == GUEST) {
      throw new NoPermissionParticipantException();
    }

    return ParticipantAdmin.builder()
        .userId(participantEntity.getUserId())
        .status(participantEntity.getParticipantStatus())
        .build();
  }

  public Long readParticipantCount(ChatRoom chatRoom) {
    return participantRepository.countByChatRoomId(chatRoom.getId());
  }

  public Host readHost(Long roomId) {
    ParticipantEntity participantEntity = participantRepository.findByChatRoomIdAndParticipantStatus(roomId,
            ParticipantStatus.HOST)
        .orElseThrow(ParticipantNotFoundException::new);

    return Host.builder()
        .id(participantEntity.getId())
        .chatRoomId(participantEntity.getChatRoomId())
        .userId(participantEntity.getUserId())
        .status(participantEntity.getParticipantStatus())
        .build();
  }
}
