package toy.bookchat.bookchat.domain.user.service;

import org.springframework.web.multipart.MultipartFile;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.service.dto.ChangeUserNicknameRequestDto;
import toy.bookchat.bookchat.domain.user.service.dto.UserSignUpRequestDto;


public interface UserService {

    boolean isDuplicatedName(String nickname);

    void registerNewUser(UserSignUpRequestDto userSignUpRequestDto, MultipartFile userProfileImage,
        String oauth2MemberNumber, String userEmail);

    void checkRegisteredUser(String oauth2MemberNumber);

    void deleteUser(User user);

    void changeUserNickname(ChangeUserNicknameRequestDto changeUserNicknameRequestDto, User user);
}
