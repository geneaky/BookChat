package toy.bookchat.bookchat.domain.chatroom.repository.query.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RoomGuest {

    private final Long id;
    private final String nickname;
    private final String profileImageUrl;
    private final Long defaultProfileImageType;

    @Builder
    private RoomGuest(Long id, String nickname, String profileImageUrl,
        Long defaultProfileImageType) {
        this.id = id;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.defaultProfileImageType = defaultProfileImageType;
    }
}
