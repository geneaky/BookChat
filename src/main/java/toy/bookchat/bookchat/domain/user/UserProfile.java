package toy.bookchat.bookchat.domain.user;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

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

    public static UserProfile from(User user) {
        return UserProfile.builder()
            .userId(user.getId())
            .userNickname(user.getNickname())
            .userEmail(user.getEmail())
            .userProfileImageUri(user.getProfileImageUrl())
            .defaultProfileImageType(user.getDefaultProfileImageType())
            .build();
    }
}
