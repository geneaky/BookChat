package toy.bookchat.bookchat.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.storage.StorageService;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.exception.UserAlreadySignUpException;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.domain.user.service.dto.UserSignUpRequestDto;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final StorageService storageService;

    @Override
    @Transactional(readOnly = true)
    public boolean isDuplicatedName(String nickname) {
        return userRepository.existsByNickName(nickname);
    }

    @Override
    @Transactional
    public void registerNewUser(UserSignUpRequestDto userSignUpRequestDto, String oauth2MemberNumber) {


        if(userSignUpRequestDto.hasValidImage()) {
            String prefixedUUIDFileName = getPrefixedUUIDFileName();
            saveUser(oauth2MemberNumber, userSignUpRequestDto, prefixedUUIDFileName);
            storageService.upload(userSignUpRequestDto.getUserProfileImage(), prefixedUUIDFileName);
            return;
        }

        saveUser(oauth2MemberNumber, userSignUpRequestDto, null);
    }

    /**
     * '날짜 역순' + UUID로 저장 - S3가 prefix를 사용하여 partitioning을 하기 때문에
     */
    private String getPrefixedUUIDFileName() {
        StringBuilder stringBuilder = new StringBuilder();
        String UUIDFileName = UUID.randomUUID().toString();
        stringBuilder.append(new SimpleDateFormat("yyyy-MM-dd").format(new Date())).reverse();
        stringBuilder.append(UUIDFileName);
        stringBuilder.insert(0,"user_profile_image/");
        return stringBuilder.toString();
    }

    private void saveUser(String oauth2MemberNumber, UserSignUpRequestDto userSignUpRequestDto, String profileImageUrl) {
        Optional<User> optionalUser = userRepository.findByEmailAndProvider(userSignUpRequestDto.getUserEmail(), userSignUpRequestDto.getOAuth2Provider());
        optionalUser.ifPresentOrElse((u) -> {
            throw new UserAlreadySignUpException("user already sign up");
        },() -> {
            User user = userSignUpRequestDto.getUser(oauth2MemberNumber,profileImageUrl);
            userRepository.save(user);
        });
    }
}
