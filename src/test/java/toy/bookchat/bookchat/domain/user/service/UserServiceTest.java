package toy.bookchat.bookchat.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.noInteractions;
import static toy.bookchat.bookchat.domain.common.Status.ACTIVE;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import toy.bookchat.bookchat.domain.agony.service.AgonyService;
import toy.bookchat.bookchat.domain.bookshelf.service.BookShelfService;
import toy.bookchat.bookchat.domain.common.Status;
import toy.bookchat.bookchat.domain.device.Device;
import toy.bookchat.bookchat.domain.device.service.DeviceService;
import toy.bookchat.bookchat.domain.storage.StorageService;
import toy.bookchat.bookchat.domain.storage.image.ImageValidator;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.domain.user.service.dto.request.ChangeUserNicknameRequest;
import toy.bookchat.bookchat.domain.user.service.dto.request.UserSignInRequest;
import toy.bookchat.bookchat.domain.user.service.dto.request.UserSignUpRequest;
import toy.bookchat.bookchat.exception.badrequest.user.UserAlreadySignUpException;
import toy.bookchat.bookchat.exception.conflict.device.DeviceAlreadyRegisteredException;
import toy.bookchat.bookchat.infrastructure.push.service.PushService;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    UserReader userReader;
    @Mock
    BookShelfService bookShelfService;
    @Mock
    AgonyService agonyService;
    @Mock
    DeviceService deviceService;
    @Mock
    PushService pushService;
    @Mock
    StorageService storageService;
    @Mock
    ImageValidator imageValidator;
    @InjectMocks
    UserService userService;

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
        UserSignUpRequest userSignUpRequest = mock(UserSignUpRequest.class);
        User mockUser = mock(User.class);
        MultipartFile multipartFile = mock(MultipartFile.class);

        when(userSignUpRequest.getUser(any(), any(), any())).thenReturn(mockUser);

        userService.registerNewUser(userSignUpRequest, multipartFile, "memberNumber",
            "test@gmail.com");

        verify(userRepository).save(any(User.class));
        verify(storageService).upload(any(), any(), any());
    }

    @Test
    void 이미_가입된_사용자일경우_예외발생() throws Exception {
        UserSignUpRequest userSignUpRequest = mock(UserSignUpRequest.class);
        User mockUser = mock(User.class);

        when(userRepository.findByName(any())).thenReturn(Optional.of(mockUser));

        assertThatThrownBy(() -> {
            userService.registerNewUser(userSignUpRequest, null,
                "testMemberNumber",
                "test@gmail.com"
            );
        }).isInstanceOf(UserAlreadySignUpException.class);
    }

    @Test
    void 사용자_회원탈퇴_요청시_삭제_성공() throws Exception {
        User user = User.builder()
            .status(ACTIVE)
            .build();

        given(userReader.readUser(eq(1L))).willReturn(user);
        userService.deleteUser(1L);

        assertThat(user.getStatus()).isEqualTo(Status.INACTIVE);
    }

    @Test
    void 사용자_닉네임_프로필사진_변경_성공() throws Exception {
        User user = User.builder()
            .id(1L)
            .nickname("user1")
            .profileImageUrl("profile-image-url")
            .build();
        ChangeUserNicknameRequest changeUserNicknameRequest = new ChangeUserNicknameRequest(
            "user2");
        MultipartFile multipartFile = mock(MultipartFile.class);

        when(userReader.readUser(user.getId())).thenReturn(user);
        when(storageService.upload(any(), any(), any())).thenReturn("test-s3-image-url");
        userService.updateUserProfile(changeUserNicknameRequest, multipartFile, user.getId());

        assertThat(user).extracting(User::getNickname, User::getProfileImageUrl)
            .containsExactly("user2", "test-s3-image-url");
    }

    @Test
    void 사용자_닉네임만_변경_성공() throws Exception {
        User user = User.builder()
            .id(1L)
            .nickname("user1")
            .profileImageUrl("profile-image-url")
            .build();

        ChangeUserNicknameRequest changeUserNicknameRequest = new ChangeUserNicknameRequest(
            "user2");

        when(userReader.readUser(user.getId())).thenReturn(user);
        userService.updateUserProfile(changeUserNicknameRequest, null, user.getId());

        String nickname = user.getNickname();
        assertThat(nickname).isEqualTo("user2");
        verify(storageService, noInteractions()).upload(any(), any(), any());
    }

    @Test
    void 등록된_디바이스가_없을경우_사용자_디바이스를_등록한다() throws Exception {
        UserSignInRequest userSignInRequest = UserSignInRequest.builder()
            .deviceToken("5o9")
            .fcmToken("w0teX6P")
            .build();

        User user = User.builder().build();

        userService.checkDevice(userSignInRequest, user);

        verify(deviceService).registerDevice(any());
    }

    @Test
    void 등록된_디바이스가_있는데_승인한경우_fcm발송후_디바이스정보_최신화() throws Exception {
        UserSignInRequest userSignInRequest = UserSignInRequest.builder()
            .deviceToken("5o9")
            .fcmToken("w0teX6P")
            .approveChangingDevice(true)
            .build();

        User user = User.builder().build();

        Device device = Device.builder()
            .deviceToken("18P1xu8")
            .fcmToken("nMhp5")
            .build();

        String oldDeviceFcmToken = device.getFcmToken();

        when(deviceService.findUserDevice(eq(user))).thenReturn(Optional.of(device));
        userService.checkDevice(userSignInRequest, user);

        assertThat(device.getDeviceToken()).isEqualTo(userSignInRequest.getDeviceToken());
        assertThat(device.getFcmToken()).isEqualTo(userSignInRequest.getFcmToken());
        verify(pushService).send(eq(oldDeviceFcmToken), any());
    }

    @Test
    void 등록된_디바이스가_현재_디바이스와_다른_디바이스이고_승인이나지_않은경우_예외발생() throws Exception {
        UserSignInRequest userSignInRequest = UserSignInRequest.builder()
            .deviceToken("5o9")
            .fcmToken("w0teX6P")
            .build();

        User user = User.builder().build();

        Device device = Device.builder()
            .deviceToken("N8vD8hy")
            .fcmToken("nMhp5")
            .build();

        when(deviceService.findUserDevice(user)).thenReturn(Optional.of(device));
        assertThatThrownBy(() -> {
            userService.checkDevice(userSignInRequest, user);
        }).isInstanceOf(DeviceAlreadyRegisteredException.class);
    }

    @Test
    void 사용자_디바이스_삭제_성공() throws Exception {
        userService.deleteDevice(17L);

        verify(deviceService).deleteUserDevice(eq(17L));
    }
}