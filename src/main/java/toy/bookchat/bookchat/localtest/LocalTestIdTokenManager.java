package toy.bookchat.bookchat.localtest;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.token.openid.IdTokenManager;

@Primary
@Profile("local")
@Component
public class LocalTestIdTokenManager implements IdTokenManager {

    @Override
    public String getOAuth2MemberNumberFromIdToken(String token, OAuth2Provider oAuth2Provider) {
        return "google123";
    }

    @Override
    public String getUserEmailFromToken(String token, OAuth2Provider oAuth2Provider) {
        return "kaktus418@gmail.com";
    }
}
