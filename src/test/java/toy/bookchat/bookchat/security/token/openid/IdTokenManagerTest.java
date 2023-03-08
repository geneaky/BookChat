package toy.bookchat.bookchat.security.token.openid;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static toy.bookchat.bookchat.security.oauth.OAuth2Provider.GOOGLE;
import static toy.bookchat.bookchat.security.oauth.OAuth2Provider.KAKAO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.exception.security.NotSupportedOAuth2ProviderException;
import toy.bookchat.bookchat.security.token.openid.google.GoogleIdTokenFetcher;
import toy.bookchat.bookchat.security.token.openid.google.GoogleIdTokenWrapper;
import toy.bookchat.bookchat.security.token.openid.kakao.KakaoIdTokenFetcher;

@ExtendWith(MockitoExtension.class)
class IdTokenManagerTest {

    @Mock
    KakaoIdTokenFetcher kakaoIdTokenFetcher;
    @Mock
    GoogleIdTokenFetcher googleIdTokenFetcher;
    @InjectMocks
    IdTokenManagerImpl idTokenManager;


    @Test
    void 토큰에서_kakao_사용자_원천_회원번호_추출_성공() throws Exception {
        KakaoIdToken kakaoIdToken = KakaoIdToken.from("1234", "hBy");

        when(kakaoIdTokenFetcher.fetchKakaoIdToken(any())).thenReturn(kakaoIdToken);

        String expectMemberNumber = idTokenManager.getOAuth2MemberNumberFromIdToken(
            "Bearer 1aJPkKol",
            KAKAO);
        assertThat(expectMemberNumber).isEqualTo(kakaoIdToken.getMemberNumber());
    }

    @Test
    void 토큰에서_google_사용자_원천_회원번호_추출_성공() throws Exception {
        GoogleIdTokenWrapper googleIdTokenWrapper = GoogleIdTokenWrapper.from("1234",
            "test@gmail.com");

        when(googleIdTokenFetcher.fetchGoogleIdToken(any())).thenReturn(googleIdTokenWrapper);
        String expectSubject = idTokenManager.getOAuth2MemberNumberFromIdToken("Bearer 5j0",
            GOOGLE);

        assertThat(expectSubject).isEqualTo(googleIdTokenWrapper.getMemberNumber());
    }

    @Test
    void 지원하지않는_oauth2_provider로_토큰에서_사용자_원천_회원번호_추출_시도시_예외발생() throws Exception {
        assertThatThrownBy(() -> {
            idTokenManager.getOAuth2MemberNumberFromIdToken("Bearer GHEB", null);
        }).isInstanceOf(NotSupportedOAuth2ProviderException.class);
    }

    @Test
    void 토큰에서_kakao_사용자_원천_이메일_추출_성공() throws Exception {
        KakaoIdToken kakaoIdToken = KakaoIdToken.from("1234", "test@kakao.com");

        when(kakaoIdTokenFetcher.fetchKakaoIdToken(any())).thenReturn(kakaoIdToken);

        String expectEmail = idTokenManager.getUserEmailFromToken("Bearer 0RA", KAKAO);

        assertThat(expectEmail).isEqualTo(kakaoIdToken.getEmail());
    }

    @Test
    void 토큰에서_google_사용자_원천_이메일_추출_성공() throws Exception {
        GoogleIdTokenWrapper googleIdTokenWrapper = GoogleIdTokenWrapper.from("1234",
            "test@gmail.com");

        when(googleIdTokenFetcher.fetchGoogleIdToken(any())).thenReturn(googleIdTokenWrapper);

        String expectEmail = idTokenManager.getUserEmailFromToken("Bearer I99", GOOGLE);

        assertThat(expectEmail).isEqualTo(googleIdTokenWrapper.getEmail());
    }

    @Test
    void 지원하지않는_oauth2_provider로_토큰에서_사용자_원천_이메일_추출시도시_예외발생() throws Exception {
        assertThatThrownBy(() -> {
            idTokenManager.getUserEmailFromToken("Bearer G2I9hTP6", null);
        }).isInstanceOf(NotSupportedOAuth2ProviderException.class);
    }
}