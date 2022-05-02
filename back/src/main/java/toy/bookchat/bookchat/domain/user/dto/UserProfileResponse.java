package toy.bookchat.bookchat.domain.user.dto;

import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.security.user.UserPrincipal;

@Getter
@Builder
public class UserProfileResponse {

    private String username;
    private String userEmail;
    private String userProfileImageUri;

    public static UserProfileResponse of(UserPrincipal userPrincipal) {
        return UserProfileResponse.builder()
            .username(userPrincipal.getName())
            .userEmail(userPrincipal.getEmail())
            .userProfileImageUri(userPrincipal.getProfileImageUri())
            .build();
    }
}
