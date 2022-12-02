package toy.bookchat.bookchat.security.user;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import toy.bookchat.bookchat.domain.user.ROLE;

@Getter
@EqualsAndHashCode
public class TokenPayload {

    private final Long userId;
    private final String userName;
    private final String userNickname;
    private final String userEmail;
    private final Integer defaultProfileImageType;
    private final ROLE userRole;
    private String userProfileImageUri;

    private TokenPayload(Long userId, String userName, String userNickname, String userEmail,
        String userProfileImageUri, Integer defaultProfileImageType, ROLE userRole) {
        this.userId = userId;
        this.userName = userName;
        this.userNickname = userNickname;
        this.userEmail = userEmail;
        this.userProfileImageUri = userProfileImageUri;
        this.defaultProfileImageType = defaultProfileImageType;
        this.userRole = userRole;
    }

    public static TokenPayload of(Long userId, String userName, String userNickname,
        String userEmail, String userProfileImageUri, Integer defaultProfileImageType,
        ROLE userRole) {

        return new TokenPayload(userId, userName, userNickname, userEmail, userProfileImageUri,
            defaultProfileImageType, userRole);
    }
}
