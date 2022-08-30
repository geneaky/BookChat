package toy.bookchat.bookchat.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.storage.StorageService;
import toy.bookchat.bookchat.domain.user.ROLE;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.domain.user.service.dto.UserSignUpRequestDto;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

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
            String fileName = UUID.randomUUID().toString();
            saveUser(oauth2MemberNumber, userSignUpRequestDto, fileName);
            storageService.upload(userSignUpRequestDto.getUserProfileImage());
            return;
        }

        saveUser(oauth2MemberNumber, userSignUpRequestDto, null);
        /* TODO: 2022-08-30
            반환받은 이미지 url과 함께 dto 사용자 데이터 사용해서 회원가입 진행
            #외부 api 호출중 예외가 발생한 경우 db는 롤백이되지만
            #외부 api로 이미지 저장후 db transaction에서 예외가 발생한 경우?
            # -> 이미지의 이름을 uuid로 생성하여 먼저 저장 후
            # -> 해당 uuid로 s3에 업로드
            # -> 이미지 접근 url은 어떻게? -> s3 버킷 병목
            #-> https://m.blog.naver.com/naebon/221756312403 hex hash 프리픽스 방식으로 저장
         */
    }

    private void saveUser(String oauth2MemberNumber, UserSignUpRequestDto userSignUpRequestDto, String fileName) {
        User user = new User(oauth2MemberNumber, userSignUpRequestDto.getUserEmail(), fileName, ROLE.USER, OAuth2Provider.from(userSignUpRequestDto.getOauth2Provider()), userSignUpRequestDto.getNickname(), userSignUpRequestDto.getReadingTastes());
        userRepository.save(user);
    }
}
