package toy.bookchat.bookchat.domain.user.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
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
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
        MultipartFile userProfileImage, String userName, String userEmail) {
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
    public void updateUserProfile(ChangeUserNicknameRequest changeUserNicknameRequest,
        MultipartFile userProfileImage, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        if (imageExistent(userProfileImage)) {
            updateNicknameWithProfileImage(changeUserNicknameRequest, userProfileImage, user);
            return;
        }
        user.changeUserNickname(changeUserNicknameRequest.getNickname());
    }

    private boolean imageExistent(MultipartFile userProfileImage) {
        return userProfileImage != null;
    }

    private void updateNicknameWithProfileImage(ChangeUserNicknameRequest changeUserNicknameRequest,
        MultipartFile userProfileImage, User user) {
        String uploadFileUrl = storageService.upload(userProfileImage, UUID.randomUUID().toString(),
            LocalDateTime.now().format(dateTimeFormatter));
        user.changeUserNickname(changeUserNicknameRequest.getNickname());
        user.changeProfileImageUrl(uploadFileUrl);
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
