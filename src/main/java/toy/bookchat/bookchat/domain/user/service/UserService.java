package toy.bookchat.bookchat.domain.user.service;

import static toy.bookchat.bookchat.infrastructure.push.PushType.LOGIN;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import toy.bookchat.bookchat.db_module.device.DeviceEntity;
import toy.bookchat.bookchat.db_module.user.UserEntity;
import toy.bookchat.bookchat.db_module.user.repository.UserRepository;
import toy.bookchat.bookchat.domain.device.service.DeviceService;
import toy.bookchat.bookchat.domain.storage.StorageService;
import toy.bookchat.bookchat.domain.user.UserProfile;
import toy.bookchat.bookchat.domain.user.api.v1.request.ChangeUserNicknameRequest;
import toy.bookchat.bookchat.domain.user.api.v1.request.UserSignInRequest;
import toy.bookchat.bookchat.domain.user.api.v1.request.UserSignUpRequest;
import toy.bookchat.bookchat.domain.user.api.v1.response.MemberProfileResponse;
import toy.bookchat.bookchat.exception.badrequest.user.UserAlreadySignUpException;
import toy.bookchat.bookchat.exception.conflict.device.DeviceAlreadyRegisteredException;
import toy.bookchat.bookchat.infrastructure.push.PushMessageBody;
import toy.bookchat.bookchat.infrastructure.push.service.PushService;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final UserReader userReader;
  private final DeviceService deviceService;
  private final PushService pushService;
  private final StorageService storageService;
  private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public UserService(UserRepository userRepository, UserReader userReader, DeviceService deviceService,
      PushService pushService,
      @Qualifier("userProfileStorageService") StorageService storageService) {
    this.userRepository = userRepository;
    this.userReader = userReader;
    this.deviceService = deviceService;
    this.pushService = pushService;
    this.storageService = storageService;
  }

  @Transactional(readOnly = true)
  public boolean isDuplicatedName(String nickname) {
    return userRepository.existsByNickname(nickname);
  }

  @Transactional
  public void registerNewUser(UserSignUpRequest userSignUpRequest, MultipartFile userProfileImage, String userName,
      String userEmail) {
    if (imageExistent(userProfileImage)) {
      uploadWithImage(userSignUpRequest, userProfileImage, userName, userEmail);
      return;
    }
    uploadWithoutImage(userSignUpRequest, userName, userEmail);
  }

  private void uploadWithImage(UserSignUpRequest userSignUpRequest,
      MultipartFile userProfileImage, String userName, String userEmail) {
    String uploadFileUrl = storageService.upload(userProfileImage, UUID.randomUUID().toString(),
        LocalDateTime.now().format(dateTimeFormatter));
    saveUser(userSignUpRequest, userName, userEmail, uploadFileUrl);
  }

  private void uploadWithoutImage(UserSignUpRequest userSignUpRequest, String userName,
      String userEmail) {
    saveUser(userSignUpRequest, userName, userEmail, null);
  }

  @Transactional(readOnly = true)
  public UserEntity findUserByUsername(String oauth2MemberNumber) {
    return userReader.readUserEntity(oauth2MemberNumber);
  }

  @Transactional
  public void deleteUser(Long userId) {
    UserEntity userEntity = userReader.readUserEntity(userId);
    userEntity.inactive();
  }

  @Transactional
  public void updateUserProfile(ChangeUserNicknameRequest changeUserNicknameRequest,
      MultipartFile userProfileImage, Long userId) {
    UserEntity userEntity = userReader.readUserEntity(userId);
    if (imageExistent(userProfileImage)) {
      updateNicknameWithProfileImage(changeUserNicknameRequest, userProfileImage, userEntity);
      return;
    }
    userEntity.changeUserNickname(changeUserNicknameRequest.getNickname());
  }

  private boolean imageExistent(MultipartFile userProfileImage) {
    return userProfileImage != null;
  }

  private void updateNicknameWithProfileImage(ChangeUserNicknameRequest changeUserNicknameRequest,
      MultipartFile userProfileImage, UserEntity userEntity) {
    String uploadFileUrl = storageService.upload(userProfileImage, UUID.randomUUID().toString(),
        LocalDateTime.now().format(dateTimeFormatter));
    userEntity.changeUserNickname(changeUserNicknameRequest.getNickname());
    userEntity.changeProfileImageUrl(uploadFileUrl);
  }

  private void saveUser(UserSignUpRequest userSignUpRequest, String userName,
      String email, String profileImageUrl) {
    userRepository.findByName(userName).ifPresentOrElse(user -> {
      throw new UserAlreadySignUpException();
    }, () -> {
      UserEntity userEntity = userSignUpRequest.getUser(userName, email, profileImageUrl);
      userRepository.save(userEntity);
    });
  }

  @Transactional
  public void checkDevice(UserSignInRequest userSignInRequest, UserEntity userEntity) {
    deviceService.findUserDevice(userEntity)
        .ifPresentOrElse(changeDeviceIfApproved(userSignInRequest),
            registerUserDevice(userSignInRequest, userEntity));
  }

  private Runnable registerUserDevice(UserSignInRequest userSignInRequest, UserEntity userEntity) {
    return () -> deviceService.registerDevice(userSignInRequest.createDevice(userEntity));
  }

  private Consumer<DeviceEntity> changeDeviceIfApproved(UserSignInRequest userSignInRequest) {
    return device -> {
      if (userSignInRequest.approved()) {
        String oldDeviceFcmToken = device.getFcmToken();
        device.changeDeviceToken(userSignInRequest.getDeviceToken());
        device.changeFcmToken(userSignInRequest.getFcmToken());
        pushService.send(oldDeviceFcmToken,
            PushMessageBody.of(LOGIN, "다른 곳에서 로그인 했습니다."));
        return;
      }
      throw new DeviceAlreadyRegisteredException();
    };
  }

  @Transactional
  public void deleteDevice(Long userId) {
    deviceService.deleteUserDevice(userId);
  }

  @Transactional(readOnly = true)
  public MemberProfileResponse getMemberProfile(Long memberId) {
    UserEntity userEntity = userReader.readUserEntity(memberId);
    return MemberProfileResponse.of(userEntity);
  }

  @Transactional(readOnly = true)
  public UserProfile findUser(Long userId) {
    UserEntity userEntity = userReader.readUserEntity(userId);
    return UserProfile.from(userEntity);
  }
}
