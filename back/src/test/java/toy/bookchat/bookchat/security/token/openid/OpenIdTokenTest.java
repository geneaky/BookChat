package toy.bookchat.bookchat.security.token.openid;

import static io.jsonwebtoken.JwsHeader.KEY_ID;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.security.exception.DenidedTokenException;
import toy.bookchat.bookchat.security.exception.ExpiredTokenException;
import toy.bookchat.bookchat.security.exception.IllegalStandardTokenException;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@ExtendWith(MockitoExtension.class)
class OpenIdTokenTest {

    @Test
    void 만료된_토큰으로_처리_요청시_예외발생() throws Exception {
        PrivateKey privateKey = getPrivateKey();

        String token = Jwts.builder()
            .setSubject("1234")
            .setHeaderParam(KEY_ID, "abcdedf")
            .setIssuer("https://kauth.kakao.com")
            .setExpiration(new Date(0))
            .signWith(SignatureAlgorithm.RS256, privateKey)
            .compact();

        OpenIdToken openIdToken = OpenIdToken.of(token);

        open

        assertThatThrownBy(() -> {
            openIdTokenManager.getOAuth2MemberNumberFromToken(token, OAuth2Provider.KAKAO);
        }).isInstanceOf(ExpiredTokenException.class);
    }

    @Test
    void 임의로_수정한_토큰으로_처리_요청시_예외발생() throws Exception {
        PrivateKey privateKey = getPrivateKey();
        PublicKey publicKey = getPublicKey();

        String token = getMockOpenIdToken(privateKey);

        when(openIdTokenConfig.getPublicKey(any(), any())).thenReturn(publicKey);

        assertThatThrownBy(() -> {
            openIdTokenManager.getOAuth2MemberNumberFromToken(token + "test", OAuth2Provider.KAKAO);
        }).isInstanceOf(DenidedTokenException.class);
    }

    @Test
    void 발급_인증기관_정보_없을시_예외발생() throws Exception {
        PrivateKey privateKey = getPrivateKey();
        PublicKey publicKey = getPublicKey();

        String token = "Bearer " + Jwts.builder()
            .setSubject("1234")
            .setHeaderParam(KEY_ID, "abcdedf")
            .signWith(SignatureAlgorithm.RS256, privateKey)
            .compact();

        when(openIdTokenConfig.getPublicKey(any(), any())).thenReturn(publicKey);

        assertThatThrownBy(() -> {
            openIdTokenManager.getOAuth2MemberNumberFromToken(token, OAuth2Provider.KAKAO);
        }).isInstanceOf(IllegalStandardTokenException.class);
    }
}