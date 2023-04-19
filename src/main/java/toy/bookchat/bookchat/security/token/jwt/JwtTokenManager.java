package toy.bookchat.bookchat.security.token.jwt;

import toy.bookchat.bookchat.security.user.TokenPayload;

public interface JwtTokenManager {

    String extractTokenFromAuthorizationHeader(String header);

    Long getUserIdFromToken(String token);

    String getOAuth2MemberNumberFromToken(String token);

    String getUserEmailFromToken(String token);

    boolean shouldRefreshTokenBeRenew(String token);

    TokenPayload getTokenPayloadFromToken(String token);
}
