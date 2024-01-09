package toy.bookchat.bookchat.domain.user.service;

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
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }
}
