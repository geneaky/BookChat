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

    public User getUser(String oauth2MemberNumber, String email, String profileImageUrl, OAuth2Provider providerType) {
        return new User(oauth2MemberNumber, email, profileImageUrl, ROLE.USER,
            providerType, this.getNickname(), this.getReadingTastes(),
            this.getDefaultProfileImageType());
    }

}
