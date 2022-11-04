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
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.domain.user.service.dto.request.ChangeUserNicknameRequest;
import toy.bookchat.bookchat.domain.user.service.dto.request.UserSignUpRequest;
import toy.bookchat.bookchat.exception.user.UserAlreadySignUpException;
import toy.bookchat.bookchat.exception.user.UserNotFoundException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final StorageService storageService;
    private final ImageValidator imageValidator;

    public UserService(UserRepository userRepository, StorageService storageService,
        ImageValidator imageValidator) {
        this.userRepository = userRepository;
        this.storageService = storageService;
        this.imageValidator = imageValidator;
    }

    @Transactional(readOnly = true)
    public boolean isDuplicatedName(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    @Transactional
    public void registerNewUser(UserSignUpRequest userSignUpRequest,
        MultipartFile userProfileImage, String userName, String userEmail) {
        if (imageValidator.hasValidImage(userProfileImage)) {
            String prefixedUUIDFileName = createFileName(userProfileImage);
            String prefixedUUIDFileUrl = createFileUrl(prefixedUUIDFileName);

            saveUser(userSignUpRequest, userName, userEmail, prefixedUUIDFileUrl);

            storageService.upload(userProfileImage, prefixedUUIDFileName);
            return;
        }

        saveUser(userSignUpRequest, userName, userEmail, null);
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

    @Transactional(readOnly = true)
    public User findUserByUsername(String oauth2MemberNumber) {
        return userRepository.findByName(oauth2MemberNumber)
            .orElseThrow(() -> {
                throw new UserNotFoundException("Not Registered User");
            });
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Transactional
    public void changeUserNickname(ChangeUserNicknameRequest changeUserNicknameRequest,
        Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new UserNotFoundException("Can't find User");
        });
        user.changeUserNickname(changeUserNicknameRequest.getNickname());
    }

    private void saveUser(UserSignUpRequest userSignUpRequest, String userName,
        String email, String profileImageUrl) {
        Optional<User> optionalUser = userRepository.findByName(userName);
        optionalUser.ifPresentOrElse(u -> {
            throw new UserAlreadySignUpException("user already sign up");
        }, () -> {
            User user = userSignUpRequest.getUser(userName, email, profileImageUrl);
            userRepository.save(user);
        });
    }
}
