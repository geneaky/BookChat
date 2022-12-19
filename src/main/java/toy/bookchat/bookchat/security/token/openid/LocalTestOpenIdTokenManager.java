package toy.bookchat.bookchat.security.token.openid;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@Primary
@Profile("local")
@Component
public class LocalTestOpenIdTokenManager implements OpenIdTokenManager {

    @Override
    public String getOAuth2MemberNumberFromToken(String token, OAuth2Provider oAuth2Provider) {
        return "google1234";
    }

    @Override
    public String getUserEmailFromToken(String token, OAuth2Provider oAuth2Provider) {
        return "kaktus418@gmail.com";
    }
}
