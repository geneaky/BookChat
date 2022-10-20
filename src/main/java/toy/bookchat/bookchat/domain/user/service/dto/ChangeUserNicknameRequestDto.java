package toy.bookchat.bookchat.domain.user.service.dto;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChangeUserNicknameRequestDto {

    @NotBlank
    private String nickname;
}
