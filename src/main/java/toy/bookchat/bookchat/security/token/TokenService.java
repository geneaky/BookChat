package toy.bookchat.bookchat.security.token;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.api.dto.Token;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.exception.user.UserNotFoundException;
import toy.bookchat.bookchat.security.token.dto.RefreshTokenRequestDto;
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
    public Token generateToken(RefreshTokenRequestDto refreshTokenRequestDto) {
        String accessToken = generateAccessToken(refreshTokenRequestDto);
        String refreshToken = generateOrUsingDefaultRefreshToken(refreshTokenRequestDto);

        return Token.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }

    private String generateAccessToken(RefreshTokenRequestDto refreshTokenRequestDto) {

        return jwtTokenProvider.createAccessToken(
            getUserFromRefreshToken(refreshTokenRequestDto));
    }

    private String generateOrUsingDefaultRefreshToken(
        RefreshTokenRequestDto refreshTokenRequestDto) {
        String refreshToken = refreshTokenRequestDto.getRefreshToken();

        if (shouldBeRenew(refreshToken)) {
            refreshToken = renewRefreshToken(refreshTokenRequestDto);
        }

        return refreshToken;
    }

    private String renewRefreshToken(RefreshTokenRequestDto refreshTokenRequestDto) {
        String refreshToken;
        refreshToken = jwtTokenProvider.createRefreshToken(
            getUserFromRefreshToken(refreshTokenRequestDto));

        refreshTokenRepository.findByUserId(jwtTokenManager.getUserIdFromToken(refreshToken))
            .orElseThrow(() -> {
                throw new IllegalStateException();
            })
            .changeRefreshToken(refreshToken);

        return refreshToken;
    }

    private boolean shouldBeRenew(String refreshToken) {
        return jwtTokenManager.shouldRefreshTokenBeRenew(refreshToken);
    }

    private User getUserFromRefreshToken(RefreshTokenRequestDto refreshTokenRequestDto) {
        return userRepository.findById(
                jwtTokenManager.getUserIdFromToken(refreshTokenRequestDto.getRefreshToken()))
            .orElseThrow(() -> {
                throw new UserNotFoundException("Can't Find User");
            });
    }

}
