package token.openid;

import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

public interface OpenIdTokenManager {

    String getOAuth2MemberNumberFromToken(String token, OAuth2Provider oAuth2Provider);

    String getUserEmailFromToken(String token, OAuth2Provider oAuth2Provider);
}
