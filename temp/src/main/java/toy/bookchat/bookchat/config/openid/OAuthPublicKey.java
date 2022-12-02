package toy.bookchat.bookchat.config.openid;

import java.security.Key;
import java.security.KeyFactory;

public interface OAuthPublicKey {

    Key getKey(String keyId, KeyFactory keyFactory);
}
