package toy.bookchat.bookchat.security.oauth;

import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import toy.bookchat.bookchat.domain.user.ROLE;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.security.user.UserPrincipal;

@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest)
        throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest,
        OAuth2User oAuth2User) {
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId,
            attributes);
        if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException(
                "Email not found from OAuth2 Provider");
        }

        Optional<User> optionalUser = userRepository.findByEmailAndProvider(
            oAuth2UserInfo.getEmail(),
            getProviderFromOAuth2UserRequest(oAuth2UserRequest));

        User user;

        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            if (!user.getProvider().equals(getProviderFromOAuth2UserRequest(oAuth2UserRequest))) {
                throw new OAuth2AuthenticationProcessingException(
                    "you can use this account to login");
            }
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private OAuth2Provider getProviderFromOAuth2UserRequest(OAuth2UserRequest oAuth2UserRequest) {
        return OAuth2Provider.valueOf(
            oAuth2UserRequest.getClientRegistration().getRegistrationId());
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest,
        OAuth2UserInfo oAuth2UserInfo) {
        User user = User.builder()
            .provider(getProviderFromOAuth2UserRequest(oAuth2UserRequest))
            .name(oAuth2UserInfo.getName())
            .email(oAuth2UserInfo.getEmail())
            .profileImageUrl(oAuth2UserInfo.getImageUrl())
            .role(ROLE.USER)
            .build();

        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.updateImageUrl(oAuth2UserInfo.getImageUrl());
        return userRepository.save(existingUser);
    }
}
