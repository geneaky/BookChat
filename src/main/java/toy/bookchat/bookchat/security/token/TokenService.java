package toy.bookchat.bookchat.security.token;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.api.dto.Token;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.exception.notfound.user.UserNotFoundException;
import toy.bookchat.bookchat.security.token.dto.RefreshTokenRequest;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenManager;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenProvider;
import toy.bookchat.bookchat.security.token.jwt.RefreshTokenRepository;

@Slf4j
@Service
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenManager jwtTokenManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public TokenService(JwtTokenProvider jwtTokenProvider, JwtTokenManager jwtTokenManager,
        RefreshTokenRepository refreshTokenRepository,
        UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtTokenManager = jwtTokenManager;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Token generateToken(RefreshTokenRequest refreshTokenRequest) {
        String accessToken = generateAccessToken(refreshTokenRequest);
        String refreshToken = generateOrUsingDefaultRefreshToken(refreshTokenRequest);

        return Token.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }

    private String generateAccessToken(RefreshTokenRequest refreshTokenRequest) {

        return jwtTokenProvider.createAccessToken(
            getUserFromRefreshToken(refreshTokenRequest));
    }

    private String generateOrUsingDefaultRefreshToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();

        if (shouldBeRenew(refreshToken)) {
            refreshToken = renewRefreshToken(refreshTokenRequest);
        }

        return refreshToken;
    }

    private String renewRefreshToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken;
        refreshToken = jwtTokenProvider.createRefreshToken(
            getUserFromRefreshToken(refreshTokenRequest));

        refreshTokenRepository.findByUserId(jwtTokenManager.getUserIdFromToken(refreshToken))
            .orElseThrow(IllegalStateException::new)
            .changeRefreshToken(refreshToken);

        return refreshToken;
    }

    private boolean shouldBeRenew(String refreshToken) {
        return jwtTokenManager.shouldRefreshTokenBeRenew(refreshToken);
    }

    private User getUserFromRefreshToken(RefreshTokenRequest refreshTokenRequest) {
        return userRepository.findById(
                jwtTokenManager.getUserIdFromToken(refreshTokenRequest.getRefreshToken()))
            .orElseThrow(UserNotFoundException::new);
    }

}
