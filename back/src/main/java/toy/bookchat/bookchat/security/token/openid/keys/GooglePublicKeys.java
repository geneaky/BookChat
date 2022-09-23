package toy.bookchat.bookchat.security.token.openid.keys;

import toy.bookchat.bookchat.config.openid.OAuthPublicKey;

import java.security.Key;
import java.security.KeyFactory;

public class GooglePublicKeys implements OAuthPublicKey {

    @Override
    public Key getKey(String keyId, KeyFactory keyFactory) {
        return null;
    }
}
