package toy.bookchat.bookchat.security.token.openid;

import static toy.bookchat.bookchat.security.oauth.OAuth2Provider.KAKAO;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class KakaoIdToken {

    private final String memberNumber;
    private final String email;

    public static KakaoIdToken from(String memberNumber, String email) {
        return new KakaoIdToken(memberNumber + KAKAO.getValue(), email);
    }
}
