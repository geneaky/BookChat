package toy.bookchat.bookchat.domain.user.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import toy.bookchat.bookchat.domain.storage.StorageService;
import toy.bookchat.bookchat.domain.storage.image.ImageValidator;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.exception.UserAlreadySignUpException;
import toy.bookchat.bookchat.domain.user.exception.UserNotFoundException;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.domain.user.service.dto.UserSignUpRequestDto;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final StorageService storageService;
    private final ImageValidator imageValidator;

    public UserServiceImpl(UserRepository userRepository, StorageService storageService,
        ImageValidator imageValidator) {
        this.userRepository = userRepository;
        this.storageService = storageService;
        this.imageValidator = imageValidator;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isDuplicatedName(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    @Override
    @Transactional
    public void registerNewUser(UserSignUpRequestDto userSignUpRequestDto,
        MultipartFile userProfileImage, String userName, String userEmail) {
        if (imageValidator.hasValidImage(userProfileImage)) {
            String prefixedUUIDFileName = createFileName(userProfileImage);
            String prefixedUUIDFileUrl = createFileUrl(prefixedUUIDFileName);

            saveUser(userSignUpRequestDto, userName, userEmail, prefixedUUIDFileUrl,
                userSignUpRequestDto.getOauth2Provider());

            storageService.upload(userProfileImage, prefixedUUIDFileName);
            return;
        }

        saveUser(userSignUpRequestDto, userName, userEmail, null,
            userSignUpRequestDto.getOauth2Provider());
    }

    private String createFileUrl(String prefixedUUIDFileName) {
        return storageService.getFileUrl(prefixedUUIDFileName);
    }

    private String createFileName(MultipartFile userProfileImage) {
        return storageService.createFileName(
            imageValidator.getFileExtension(userProfileImage),
            UUID.randomUUID().toString(),
            new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    }

    @Override
    public void checkRegisteredUser(String oauth2MemberNumber) {
        userRepository.findByName(oauth2MemberNumber)
            .orElseThrow(() -> {
                throw new UserNotFoundException("Not Registered User");
            });
    }

    @Override
    @Transactional
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    private void saveUser(UserSignUpRequestDto userSignUpRequestDto, String userName,
        String email, String profileImageUrl, OAuth2Provider providerType) {
        Optional<User> optionalUser = userRepository.findByName(userName);
        optionalUser.ifPresentOrElse(u -> {
            throw new UserAlreadySignUpException("user already sign up");
        }, () -> {
            User user = userSignUpRequestDto.getUser(userName, email, profileImageUrl,
                providerType);
            userRepository.save(user);
        });
    }
}
