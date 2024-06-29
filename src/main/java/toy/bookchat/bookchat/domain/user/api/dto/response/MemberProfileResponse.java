package toy.bookchat.bookchat.domain.user.api.dto.response;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import toy.bookchat.bookchat.db_module.user.UserEntity;

@Getter
@EqualsAndHashCode
public class MemberProfileResponse {

    private Long userId;
    private String userNickname;
    private String userEmail;
    private String userProfileImageUri;
    private Integer defaultProfileImageType;

    @Builder
    private MemberProfileResponse(Long userId, String userNickname, String userEmail, String userProfileImageUri, Integer defaultProfileImageType) {
        this.userId = userId;
        this.userNickname = userNickname;
        this.userEmail = userEmail;
        this.userProfileImageUri = userProfileImageUri;
        this.defaultProfileImageType = defaultProfileImageType;
    }

    public static MemberProfileResponse of(UserEntity userEntity) {
        return MemberProfileResponse.builder()
            .userId(userEntity.getId())
            .userNickname(userEntity.getNickname())
            .userEmail(userEntity.getEmail())
            .userProfileImageUri(userEntity.getProfileImageUrl())
            .defaultProfileImageType(userEntity.getDefaultProfileImageType())
            .build();
    }
}
