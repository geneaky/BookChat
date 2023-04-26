package toy.bookchat.bookchat.domain.user.api.dto;

import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.security.user.TokenPayload;

@Getter
@Builder
public class UserProfileResponse {

    private Long userId;
    private String userNickname;
    private String userEmail;
    private String userProfileImageUri;
    private Integer defaultProfileImageType;

    public static UserProfileResponse of(TokenPayload tokenPayload) {
        return UserProfileResponse.builder()
            .userId(tokenPayload.getUserId())
            .userNickname(tokenPayload.getUserNickname())
            .userEmail(tokenPayload.getUserEmail())
            .userProfileImageUri(tokenPayload.getUserProfileImageUri())
            .defaultProfileImageType(tokenPayload.getDefaultProfileImageType())
            .build();
    }
}
