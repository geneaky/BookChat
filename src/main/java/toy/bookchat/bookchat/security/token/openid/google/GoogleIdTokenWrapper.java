package toy.bookchat.bookchat.security.token.openid.google;

import static toy.bookchat.bookchat.security.oauth.OAuth2Provider.GOOGLE;

import lombok.Getter;

@Getter
public class GoogleIdTokenWrapper {

    private final String memberNumber;
    private final String email;

    private GoogleIdTokenWrapper(String memberNumber, String email) {
        this.memberNumber = memberNumber;
        this.email = email;
    }

    public static GoogleIdTokenWrapper from(String subject, String email) {
        return new GoogleIdTokenWrapper(subject + GOOGLE.getValue(), email);
    }
}
