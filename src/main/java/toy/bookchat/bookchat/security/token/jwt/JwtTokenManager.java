package toy.bookchat.bookchat.security.token.jwt;

import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

public interface JwtTokenManager {
    String getOAuth2MemberNumberFromToken(String token);

    String getUserEmailFromToken(String token);

    OAuth2Provider getOAuth2ProviderFromToken(String token);

    boolean shouldRefreshTokenBeRenewed(String token);
}
