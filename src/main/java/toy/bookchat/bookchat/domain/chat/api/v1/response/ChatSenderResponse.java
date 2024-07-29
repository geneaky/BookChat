package toy.bookchat.bookchat.domain.chat.api.v1.response;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import toy.bookchat.bookchat.domain.chat.Sender;

@Getter
@EqualsAndHashCode
public class ChatSenderResponse {

    private Long id;
    private String nickname;
    private String profileImageUrl;
    private Integer defaultProfileImageType;

    @Builder
    private ChatSenderResponse(Long id, String nickname, String profileImageUrl, Integer defaultProfileImageType) {
        this.id = id;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.defaultProfileImageType = defaultProfileImageType;
    }

    public static ChatSenderResponse from(Sender sender) {
        return ChatSenderResponse.builder()
            .id(sender.getId())
            .nickname(sender.getNickname())
            .profileImageUrl(sender.getProfileImageUrl())
            .defaultProfileImageType(sender.getDefaultProfileImageType())
            .build();
    }
}
