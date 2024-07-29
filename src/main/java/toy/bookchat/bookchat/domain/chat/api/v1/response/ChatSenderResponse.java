package toy.bookchat.bookchat.domain.chat.api.v1.response;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import toy.bookchat.bookchat.db_module.user.UserEntity;

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

    public static ChatSender from(UserEntity userEntity) {
        return ChatSender.builder()
            .id(userEntity.getId())
            .nickname(userEntity.getNickname())
            .profileImageUrl(userEntity.getProfileImageUrl())
            .defaultProfileImageType(userEntity.getDefaultProfileImageType())
            .build();
    }
}
