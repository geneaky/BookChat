package toy.bookchat.bookchat.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import toy.bookchat.bookchat.domain.storage.StorageService;
import toy.bookchat.bookchat.domain.user.ReadingTaste;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.domain.user.service.dto.UserSignUpRequestDto;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    StorageService storageService;
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

    @Test
    public void 회원가입_성공() throws Exception {
        UserSignUpRequestDto userSignUpRequestDto = mock(UserSignUpRequestDto.class);

        when(userSignUpRequestDto.hasValidImage()).thenReturn(true);
        userService.registerNewUser(userSignUpRequestDto, "memberNumber");

        verify(userRepository).save(any(User.class));
        verify(storageService).upload(userSignUpRequestDto.getUserProfileImage());
    }

}