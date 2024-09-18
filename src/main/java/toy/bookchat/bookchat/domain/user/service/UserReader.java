package toy.bookchat.bookchat.domain.user.service;

import static toy.bookchat.bookchat.support.Status.ACTIVE;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.user.UserEntity;
import toy.bookchat.bookchat.db_module.user.repository.UserRepository;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.exception.notfound.user.UserNotFoundException;

@Component
public class UserReader {

  private final UserRepository userRepository;

  public UserReader(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public UserEntity readUserEntity(Long userId) {
    return userRepository.findByIdAndStatus(userId, ACTIVE).orElseThrow(UserNotFoundException::new);
  }

  public UserEntity readUserEntity(String oauth2MemberNumber) {
    return userRepository.findByNameAndStatus(oauth2MemberNumber, ACTIVE)
        .orElseThrow(UserNotFoundException::new);

  }

  public User readUser(Long userId) {
    UserEntity userEntity = userRepository.findByIdAndStatus(userId, ACTIVE).orElseThrow(UserNotFoundException::new);

    return User.builder()
        .id(userEntity.getId())
        .nickname(userEntity.getNickname())
        .profileImageUrl(userEntity.getProfileImageUrl())
        .defaultProfileImageType(userEntity.getDefaultProfileImageType())
        .build();
  }

  public List<User> readUsers(List<Long> userIds) {
    List<UserEntity> userEntities = userRepository.findByIdIn(userIds);

    return userEntities.stream()
        .map(userEntity -> User.builder()
            .id(userEntity.getId())
            .nickname(userEntity.getNickname())
            .profileImageUrl(userEntity.getProfileImageUrl())
            .defaultProfileImageType(userEntity.getDefaultProfileImageType())
            .build())
        .collect(Collectors.toList());
  }
}
