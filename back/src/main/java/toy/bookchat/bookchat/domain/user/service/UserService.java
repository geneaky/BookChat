package toy.bookchat.bookchat.domain.user.service;

import toy.bookchat.bookchat.domain.user.service.dto.UserSignUpRequestDto;

public interface UserService {
    boolean isDuplicatedName(String nickname);

    void registerNewUser(UserSignUpRequestDto userSignUpRequestDto);
}
