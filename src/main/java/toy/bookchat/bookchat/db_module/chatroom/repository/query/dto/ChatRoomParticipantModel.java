package toy.bookchat.bookchat.db_module.chatroom.repository.query.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import toy.bookchat.bookchat.domain.participant.ParticipantStatus;

@Getter
public class ChatRoomParticipantModel {

  private Long userId;
  private String nickname;
  private String profileImageUrl;
  private Integer defaultProfileImageType;
  private ParticipantStatus status;

  @QueryProjection
  public ChatRoomParticipantModel(Long userId, String nickname, String profileImageUrl,
      Integer defaultProfileImageType, ParticipantStatus status) {
    this.userId = userId;
    this.nickname = nickname;
    this.profileImageUrl = profileImageUrl;
    this.defaultProfileImageType = defaultProfileImageType;
    this.status = status;
  }
}
