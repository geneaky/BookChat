package toy.bookchat.bookchat.domain.chat.api.v1.response;

import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.domain.participant.Host;

@Getter
public class ChatRoomHostResponse {

  private Long id;
  private String nickname;
  private String profileImageUrl;
  private Integer defaultProfileImageType;

  @Builder
  private ChatRoomHostResponse(Long id, String nickname, String profileImageUrl, Integer defaultProfileImageType) {
    this.id = id;
    this.nickname = nickname;
    this.profileImageUrl = profileImageUrl;
    this.defaultProfileImageType = defaultProfileImageType;
  }

  public static ChatRoomHostResponse from(Host host) {
    return ChatRoomHostResponse.builder()
        .id(host.getUserId())
        .nickname(host.getNickname())
        .profileImageUrl(host.getProfileImageUrl())
        .defaultProfileImageType(host.getDefaultProfileImageType())
        .build();
  }
}
