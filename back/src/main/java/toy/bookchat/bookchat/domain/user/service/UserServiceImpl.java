package toy.bookchat.bookchat.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.storage.StorageService;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.exception.UserAlreadySignUpException;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.domain.user.service.dto.UserSignUpRequestDto;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
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
            String prefixedUUIDFileName = storageService.createFileName(userSignUpRequestDto.getFileExtension());
            String prefixedUUIDFileUrl = storageService.getFileUrl(prefixedUUIDFileName);
            saveUser(userSignUpRequestDto, oauth2MemberNumber, userEmail, prefixedUUIDFileUrl, providerType);
            storageService.upload(userSignUpRequestDto.getUserProfileImage(), prefixedUUIDFileName);
            return;
        }

        saveUser(userSignUpRequestDto, oauth2MemberNumber, userEmail, null, providerType);
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
