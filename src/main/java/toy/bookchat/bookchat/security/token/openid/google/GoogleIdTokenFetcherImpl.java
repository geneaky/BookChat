package toy.bookchat.bookchat.security.token.openid.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.config.token.OAuth2Properties;
import toy.bookchat.bookchat.exception.unauthorized.NotVerifiedIdTokenException;

@Component
public class GoogleIdTokenFetcherImpl implements GoogleIdTokenFetcher {

    private final OAuth2Properties oAuth2Properties;
    private final GoogleIdTokenVerifier verifier;

    public GoogleIdTokenFetcherImpl(OAuth2Properties oAuth2Properties) {
        this.oAuth2Properties = oAuth2Properties;
        this.verifier = new GoogleIdTokenVerifier.Builder(
            new NetHttpTransport(), new GsonFactory())
            .setAudience(this.oAuth2Properties.getGoogleClientIds())
            .build();
    }

    @Override
    public GoogleIdTokenWrapper fetchGoogleIdToken(String token) {
        try {
            GoogleIdToken googleIdToken = verifier.verify(token);
            return GoogleIdTokenWrapper.from(googleIdToken.getPayload().getSubject(),
                googleIdToken.getPayload().getEmail());
        } catch (Exception e) {
            throw new NotVerifiedIdTokenException();
        }
    }
}
