package token.jwt;

import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.user.TokenPayload;

public interface JwtTokenManager {

    Long getUserIdFromToken(String token);

    String getOAuth2MemberNumberFromToken(String token);

    String getUserEmailFromToken(String token);

    OAuth2Provider getOAuth2ProviderFromToken(String token);

    boolean shouldRefreshTokenBeRenew(String token);

    TokenPayload getTokenPayloadFromToken(String token);
}
