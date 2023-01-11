package toy.bookchat.bookchat.security.token.openid.keys;

import java.security.Key;
import java.security.KeyFactory;
import toy.bookchat.bookchat.config.token.openid.OAuthPublicKey;

public class GooglePublicKeys implements OAuthPublicKey {

    @Override
    public Key getKey(String keyId, KeyFactory keyFactory) {
        return null;
    }
}
