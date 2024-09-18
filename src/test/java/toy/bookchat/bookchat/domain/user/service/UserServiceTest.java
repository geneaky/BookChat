package toy.bookchat.bookchat.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.noInteractions;
import static toy.bookchat.bookchat.support.Status.ACTIVE;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import toy.bookchat.bookchat.db_module.device.DeviceEntity;
import toy.bookchat.bookchat.db_module.device.repository.DeviceRepository;
import toy.bookchat.bookchat.db_module.user.UserEntity;
import toy.bookchat.bookchat.db_module.user.repository.UserRepository;
import toy.bookchat.bookchat.support.Status;
import toy.bookchat.bookchat.infrastructure.s3.StorageService;
import toy.bookchat.bookchat.domain.user.UserProfile;
import toy.bookchat.bookchat.domain.user.api.v1.request.ChangeUserNicknameRequest;
import toy.bookchat.bookchat.domain.user.api.v1.request.UserSignInRequest;
import toy.bookchat.bookchat.domain.user.api.v1.request.UserSignUpRequest;
import toy.bookchat.bookchat.domain.user.api.v1.response.MemberProfileResponse;
import toy.bookchat.bookchat.exception.badrequest.user.UserAlreadySignUpException;
import toy.bookchat.bookchat.exception.conflict.device.DeviceAlreadyRegisteredException;
import toy.bookchat.bookchat.infrastructure.fcm.service.PushService;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  PushService pushService;
  @Mock
  StorageService storageService;
  @InjectMocks
  UserService userService;
  @Mock
  private UserRepository userRepository;
  @Mock
  private UserReader userReader;
  @Mock
  private DeviceRepository deviceRepository;

  @Test
  @DisplayName("사용자 nickname 중복 체크")
  void isDuplicatedName1() throws Exception {
    userService.isDuplicatedName("test");

    verify(userRepository).existsByNickname(any());
  }

  @Test
  @DisplayName("처음 가입하는 회원의 경우 회원가입 성공")
  void registerNewUser1() throws Exception {
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
  @DisplayName("이미 가입된 사용자일 경우 예외발생")
  void registerNewUser2() throws Exception {
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
  @DisplayName("사용자 회원탈퇴시 soft delete 성공")
  void deleteUser() throws Exception {
    UserEntity userEntity = UserEntity.builder()
        .status(ACTIVE)
        .build();

    given(userReader.readUserEntity(eq(1L))).willReturn(userEntity);
    userService.deleteUser(1L);

    assertThat(userEntity.getStatus()).isEqualTo(Status.INACTIVE);
  }

  @Test
  @DisplayName("사용자 닉네임, 프로필사진 변경 성공")
  void updateUserProfile1() throws Exception {
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
  @DisplayName("사용자 닉네임만 변경 성공")
  void updateUserProfile2() throws Exception {
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
  @DisplayName("등록된 기기가 없을 경우 사용자 기기를 등록한다")
  void checkDevice1() throws Exception {
    UserSignInRequest userSignInRequest = UserSignInRequest.builder()
        .deviceToken("5o9")
        .fcmToken("w0teX6P")
        .build();

    userService.checkDevice(userSignInRequest, 1L);

    verify(deviceRepository).save(any());
  }

  @Test
  @DisplayName("등록된 기기가 있는데 승인한 경우 fcm발송 후 디바이스 정보 최신화")
  void checkDevice2() throws Exception {
    UserSignInRequest userSignInRequest = UserSignInRequest.builder()
        .deviceToken("5o9")
        .fcmToken("w0teX6P")
        .approveChangingDevice(true)
        .build();

    DeviceEntity deviceEntity = DeviceEntity.builder()
        .deviceToken("18P1xu8")
        .fcmToken("nMhp5")
        .build();

    String oldDeviceFcmToken = deviceEntity.getFcmToken();

    given(deviceRepository.findByUserId(any())).willReturn(Optional.of(deviceEntity));
    userService.checkDevice(userSignInRequest, 1L);

    assertThat(deviceEntity.getDeviceToken()).isEqualTo(userSignInRequest.getDeviceToken());
    assertThat(deviceEntity.getFcmToken()).isEqualTo(userSignInRequest.getFcmToken());
    verify(pushService).send(eq(oldDeviceFcmToken), any());
  }

  @Test
  @DisplayName("등록된 기기가 현재 기기와 다른 기기이고 승인하지 않은 경우 예외발생")
  void checkDevice3() throws Exception {
    UserSignInRequest userSignInRequest = UserSignInRequest.builder()
        .deviceToken("5o9")
        .fcmToken("w0teX6P")
        .build();

    UserEntity userEntity = UserEntity.builder().build();

    DeviceEntity deviceEntity = DeviceEntity.builder()
        .deviceToken("N8vD8hy")
        .fcmToken("nMhp5")
        .build();

    given(deviceRepository.findByUserId(any())).willReturn(Optional.of(deviceEntity));

    assertThatThrownBy(() -> {
      userService.checkDevice(userSignInRequest, 1L);
    }).isInstanceOf(DeviceAlreadyRegisteredException.class);
  }

  @Test
  @DisplayName("사용자 디바이스 삭제 성공")
  void deleteDevice() throws Exception {
    userService.deleteDevice(17L);

    verify(deviceRepository).deleteByUserId(eq(17L));
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