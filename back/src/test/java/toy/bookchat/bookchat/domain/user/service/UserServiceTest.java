package toy.bookchat.bookchat.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserServiceImpl userService;

    @Test
    public void 사용자_중복된_nickname_체크() throws Exception {

        when(userRepository.existsByNickName(anyString())).thenReturn(true);
        boolean result = userService.isDuplicatedName("test");
        assertThat(result).isTrue();
    }

    @Test
    public void 사용자가_중복되지_않은_nickname_체크() throws Exception {
        when(userRepository.existsByNickName(anyString())).thenReturn(false);
        boolean result = userService.isDuplicatedName("test");
        assertThat(result).isFalse();
    }
}