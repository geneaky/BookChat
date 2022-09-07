package toy.bookchat.bookchat.security.openid;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.config.JwtTokenConfig;
import toy.bookchat.bookchat.security.exception.DenidedTokenException;
import toy.bookchat.bookchat.security.exception.ExpiredTokenException;

@ExtendWith(MockitoExtension.class)
class OpenIdTokenManagerTest {

    @Mock
    JwtTokenConfig jwtTokenConfig;

    @InjectMocks
    OpenIdTokenManager openIdTokenManager;

    @Test
    public void 토큰에서_사용자_원천_회원번호_추출_성공() throws Exception {

        String token = Jwts.builder()
            .setSubject("1234")
            .signWith(SignatureAlgorithm.HS256, "test_secret")
            .compact();

        when(jwtTokenConfig.getSecret()).thenReturn("test_secret");

        assertThat(openIdTokenManager.getOauth2MemberNumberFromRequest(token)).isEqualTo("1234");
    }

    @Test
    public void 만료된_토큰으로_처리_요청시_예외발생() throws Exception {
        String token = Jwts.builder()
            .setSubject("1234")
            .setExpiration(new Date(0))
            .signWith(SignatureAlgorithm.HS256, "test_secret")
            .compact();

        when(jwtTokenConfig.getSecret()).thenReturn("test_secret");

        assertThatThrownBy(() -> {
            openIdTokenManager.getOauth2MemberNumberFromRequest(token);
        }).isInstanceOf(ExpiredTokenException.class);
    }

    @Test
    public void 유효하지않은_토큰으로_처리_요청시_예외발생() throws Exception {
        String token = Jwts.builder()
            .setSubject("1234")
            .signWith(SignatureAlgorithm.HS256, "test_secret")
            .compact();

        when(jwtTokenConfig.getSecret()).thenReturn("test_secret");

        assertThatThrownBy(() -> {
            openIdTokenManager.getOauth2MemberNumberFromRequest(token + "test");
        }).isInstanceOf(DenidedTokenException.class);
    }


}