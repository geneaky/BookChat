package toy.bookchat.bookchat.security.token.openid.kakao;

import toy.bookchat.bookchat.security.token.openid.KakaoIdToken;

public interface KakaoIdTokenFetcher {

    KakaoIdToken fetchKakaoIdToken(String idToken);
}
