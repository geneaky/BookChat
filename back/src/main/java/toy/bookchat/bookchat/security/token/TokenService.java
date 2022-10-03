package toy.bookchat.bookchat.security.token;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.user.api.dto.Token;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.token.dto.RefreshTokenRequestDto;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenManager;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenProvider;
import toy.bookchat.bookchat.security.token.jwt.RefreshToken;
import toy.bookchat.bookchat.security.token.jwt.RefreshTokenRepository;

@Slf4j
@Service
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenManager jwtTokenManager;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenService(JwtTokenProvider jwtTokenProvider, JwtTokenManager jwtTokenManager,
        RefreshTokenRepository refreshTokenRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtTokenManager = jwtTokenManager;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public Token generateToken(RefreshTokenRequestDto refreshTokenRequestDto) {
        String accessToken = createAccessToken(refreshTokenRequestDto);
        String refreshToken = createOrDefaultRefreshToken(refreshTokenRequestDto);

        return Token.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }

    private String createAccessToken(RefreshTokenRequestDto refreshTokenRequestDto) {
        String userName = jwtTokenManager.getOAuth2MemberNumberFromToken(
            refreshTokenRequestDto.getRefreshToken());
        String userEmail = jwtTokenManager.getUserEmailFromToken(
            refreshTokenRequestDto.getRefreshToken());
        OAuth2Provider oAuth2Provider = jwtTokenManager.getOAuth2ProviderFromToken(
            refreshTokenRequestDto.getRefreshToken());

        return jwtTokenProvider.createAccessToken(userName, userEmail,
            oAuth2Provider);
    }

    private String createOrDefaultRefreshToken(RefreshTokenRequestDto refreshTokenRequestDto) {
        String userName = jwtTokenManager.getOAuth2MemberNumberFromToken(
            refreshTokenRequestDto.getRefreshToken());
        String userEmail = jwtTokenManager.getUserEmailFromToken(
            refreshTokenRequestDto.getRefreshToken());
        OAuth2Provider oAuth2Provider = jwtTokenManager.getOAuth2ProviderFromToken(
            refreshTokenRequestDto.getRefreshToken());

        String refreshToken = refreshTokenRequestDto.getRefreshToken();

        if (jwtTokenManager.shouldRefreshTokenBeRenewed(refreshToken)) {
            refreshToken = jwtTokenProvider.createRefreshToken(userName, userEmail, oAuth2Provider);
            RefreshToken userRefreshToken = refreshTokenRepository.findByUserName(userName);
            userRefreshToken.changeRefreshToken(refreshToken);
        }

        return refreshToken;
    }

}
