package toy.bookchat.bookchat.security.token.openid;

import static toy.bookchat.bookchat.security.oauth.OAuth2Provider.GOOGLE;
import static toy.bookchat.bookchat.security.oauth.OAuth2Provider.KAKAO;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.exception.unauthorized.NotSupportedOAuth2ProviderException;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.token.openid.google.GoogleIdTokenFetcher;
import toy.bookchat.bookchat.security.token.openid.kakao.KakaoIdTokenFetcher;

@Slf4j
@Component
public class IdTokenManagerImpl implements IdTokenManager {

    private final int BEGIN_INDEX = 7;

    private final KakaoIdTokenFetcher kakaoIdTokenFetcher;
    private final GoogleIdTokenFetcher googleIdTokenFetcher;

    public IdTokenManagerImpl(KakaoIdTokenFetcher kakaoIdTokenFetcher,
        GoogleIdTokenFetcher googleIdTokenFetcher) {
        this.kakaoIdTokenFetcher = kakaoIdTokenFetcher;
        this.googleIdTokenFetcher = googleIdTokenFetcher;
    }

    @Override
    public String getOAuth2MemberNumberFromIdToken(String token, OAuth2Provider oAuth2Provider) {
        String idToken = getIdToken(token);
        if (oAuth2Provider == KAKAO) {
            return kakaoIdTokenFetcher.fetchKakaoIdToken(idToken).getMemberNumber();
        }

        if (oAuth2Provider == GOOGLE) {
            return googleIdTokenFetcher.fetchGoogleIdToken(idToken).getMemberNumber();
        }

        throw new NotSupportedOAuth2ProviderException();
    }

    @Override
    public String getUserEmailFromToken(String token, OAuth2Provider oAuth2Provider) {
        String idToken = getIdToken(token);
        if (oAuth2Provider == KAKAO) {
            return kakaoIdTokenFetcher.fetchKakaoIdToken(idToken).getEmail();
        }

        if (oAuth2Provider == GOOGLE) {
            return googleIdTokenFetcher.fetchGoogleIdToken(idToken).getEmail();
        }

        throw new NotSupportedOAuth2ProviderException();
    }

    private String getIdToken(String bearerToken) {
        return bearerToken.substring(BEGIN_INDEX);
    }

}
