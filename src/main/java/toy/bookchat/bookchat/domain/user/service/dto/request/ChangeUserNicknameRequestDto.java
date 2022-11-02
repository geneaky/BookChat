package toy.bookchat.bookchat.domain.user.service.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChangeUserNicknameRequestDto {

    @NotBlank
    private String nickname;

    public ChangeUserNicknameRequestDto(String nickname) {
        this.nickname = nickname;
    }
}
