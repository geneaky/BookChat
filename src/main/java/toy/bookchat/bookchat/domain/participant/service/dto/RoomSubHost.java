package toy.bookchat.bookchat.domain.participant.service.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class RoomSubHost {

    private final Long id;
    private final String nickname;
    private final String profileImageUrl;
    private final Integer defaultProfileImageType;

    @Builder
    private RoomSubHost(Long id, String nickname, String profileImageUrl,
        Integer defaultProfileImageType) {
        this.id = id;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.defaultProfileImageType = defaultProfileImageType;
    }
}
