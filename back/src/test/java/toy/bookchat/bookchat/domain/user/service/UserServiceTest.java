package toy.bookchat.bookchat.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import toy.bookchat.bookchat.domain.storage.StorageService;
import toy.bookchat.bookchat.domain.storage.image.ImageValidator;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.exception.UserAlreadySignUpException;
import toy.bookchat.bookchat.domain.user.exception.UserNotFoundException;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.domain.user.service.dto.UserSignUpRequestDto;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    StorageService storageService;
    @Mock
    ImageValidator imageValidator;
    @InjectMocks
    UserServiceImpl userService;

    @Test
    void 사용자_중복된_nickname_체크() throws Exception {

        when(userRepository.existsByNickname(anyString())).thenReturn(true);
        boolean result = userService.isDuplicatedName("test");
        assertThat(result).isTrue();
    }

    @Test
    void 사용자가_중복되지_않은_nickname_체크() throws Exception {
        when(userRepository.existsByNickname(anyString())).thenReturn(false);
        boolean result = userService.isDuplicatedName("test");
        assertThat(result).isFalse();
    }

    @Test
    void 처음_가입하는_회원의_경우_회원가입_성공() throws Exception {
        UserSignUpRequestDto userSignUpRequestDto = mock(UserSignUpRequestDto.class);
        User mockUser = mock(User.class);
        MultipartFile multipartFile = mock(MultipartFile.class);

        when(storageService.getFileUrl(any())).thenReturn("testBucketUrl");
        when(imageValidator.hasValidImage(any())).thenReturn(true);
        when(userSignUpRequestDto.getUser(any(), any(), any(), any())).thenReturn(mockUser);

        userService.registerNewUser(userSignUpRequestDto, multipartFile, "memberNumber",
            "test@gmail.com");

        verify(userRepository).save(any(User.class));
        verify(storageService).upload(any(), any());
    }

    @Test
    void 이미_가입된_사용자일경우_예외발생() throws Exception {
        UserSignUpRequestDto userSignUpRequestDto = mock(UserSignUpRequestDto.class);
        User mockUser = mock(User.class);
        MultipartFile multipartFile = mock(MultipartFile.class);

        when(userRepository.findByName(any())).thenReturn(Optional.of(mockUser));

        assertThatThrownBy(() -> {
            userService.registerNewUser(userSignUpRequestDto, multipartFile, "testMemberNumber",
                "test@gmail.com"
            );
        }).isInstanceOf(UserAlreadySignUpException.class);
    }

    @Test
    void 가입된_사용자인지_체크시_가입되지_않은_사용자라면_예외발생() throws Exception {

        when(userRepository.findByName(any())).thenReturn(Optional.ofNullable(null));

        assertThatThrownBy(() -> {
            userService.checkRegisteredUser("username");
        }).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void 사용자_회원탈퇴_요청시_삭제_성공() throws Exception {
        userService.deleteUser(any(User.class));

        verify(userRepository).delete(any());
    }
}