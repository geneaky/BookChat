package toy.bookchat.bookchat.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import toy.bookchat.bookchat.db_module.device.DeviceEntity;
import toy.bookchat.bookchat.db_module.user.UserEntity;
import toy.bookchat.bookchat.db_module.user.repository.UserRepository;
import toy.bookchat.bookchat.domain.agony.service.AgonyService;
import toy.bookchat.bookchat.domain.bookshelf.service.BookShelfService;
import toy.bookchat.bookchat.domain.common.Status;
import toy.bookchat.bookchat.domain.device.service.DeviceService;
import toy.bookchat.bookchat.domain.storage.StorageService;
import toy.bookchat.bookchat.domain.storage.image.ImageValidator;
import toy.bookchat.bookchat.domain.user.UserProfile;
import toy.bookchat.bookchat.domain.user.api.v1.request.ChangeUserNicknameRequest;
import toy.bookchat.bookchat.domain.user.api.v1.request.UserSignInRequest;
import toy.bookchat.bookchat.domain.user.api.v1.request.UserSignUpRequest;
import toy.bookchat.bookchat.domain.user.api.v1.response.MemberProfileResponse;
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
    UserEntity mockUserEntity = mock(UserEntity.class);
    MultipartFile multipartFile = mock(MultipartFile.class);

    when(userSignUpRequest.getUser(any(), any(), any())).thenReturn(mockUserEntity);

    userService.registerNewUser(userSignUpRequest, multipartFile, "memberNumber",
        "test@gmail.com");

    verify(userRepository).save(any(UserEntity.class));
    verify(storageService).upload(any(), any(), any());
  }

  @Test
  void 이미_가입된_사용자일경우_예외발생() throws Exception {
    UserSignUpRequest userSignUpRequest = mock(UserSignUpRequest.class);
    UserEntity mockUserEntity = mock(UserEntity.class);

    when(userRepository.findByName(any())).thenReturn(Optional.of(mockUserEntity));

    assertThatThrownBy(() -> {
      userService.registerNewUser(userSignUpRequest, null,
          "testMemberNumber",
          "test@gmail.com"
      );
    }).isInstanceOf(UserAlreadySignUpException.class);
  }

  @Test
  void 사용자_회원탈퇴_요청시_삭제_성공() throws Exception {
    UserEntity userEntity = UserEntity.builder()
        .status(ACTIVE)
        .build();

    given(userReader.readUserEntity(eq(1L))).willReturn(userEntity);
    userService.deleteUser(1L);

    assertThat(userEntity.getStatus()).isEqualTo(Status.INACTIVE);
  }

  @Test
  void 사용자_닉네임_프로필사진_변경_성공() throws Exception {
    UserEntity userEntity = UserEntity.builder()
        .id(1L)
        .nickname("user1")
        .profileImageUrl("profile-image-url")
        .build();
    ChangeUserNicknameRequest changeUserNicknameRequest = new ChangeUserNicknameRequest(
        "user2");
    MultipartFile multipartFile = mock(MultipartFile.class);

    when(userReader.readUserEntity(userEntity.getId())).thenReturn(userEntity);
    when(storageService.upload(any(), any(), any())).thenReturn("test-s3-image-url");
    userService.updateUserProfile(changeUserNicknameRequest, multipartFile, userEntity.getId());

    assertThat(userEntity).extracting(UserEntity::getNickname, UserEntity::getProfileImageUrl)
        .containsExactly("user2", "test-s3-image-url");
  }

  @Test
  void 사용자_닉네임만_변경_성공() throws Exception {
    UserEntity userEntity = UserEntity.builder()
        .id(1L)
        .nickname("user1")
        .profileImageUrl("profile-image-url")
        .build();

    ChangeUserNicknameRequest changeUserNicknameRequest = new ChangeUserNicknameRequest(
        "user2");

    when(userReader.readUserEntity(userEntity.getId())).thenReturn(userEntity);
    userService.updateUserProfile(changeUserNicknameRequest, null, userEntity.getId());

    String nickname = userEntity.getNickname();
    assertThat(nickname).isEqualTo("user2");
    verify(storageService, noInteractions()).upload(any(), any(), any());
  }

  @Test
  void 등록된_디바이스가_없을경우_사용자_디바이스를_등록한다() throws Exception {
    UserSignInRequest userSignInRequest = UserSignInRequest.builder()
        .deviceToken("5o9")
        .fcmToken("w0teX6P")
        .build();

    UserEntity userEntity = UserEntity.builder().build();

    userService.checkDevice(userSignInRequest, userEntity);

    verify(deviceService).registerDevice(any());
  }

  @Test
  void 등록된_디바이스가_있는데_승인한경우_fcm발송후_디바이스정보_최신화() throws Exception {
    UserSignInRequest userSignInRequest = UserSignInRequest.builder()
        .deviceToken("5o9")
        .fcmToken("w0teX6P")
        .approveChangingDevice(true)
        .build();

    UserEntity userEntity = UserEntity.builder().build();

    DeviceEntity deviceEntity = DeviceEntity.builder()
        .deviceToken("18P1xu8")
        .fcmToken("nMhp5")
        .build();

    String oldDeviceFcmToken = deviceEntity.getFcmToken();

    when(deviceService.findUserDevice(eq(userEntity))).thenReturn(Optional.of(deviceEntity));
    userService.checkDevice(userSignInRequest, userEntity);

    assertThat(deviceEntity.getDeviceToken()).isEqualTo(userSignInRequest.getDeviceToken());
    assertThat(deviceEntity.getFcmToken()).isEqualTo(userSignInRequest.getFcmToken());
    verify(pushService).send(eq(oldDeviceFcmToken), any());
  }

  @Test
  void 등록된_디바이스가_현재_디바이스와_다른_디바이스이고_승인이나지_않은경우_예외발생() throws Exception {
    UserSignInRequest userSignInRequest = UserSignInRequest.builder()
        .deviceToken("5o9")
        .fcmToken("w0teX6P")
        .build();

    UserEntity userEntity = UserEntity.builder().build();

    DeviceEntity deviceEntity = DeviceEntity.builder()
        .deviceToken("N8vD8hy")
        .fcmToken("nMhp5")
        .build();

    when(deviceService.findUserDevice(userEntity)).thenReturn(Optional.of(deviceEntity));
    assertThatThrownBy(() -> {
      userService.checkDevice(userSignInRequest, userEntity);
    }).isInstanceOf(DeviceAlreadyRegisteredException.class);
  }

  @Test
  void 사용자_디바이스_삭제_성공() throws Exception {
    userService.deleteDevice(17L);

    verify(deviceService).deleteUserDevice(eq(17L));
  }

  @Test
  void 회원_프로필_정보_조회_성공() throws Exception {
    UserEntity userEntity = UserEntity.builder()
        .id(1L)
        .nickname("user1")
        .email("kKvTABYqa@test.com")
        .profileImageUrl("profile-image-url")
        .defaultProfileImageType(2)
        .build();

    given(userReader.readUserEntity(anyLong())).willReturn(userEntity);

    MemberProfileResponse expectedMemberProfileResponse = MemberProfileResponse.of(userEntity);
    MemberProfileResponse memberProfileResponse = userService.getMemberProfile(1L);

    assertThat(memberProfileResponse).isEqualTo(expectedMemberProfileResponse);
  }

  @Test
  void 사용자_프로필_조회_성공() throws Exception {
    UserEntity userEntity = UserEntity.builder()
        .id(1L)
        .nickname("user1")
        .email("kKvTABYqa@test.com")
        .profileImageUrl("profile-image-url")
        .defaultProfileImageType(2)
        .build();
    given(userReader.readUserEntity(anyLong())).willReturn(userEntity);

    UserProfile userProfile = userService.findUser(1L);

    verify(userReader).readUserEntity(anyLong());
  }
}