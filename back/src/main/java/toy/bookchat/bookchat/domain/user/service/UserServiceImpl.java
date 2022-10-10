package toy.bookchat.bookchat.domain.user.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        String oauth2MemberNumber, String userEmail, OAuth2Provider providerType) {
        if (imageValidator.hasValidImage(userSignUpRequestDto.getUserProfileImage())) {
            String prefixedUUIDFileName = storageService.createFileName(
                imageValidator.getFileExtension(
                    userSignUpRequestDto.getUserProfileImage()));
            String prefixedUUIDFileUrl = storageService.getFileUrl(prefixedUUIDFileName);
            saveUser(userSignUpRequestDto, oauth2MemberNumber, userEmail, prefixedUUIDFileUrl,
                providerType);
            storageService.upload(userSignUpRequestDto.getUserProfileImage(), prefixedUUIDFileName);
            return;
        }

        saveUser(userSignUpRequestDto, oauth2MemberNumber, userEmail, null, providerType);
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

    private void saveUser(UserSignUpRequestDto userSignUpRequestDto, String oauth2MemberNumber,
        String email, String profileImageUrl, OAuth2Provider providerType) {
        Optional<User> optionalUser = userRepository.findByName(oauth2MemberNumber);
        optionalUser.ifPresentOrElse(u -> {
            throw new UserAlreadySignUpException("user already sign up");
        }, () -> {
            User user = userSignUpRequestDto.getUser(oauth2MemberNumber, email, profileImageUrl,
                providerType);
            userRepository.save(user);
        });
    }
}
