package toy.bookchat.bookchat.domain.user.service.dto;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import toy.bookchat.bookchat.domain.user.ROLE;
import toy.bookchat.bookchat.domain.user.ReadingTaste;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSignUpRequestDto {

    @NotBlank
    String nickname;
    MultipartFile userProfileImage;
    List<ReadingTaste> readingTastes;
    @NotNull
    Integer defaultProfileImageType;

    public User getUser(String oauth2MemberNumber, String email, String profileImageUrl,
        OAuth2Provider providerType) {
        return new User(oauth2MemberNumber, email, profileImageUrl, ROLE.USER,
            providerType, this.getNickname(), this.getReadingTastes(),
            this.getDefaultProfileImageType());
    }

}
