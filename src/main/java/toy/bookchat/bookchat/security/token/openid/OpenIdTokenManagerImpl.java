package toy.bookchat.bookchat.security.token.openid;

import static toy.bookchat.bookchat.domain.common.AuthConstants.BEGIN_INDEX;
import static toy.bookchat.bookchat.security.oauth.OAuth2Provider.GOOGLE;
import static toy.bookchat.bookchat.security.oauth.OAuth2Provider.KAKAO;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import java.util.Arrays;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.config.token.openid.OpenIdTokenConfig;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@Slf4j
@Component
public class OpenIdTokenManagerImpl implements OpenIdTokenManager {

    private final OpenIdTokenConfig openIdTokenConfig;
    public static final String CLIENT_ID1 = "1029693136513-nfk83nh8spdvrvbsabb4kq4u7c7uo2as.apps.googleusercontent.com";
    public static final String CLIENT_ID2 = "1029693136513-4vijorjkhgichaa52flkkmcrlkluojt5.apps.googleusercontent.com";

    private final GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
        new NetHttpTransport(), new GsonFactory())
        .setAudience(Arrays.asList(CLIENT_ID1, CLIENT_ID2))
        .build();


    public OpenIdTokenManagerImpl(OpenIdTokenConfig openIdTokenConfig) {
        this.openIdTokenConfig = openIdTokenConfig;
    }

    @Override
    public String getOAuth2MemberNumberFromIdToken(String token, OAuth2Provider oAuth2Provider) {
        if (oAuth2Provider == KAKAO) {
            return OpenIdToken.of(getIdToken(token)).getOAuth2MemberNumber(
                openIdTokenConfig.getPublicKey(OpenIdToken.of(getIdToken(token)).getKeyId(),
                    oAuth2Provider));
        }

        if (oAuth2Provider == GOOGLE) {
            return getGoogleMemberNumberFromGoogleIdToken(token);
        }
    }

    private String getGoogleMemberNumberFromGoogleIdToken(String token) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(googleIdTokenReaderTemplate(getIdToken(token),
            googleIdToken -> googleIdToken.getPayload().getSubject()));
        stringBuilder.append(GOOGLE.getValue());
        return stringBuilder.toString();
    }

    @Override
    public String getUserEmailFromToken(String token, OAuth2Provider oAuth2Provider) {
        if (oAuth2Provider == KAKAO) {
            OpenIdToken openIdToken = OpenIdToken.of(getIdToken(token));
            return openIdToken.getEmail(
                openIdTokenConfig.getPublicKey(openIdToken.getKeyId(), oAuth2Provider));
        }

        if (oAuth2Provider == GOOGLE) {
            return googleIdTokenReaderTemplate(getIdToken(token),
                googleIdToken -> googleIdToken.getPayload().getEmail());
        }
    }

    private String getIdToken(String bearerToken) {
        return bearerToken.substring(BEGIN_INDEX);
    }

    private String googleIdTokenReaderTemplate(String token,
        Function<GoogleIdToken, String> callback) {
        try {
            GoogleIdToken googleIdToken = verifier.verify(token);
            return callback.apply(googleIdToken);
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }
}
