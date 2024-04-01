package toy.bookchat.bookchat.domain.chat.api.dto.response;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import toy.bookchat.bookchat.domain.user.User;

@Getter
@EqualsAndHashCode
public class ChatSender {

    private Long id;
    private String nickname;
    private String profileImageUrl;
    private Integer defaultProfileImageType;

    @Builder
    private ChatSender(Long id, String nickname, String profileImageUrl, Integer defaultProfileImageType) {
        this.id = id;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.defaultProfileImageType = defaultProfileImageType;
    }

    public static ChatSender from(User user) {
        return ChatSender.builder()
            .id(user.getId())
            .nickname(user.getNickname())
            .profileImageUrl(user.getProfileImageUrl())
            .defaultProfileImageType(user.getDefaultProfileImageType())
            .build();
    }
}
