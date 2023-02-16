package toy.bookchat.bookchat.security.token.openid;

import static toy.bookchat.bookchat.domain.common.AuthConstants.BEGIN_INDEX;
import static toy.bookchat.bookchat.security.oauth.OAuth2Provider.GOOGLE;
import static toy.bookchat.bookchat.security.oauth.OAuth2Provider.KAKAO;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.config.token.OAuth2Properties;
import toy.bookchat.bookchat.exception.security.NotSupportedOAuth2ProviderException;
import toy.bookchat.bookchat.exception.security.NotVerifiedIdTokenException;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.token.openid.keys.KakaoPublicKeyFetcher;

@Slf4j
@Component
public class IdTokenManagerImpl implements IdTokenManager {

    private final KakaoPublicKeyFetcher kakaoPublickeyFetcher;
    private final OAuth2Properties oAuth2Properties;
    private final GoogleIdTokenVerifier verifier;


    public IdTokenManagerImpl(KakaoPublicKeyFetcher kakaoPublickeyFetcher,
        OAuth2Properties oAuth2Properties) {
        this.kakaoPublickeyFetcher = kakaoPublickeyFetcher;
        this.oAuth2Properties = oAuth2Properties;
        this.verifier = new GoogleIdTokenVerifier.Builder(
            new NetHttpTransport(), new GsonFactory())
            .setAudience(oAuth2Properties.getGoogleClientIds())
            .build();
    }

    @Override
    public String getOAuth2MemberNumberFromIdToken(String token, OAuth2Provider oAuth2Provider) {
        String idToken = getIdToken(token);
        if (oAuth2Provider == KAKAO) {
            return KakaoIdToken.of(idToken).getOAuth2MemberNumber(
                kakaoPublickeyFetcher.getPublicKey(idToken,
                    oAuth2Properties.getKakaoUri()), oAuth2Properties.getKakaoAppKey());
        }

        if (oAuth2Provider == GOOGLE) {
            return googleIdTokenReaderTemplate(idToken,
                googleIdToken -> googleIdToken.getPayload().getSubject()) + GOOGLE.getValue();
        }

        throw new NotSupportedOAuth2ProviderException();
    }

    @Override
    public String getUserEmailFromToken(String token, OAuth2Provider oAuth2Provider) {
        String idToken = getIdToken(token);
        if (oAuth2Provider == KAKAO) {
            return KakaoIdToken.of(idToken)
                .getEmail(
                    kakaoPublickeyFetcher.getPublicKey(idToken, oAuth2Properties.getKakaoUri()),
                    oAuth2Properties.getKakaoAppKey());
        }

        if (oAuth2Provider == GOOGLE) {
            return googleIdTokenReaderTemplate(idToken,
                googleIdToken -> googleIdToken.getPayload().getEmail());
        }

        throw new NotSupportedOAuth2ProviderException();
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
            throw new NotVerifiedIdTokenException();
        }
    }
}
