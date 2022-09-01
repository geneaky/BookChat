package toy.bookchat.bookchat.domain.user.service.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import toy.bookchat.bookchat.domain.user.ROLE;
import toy.bookchat.bookchat.domain.user.ReadingTaste;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.exception.ImageInputStreamException;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

import javax.imageio.ImageIO;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSignUpRequestDto {

    public static final int WIDTH_LIMIT = 150;
    public static final int HEIGHT_LIMIT = 150;
    /**
     * @param oauth2Provider
     * enum으로 넘겨받을까 했지만 결국 enum에 @JasonCleator @JasonValue 같은
     * jackson 의존성이 생기기 때문에 이렇게 정규표현식으로 작성하여 validation이 가능하도록함
     */
    @NotBlank
    String nickname;
    @Email
    String userEmail;
    @NotBlank
    @Pattern(regexp = "(kakao|google)")
    String oauth2Provider;
    MultipartFile userProfileImage;
    List<ReadingTaste> readingTastes;
    @NotNull
    Integer defaultProfileImageType;

    public boolean hasValidImage() {

        try {
            if (this.userProfileImage != null) {
                BufferedImage bufferedImage = ImageIO.read(this.userProfileImage.getInputStream());
                return bufferedImage.getWidth() == WIDTH_LIMIT && bufferedImage.getHeight() == HEIGHT_LIMIT;
            }
        } catch (IOException exception) {
            throw new ImageInputStreamException(exception.getMessage(), exception.getCause());
        }
        return false;
    }

    public User getUser(String oauth2MemberNumber, String profileImageUrl) {
        return new User(oauth2MemberNumber, this.getUserEmail(), profileImageUrl, ROLE.USER, this.getOAuth2Provider(), this.getNickname(), this.getReadingTastes(), this.getDefaultProfileImageType());
    }

    public OAuth2Provider getOAuth2Provider() {
        return OAuth2Provider.from(this.oauth2Provider);
    }
}
