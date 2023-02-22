package toy.bookchat.bookchat.domain.chat.service.cache;

import lombok.Getter;
import toy.bookchat.bookchat.domain.user.User;

@Getter
public class UserCache {

    private final Long userId;
    private final String userNickname;
    private final String profileImageUrl;
    private final Integer defaultProfileImageType;

    private UserCache(Long userId, String userNickname, String profileImageUrl,
        Integer defaultProfileImageType) {
        this.userId = userId;
        this.userNickname = userNickname;
        this.profileImageUrl = profileImageUrl;
        this.defaultProfileImageType = defaultProfileImageType;
    }

    public static UserCache of(User user) {
        return new UserCache(user.getId(), user.getNickname(), user.getProfileImageUrl(),
            user.getDefaultProfileImageType());
    }
}
