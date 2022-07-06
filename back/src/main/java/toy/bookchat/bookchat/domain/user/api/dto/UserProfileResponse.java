package toy.bookchat.bookchat.domain.user.api.dto;

import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.security.user.UserPrincipal;

@Getter
@Builder
public class UserProfileResponse {

    private String userName;
    private String userEmail;
    private String userProfileImageUri;

    public static UserProfileResponse of(UserPrincipal userPrincipal) {
        return UserProfileResponse.builder()
            .userName(userPrincipal.getName())
            .userEmail(userPrincipal.getEmail())
            .userProfileImageUri(userPrincipal.getProfileImageUri())
            .build();
    }
}
