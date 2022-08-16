package toy.bookchat.bookchat.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public boolean isDuplicatedName(String nickname) {
        return userRepository.existsByNickName(nickname);
    }
}
