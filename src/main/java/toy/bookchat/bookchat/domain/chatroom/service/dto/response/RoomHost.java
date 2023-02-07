package toy.bookchat.bookchat.domain.chatroom.service.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RoomHost {

    private final Long id;
    private final String nickname;
    private final String profileImageUrl;
    private final Long defaultProfileImageType;

    @Builder
    private RoomHost(Long id, String nickname, String profileImageUrl,
        Long defaultProfileImageType) {
        this.id = id;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.defaultProfileImageType = defaultProfileImageType;
    }
}
