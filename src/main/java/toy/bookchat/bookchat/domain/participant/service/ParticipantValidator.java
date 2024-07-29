package toy.bookchat.bookchat.domain.participant.service;

import java.util.List;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.participant.ParticipantEntity;
import toy.bookchat.bookchat.db_module.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.exception.badrequest.chatroom.ChatRoomIsFullException;
import toy.bookchat.bookchat.exception.badrequest.participant.AlreadyParticipateException;
import toy.bookchat.bookchat.exception.notfound.pariticipant.ParticipantNotFoundException;

@Component
public class ParticipantValidator {

  private final ParticipantRepository participantRepository;

  public ParticipantValidator(ParticipantRepository participantRepository) {
    this.participantRepository = participantRepository;
  }

  public void checkDoesUserParticipate(Long userId, Long roomId) {
    participantRepository.findByUserIdAndChatRoomId(userId, roomId).orElseThrow(ParticipantNotFoundException::new);
  }

  public void checkDoesUserAlreadyParticipate(Long userId, Long roomId) {
    participantRepository.findByUserIdAndChatRoomId(userId, roomId).ifPresent(participant -> {
      throw new AlreadyParticipateException();
    });
  }

  public void checkIsChatRoomFull(ChatRoom chatRoom) {
    List<ParticipantEntity> participantEntities = participantRepository.findByChatRoomId(chatRoom.getId());

    if (chatRoom.getRoomSize() <= participantEntities.size()) {
      throw new ChatRoomIsFullException();
    }
  }
}
