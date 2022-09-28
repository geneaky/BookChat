package toy.bookchat.bookchat.domain.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static toy.bookchat.bookchat.security.oauth.OAuth2Provider.KAKAO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import toy.bookchat.bookchat.config.aws.S3Config;
import toy.bookchat.bookchat.domain.storage.StorageService;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.exception.UserAlreadySignUpException;
import toy.bookchat.bookchat.domain.user.exception.UserNotFoundException;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.domain.user.service.dto.UserSignUpRequestDto;

import java.util.Optional;

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

        when(userRepository.existsByNickname(anyString())).thenReturn(true);
        boolean result = userService.isDuplicatedName("test");
        assertThat(result).isTrue();
    }

    @Test
    public void 사용자가_중복되지_않은_nickname_체크() throws Exception {
        when(userRepository.existsByNickname(anyString())).thenReturn(false);
        boolean result = userService.isDuplicatedName("test");
        assertThat(result).isFalse();
    }

    @Test
    public void 처음_가입하는_회원의_경우_회원가입_성공() throws Exception {
        UserSignUpRequestDto userSignUpRequestDto = mock(UserSignUpRequestDto.class);
        User mockUser = mock(User.class);
        when(storageService.getFileUrl(any())).thenReturn("testBucketUrl");
        when(userSignUpRequestDto.hasValidImage()).thenReturn(true);
        when(userSignUpRequestDto.getUser(any(), any(), any(), any())).thenReturn(mockUser);
        userService.registerNewUser(userSignUpRequestDto, "memberNumber","test@gmail.com", KAKAO);

        verify(userRepository).save(any(User.class));
        verify(storageService).upload(any(), any());
    }

    @Test
    public void 이미_가입된_사용자일경우_예외발생() throws Exception {
        UserSignUpRequestDto userSignUpRequestDto = mock(UserSignUpRequestDto.class);
        User mockUser = mock(User.class);
        when(userRepository.findByName(any())).thenReturn(Optional.of(mockUser));
        assertThatThrownBy(() -> {
            userService.registerNewUser(userSignUpRequestDto, "testMemberNumber","test@gmail.com", KAKAO);
        }).isInstanceOf(UserAlreadySignUpException.class);
    }

    @Test
    public void 사용자_회원가입시_지원하지_않는_이미지_확장자_예외발생() throws Exception {
        MultipartFile multipartFile = mock(MultipartFile.class);
        UserSignUpRequestDto userSignUpRequestDto = UserSignUpRequestDto.builder()
                .userProfileImage(multipartFile)
                .build();

        when(multipartFile.getOriginalFilename()).thenReturn("test.png");
        when(multipartFile.isEmpty()).thenReturn(false);

        assertThatThrownBy(() -> {
            userService.registerNewUser(userSignUpRequestDto, "testMemberNumber","test@gmail.com", KAKAO);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void 가입된_사용자인지_체크시_가입되지_않은_사용자라면_예외발생() throws Exception {

        when(userRepository.findByName(any())).thenReturn(Optional.ofNullable(null));

        assertThatThrownBy(() -> {
            userService.checkRegisteredUser("username");
        }).isInstanceOf(UserNotFoundException.class);
    }
}