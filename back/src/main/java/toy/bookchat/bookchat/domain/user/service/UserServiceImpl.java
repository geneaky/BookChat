package toy.bookchat.bookchat.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.config.aws.S3Config;
import toy.bookchat.bookchat.domain.storage.StorageService;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.exception.UserAlreadySignUpException;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.domain.user.service.dto.UserSignUpRequestDto;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final S3Config s3Config;
    private final UserRepository userRepository;
    private final StorageService storageService;

    @Override
    @Transactional(readOnly = true)
    public boolean isDuplicatedName(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    @Override
    @Transactional
    public void registerNewUser(UserSignUpRequestDto userSignUpRequestDto, String oauth2MemberNumber, String userEmail, OAuth2Provider providerType) {


        if(userSignUpRequestDto.hasValidImage()) {
            String prefixedUUIDFileName = getPrefixedUUIDFileName(userSignUpRequestDto.getFileExtension());
            String prefixedUUIDFileUrl = getPrefixedUUIDFileUrl(prefixedUUIDFileName);
            saveUser(userSignUpRequestDto, oauth2MemberNumber, userEmail, prefixedUUIDFileUrl, providerType);
            storageService.upload(userSignUpRequestDto.getUserProfileImage(), prefixedUUIDFileName);
            return;
        }

        saveUser(userSignUpRequestDto, oauth2MemberNumber, userEmail, null, providerType);
    }

    private String getPrefixedUUIDFileUrl(String prefixedUUIDFileName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(s3Config.getImageBucketUrl());
        stringBuilder.append(prefixedUUIDFileName);
        return stringBuilder.toString();
    }

    /**
     * '날짜 역순' + UUID로 저장 - S3가 prefix를 사용하여 partitioning을 하기 때문에
     */
    private String getPrefixedUUIDFileName(String fileExtension) {
        StringBuilder stringBuilder = new StringBuilder();
        String UUIDFileName = UUID.randomUUID().toString();
        stringBuilder.append(new SimpleDateFormat("yyyy-MM-dd").format(new Date())).reverse();
        stringBuilder.append(UUIDFileName);
        stringBuilder.append(".");
        stringBuilder.append(fileExtension);
        stringBuilder.insert(0,"user_profile_image/");
        return stringBuilder.toString();
    }

    private void saveUser(UserSignUpRequestDto userSignUpRequestDto, String oauth2MemberNumber, String email, String profileImageUrl, OAuth2Provider providerType) {
        Optional<User> optionalUser = userRepository.findByName(oauth2MemberNumber);
        optionalUser.ifPresentOrElse((u) -> {
            throw new UserAlreadySignUpException("user already sign up");
        },() -> {
            User user = userSignUpRequestDto.getUser(oauth2MemberNumber, email, profileImageUrl, providerType);
            userRepository.save(user);
        });
    }
}
