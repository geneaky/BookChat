package toy.bookchat.bookchat.domain.user;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import toy.bookchat.bookchat.db_module.user.UserEntity;

@Getter
@EqualsAndHashCode
public class UserProfile {

    private Long userId;
    private String userNickname;
    private String userEmail;
    private String userProfileImageUri;
    private Integer defaultProfileImageType;

    @Builder
    private UserProfile(Long userId, String userNickname, String userEmail, String userProfileImageUri, Integer defaultProfileImageType) {
        this.userId = userId;
        this.userNickname = userNickname;
        this.userEmail = userEmail;
        this.userProfileImageUri = userProfileImageUri;
        this.defaultProfileImageType = defaultProfileImageType;
    }

    public static UserProfile from(UserEntity userEntity) {
        return UserProfile.builder()
            .userId(userEntity.getId())
            .userNickname(userEntity.getNickname())
            .userEmail(userEntity.getEmail())
            .userProfileImageUri(userEntity.getProfileImageUrl())
            .defaultProfileImageType(userEntity.getDefaultProfileImageType())
            .build();
    }
}
