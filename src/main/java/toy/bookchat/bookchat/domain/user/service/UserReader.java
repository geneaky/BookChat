package toy.bookchat.bookchat.domain.user.service;

import static toy.bookchat.bookchat.domain.common.Status.ACTIVE;

import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.exception.notfound.user.UserNotFoundException;

@Component
public class UserReader {

    private final UserRepository userRepository;

    public UserReader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User readUser(Long userId) {
        return userRepository.findByIdAndStatus(userId, ACTIVE).orElseThrow(UserNotFoundException::new);
    }

    public User readUser(String oauth2MemberNumber) {
        return userRepository.findByNameAndStatus(oauth2MemberNumber, ACTIVE)
            .orElseThrow(UserNotFoundException::new);

    }
}
