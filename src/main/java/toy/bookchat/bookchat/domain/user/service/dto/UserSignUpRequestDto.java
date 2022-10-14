package toy.bookchat.bookchat.domain.user.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import toy.bookchat.bookchat.domain.user.ROLE;
import toy.bookchat.bookchat.domain.user.ReadingTaste;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class UserSignUpRequestDto {

    @NotBlank
    private String nickname;
    @JsonProperty("readingTastes")
    @NotNull
    private List<ReadingTaste> readingTastes;
    @NotNull
    private Integer defaultProfileImageType;
    @NotNull
    @JsonProperty("oauth2Provider")
    private OAuth2Provider oauth2Provider;

    public User getUser(String oauth2MemberNumber, String email, String profileImageUrl,
        OAuth2Provider providerType) {
        return new User(oauth2MemberNumber, email, profileImageUrl, ROLE.USER,
            providerType, this.getNickname(), this.getReadingTastes(),
            this.getDefaultProfileImageType());
    }

}
