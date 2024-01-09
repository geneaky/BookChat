package toy.bookchat.bookchat.domain.user.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.exception.notfound.user.UserNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserReaderTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserReader userReader;

    @Test
    void 유저id로_유저를_찾을수없으면_예외발생() throws Exception {
        assertThatThrownBy(() -> {
            userReader.readUser(1L);
        }).isInstanceOf(UserNotFoundException.class);
    }
}