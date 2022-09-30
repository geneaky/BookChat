package toy.bookchat.bookchat.security.token;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import toy.bookchat.bookchat.domain.user.api.dto.Token;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.token.dto.RefreshTokenRequestDto;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenManager;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenProvider;

@Slf4j
@Service
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenManager jwtTokenManager;

    public TokenService(JwtTokenProvider jwtTokenProvider, JwtTokenManager jwtTokenManager) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtTokenManager = jwtTokenManager;
    }

    public Token generateToken(RefreshTokenRequestDto refreshTokenRequestDto) {
        String userName = jwtTokenManager.getOAuth2MemberNumberFromToken(refreshTokenRequestDto.getRefreshToken());
        String userEmail = jwtTokenManager.getUserEmailFromToken(refreshTokenRequestDto.getRefreshToken());
        OAuth2Provider oAuth2Provider = jwtTokenManager.getOAuth2ProviderFromToken(
                refreshTokenRequestDto.getRefreshToken());

        String accessToken = jwtTokenProvider.createAccessToken(userName, userEmail, oAuth2Provider);
        String refreshToken = refreshTokenRequestDto.getRefreshToken();

        if(jwtTokenManager.shouldRefreshTokenBeRenewed(refreshToken)){
            refreshToken = jwtTokenProvider.createRefreshToken(userName, userEmail, oAuth2Provider);
        }

        return Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
