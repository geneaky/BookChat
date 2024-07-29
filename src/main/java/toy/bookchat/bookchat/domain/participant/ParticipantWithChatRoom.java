package toy.bookchat.bookchat.domain.participant;

import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.GUEST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.HOST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.SUBHOST;

import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;

@Getter
public class ParticipantWithChatRoom {

  private final Participant participant;
  private final ChatRoom chatRoom;

  @Builder
  private ParticipantWithChatRoom(Participant participant, ChatRoom chatRoom) {
    this.participant = participant;
    this.chatRoom = chatRoom;
  }

  public boolean canBeSubHost(ParticipantStatus participantStatus) {
    return participantStatus == SUBHOST && !participant.isSubHost();
  }

  public boolean canBeGuest(ParticipantStatus participantStatus) {
    return participantStatus == GUEST && participant.isSubHost();
  }

  public boolean canBeHost(ParticipantStatus participantStatus) {
    return participantStatus == HOST && participant.isNotHost();
  }

  public Long getChatRoomId() {
    return chatRoom.getId();
  }

  public String getChatRoomSid() {
    return chatRoom.getSid();
  }

  public Long getParticipantId() {
    return participant.getId();
  }

  public void changeStatus(ParticipantStatus participantStatus) {
    participant.changeStatus(participantStatus);
  }

  public ParticipantStatus getStatus() {
    return participant.getStatus();
  }

  public Long getParticipantUserId() {
    return participant.getUserId();
  }

  public boolean isGuest() {
    return getStatus() == GUEST;
  }

  public boolean isNotHost() {
    return getStatus() != HOST;
  }
}
