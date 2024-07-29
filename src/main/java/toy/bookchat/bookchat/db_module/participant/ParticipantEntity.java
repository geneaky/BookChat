package toy.bookchat.bookchat.db_module.participant;

import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.GUEST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.HOST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.SUBHOST;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.domain.participant.ParticipantStatus;

@Getter
@Entity
@Table(name = "participant")
public class ParticipantEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ParticipantStatus participantStatus;
  @Column(name = "user_id")
  private Long userId;
  @Column(name = "chat_room_id")
  private Long chatRoomId;
  private Boolean isConnected;

  protected ParticipantEntity() {
  }

  @Builder
  private ParticipantEntity(Long id, ParticipantStatus participantStatus, Long userId, Long chatRoomId,
      Boolean isConnected) {
    this.id = id;
    this.participantStatus = participantStatus;
    this.userId = userId;
    this.chatRoomId = chatRoomId;
    this.isConnected = isConnected;
  }


  public String getUserNickname() {
//        return this.userEntity.getNickname();
    return null;
  }

  public String getUserProfileImageUrl() {
//        return this.userEntity.getProfileImageUrl();
    return null;
  }

  public Integer getUserDefaultProfileImageType() {
//        return this.userEntity.getDefaultProfileImageType();
    return null;
  }

  public boolean isSubHost() {
    return this.participantStatus == SUBHOST;
  }

  public boolean isHost() {
    return this.participantStatus == HOST;
  }

  public boolean isNotHost() {
    return this.participantStatus != HOST;
  }

  public boolean isGuest() {
    return this.participantStatus == GUEST;
  }

  public String getChatRoomSid() {
//        return this.chatRoomEntity.getRoomSid();
    return null;
  }

  public boolean isNotSubHost() {
    return this.participantStatus != SUBHOST;
  }

  public void connect() {
    this.isConnected = true;
  }

  public void disconnect() {
    this.isConnected = false;
  }

  public void changeStatus(ParticipantStatus participantStatus) {
    this.participantStatus = participantStatus;
  }
}
