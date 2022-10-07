package toy.bookchat.bookchat.domain.user.service;

import toy.bookchat.bookchat.domain.user.service.dto.UserSignUpRequestDto;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;


public interface UserService {

    boolean isDuplicatedName(String nickname);

    void registerNewUser(UserSignUpRequestDto userSignUpRequestDto, String oauth2MemberNumber,
        String userEmail, OAuth2Provider providerType);

    void checkRegisteredUser(String oauth2MemberNumber);
}
