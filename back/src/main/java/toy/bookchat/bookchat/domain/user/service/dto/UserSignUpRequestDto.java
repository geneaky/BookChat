package toy.bookchat.bookchat.domain.user.service.dto;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.validation.constraints.Email;
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
import toy.bookchat.bookchat.domain.user.exception.ImageInputStreamException;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSignUpRequestDto {

    public static final int WIDTH_LIMIT = 150;
    public static final int HEIGHT_LIMIT = 150;

    @NotBlank
    String nickname;
    // TODO: 2022/09/16 userEmail, oauth2Provider는 openidtoken이랑
    //  request header에 넣어주기로 했었는데 생각해보니 그럼 여기서는 지워도되네
    @Email
    String userEmail;
    OAuth2Provider oauth2Provider;
    MultipartFile userProfileImage;
    List<ReadingTaste> readingTastes;
    @NotNull
    Integer defaultProfileImageType;

    public boolean hasValidImage() {

        try {
            if (this.userProfileImage != null) {
                BufferedImage bufferedImage = ImageIO.read(this.userProfileImage.getInputStream());
                return bufferedImage.getWidth() == WIDTH_LIMIT
                    && bufferedImage.getHeight() == HEIGHT_LIMIT;
            }
        } catch (IOException exception) {
            throw new ImageInputStreamException(exception.getMessage(), exception.getCause());
        }
        return false;
    }

    public User getUser(String oauth2MemberNumber, String profileImageUrl) {
        return new User(oauth2MemberNumber, this.getUserEmail(), profileImageUrl, ROLE.USER,
            this.getOAuth2Provider(), this.getNickname(), this.getReadingTastes(),
            this.getDefaultProfileImageType());
    }

    public OAuth2Provider getOAuth2Provider() {
        return this.oauth2Provider;
    }
}
