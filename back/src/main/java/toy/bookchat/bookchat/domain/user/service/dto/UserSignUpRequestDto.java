package toy.bookchat.bookchat.domain.user.service.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import toy.bookchat.bookchat.domain.user.ReadingTaste;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSignUpRequestDto {

    @NotBlank
    String nickname;
    @Email
    String userEmail;
    MultipartFile userProfileImage;
    List<ReadingTaste> readingTastes;
}
