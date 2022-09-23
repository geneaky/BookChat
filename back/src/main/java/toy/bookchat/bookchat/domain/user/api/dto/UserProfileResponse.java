package toy.bookchat.bookchat.domain.user.api.dto;

import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.security.user.UserPrincipal;

@Getter
@Builder
public class UserProfileResponse {

    private String userNickname;
    private String userEmail;
    private String userProfileImageUri;
    private Integer defaultProfileImageType;

    public static UserProfileResponse of(UserPrincipal userPrincipal) {
        return UserProfileResponse.builder()
            .userNickname(userPrincipal.getNickname())
            .userEmail(userPrincipal.getEmail())
            .userProfileImageUri(userPrincipal.getProfileImageUri())
            .defaultProfileImageType(userPrincipal.getDefaultProfileImageType())
            .build();
    }
}
