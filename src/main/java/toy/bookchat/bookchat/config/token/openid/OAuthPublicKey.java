package toy.bookchat.bookchat.config.token.openid;

import java.security.Key;
import java.security.KeyFactory;

public interface OAuthPublicKey {

    Key getKey(String keyId, KeyFactory keyFactory);
}
