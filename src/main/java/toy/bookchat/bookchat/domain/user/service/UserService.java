package toy.bookchat.bookchat.domain.user.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import toy.bookchat.bookchat.domain.agony.service.AgonyService;
import toy.bookchat.bookchat.domain.bookshelf.service.BookShelfService;
import toy.bookchat.bookchat.domain.storage.StorageService;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.domain.user.service.dto.request.ChangeUserNicknameRequest;
import toy.bookchat.bookchat.domain.user.service.dto.request.UserSignUpRequest;
import toy.bookchat.bookchat.exception.user.UserAlreadySignUpException;
import toy.bookchat.bookchat.exception.user.UserNotFoundException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BookShelfService bookShelfService;
    private final AgonyService agonyService;
    private final StorageService storageService;

    public UserService(UserRepository userRepository,
        BookShelfService bookShelfService,
        AgonyService agonyService,
        @Qualifier("userProfileStorageService") StorageService storageService) {
        this.userRepository = userRepository;
        this.bookShelfService = bookShelfService;
        this.agonyService = agonyService;
        this.storageService = storageService;
    }

    @Transactional(readOnly = true)
    public boolean isDuplicatedName(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    @Transactional
    public void registerNewUser(UserSignUpRequest userSignUpRequest,
        Optional<MultipartFile> userProfileImage, String userName, String userEmail) {
        userProfileImage.ifPresentOrElse(uploadWithImage(userSignUpRequest, userName, userEmail),
            uploadWithoutImage(userSignUpRequest, userName, userEmail));
    }

    private Runnable uploadWithoutImage(UserSignUpRequest userSignUpRequest, String userName,
        String userEmail) {
        return () -> saveUser(userSignUpRequest, userName, userEmail, null);
    }

    private Consumer<MultipartFile> uploadWithImage(UserSignUpRequest userSignUpRequest,
        String userName, String userEmail) {
        return image -> {
            String prefixedUUIDFileName = storageService.createFileName(
                image, UUID.randomUUID().toString(),
                new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            String prefixedUUIDFileUrl = storageService.getFileUrl(prefixedUUIDFileName);
            saveUser(userSignUpRequest, userName, userEmail, prefixedUUIDFileUrl);
            storageService.upload(image, prefixedUUIDFileName);
        };
    }

    @Transactional(readOnly = true)
    public User findUserByUsername(String oauth2MemberNumber) {
        return userRepository.findByName(oauth2MemberNumber)
            .orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public void deleteUser(Long userId) {
        agonyService.deleteAllUserAgony(userId);
        bookShelfService.deleteAllUserBookShelves(userId);
        userRepository.deleteById(userId);
    }

    @Transactional
    public void updateUserProfile(
        ChangeUserNicknameRequest changeUserNicknameRequest,
        Optional<MultipartFile> userProfileImage, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        userProfileImage.ifPresentOrElse(updateImage(user, changeUserNicknameRequest.getNickname()),
            () -> user.changeUserNickname(changeUserNicknameRequest.getNickname()));
    }

    private Consumer<MultipartFile> updateImage(User user, String nickname) {
        return image -> {
            String prefixedUUIDFileName = storageService.createFileName(
                image, UUID.randomUUID().toString(),
                new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            String prefixedUUIDFileUrl = storageService.getFileUrl(prefixedUUIDFileName);
            user.changeUserNickname(nickname);
            user.changeProfileImageUrl(prefixedUUIDFileUrl);
            storageService.upload(image, prefixedUUIDFileName);
        };
    }

    private void saveUser(UserSignUpRequest userSignUpRequest, String userName,
        String email, String profileImageUrl) {
        userRepository.findByName(userName).ifPresentOrElse(user -> {
            throw new UserAlreadySignUpException();
        }, () -> {
            User user = userSignUpRequest.getUser(userName, email, profileImageUrl);
            userRepository.save(user);
        });
    }
}
