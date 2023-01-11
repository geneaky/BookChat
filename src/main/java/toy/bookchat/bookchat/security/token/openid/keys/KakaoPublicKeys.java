package toy.bookchat.bookchat.security.token.openid.keys;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.Base64Utils;
import toy.bookchat.bookchat.config.token.openid.OAuthPublicKey;
import toy.bookchat.bookchat.exception.security.ExpiredPublicKeyCachedException;
import toy.bookchat.bookchat.exception.security.WrongKeySpecException;

@Getter
@Setter
@NoArgsConstructor
public class KakaoPublicKeys implements OAuthPublicKey {

    private List<KakakoPublicKey> keys;

    @Override
    public Key getKey(String keyId, KeyFactory keyFactory) {
        try {
            return searchPublicKey(keyId, keyFactory);
        } catch (InvalidKeySpecException exception) {
            throw new WrongKeySpecException();
        }
    }

    private PublicKey searchPublicKey(String keyId, KeyFactory keyFactory)
        throws InvalidKeySpecException {
        for (KakakoPublicKey publicKey : this.keys) {
            if (keyId.equals(publicKey.getKid())) {
                return generateKakaoPublicKey(keyFactory, publicKey);
            }
        }
        throw new ExpiredPublicKeyCachedException();
    }

    private PublicKey generateKakaoPublicKey(KeyFactory keyFactory, KakakoPublicKey publicKey)
        throws InvalidKeySpecException {
        BigInteger modulus = new BigInteger(1,
            Base64Utils.decodeFromUrlSafeString(publicKey.getN()));
        BigInteger exponent = new BigInteger(1,
            Base64Utils.decodeFromUrlSafeString(publicKey.getE()));
        return keyFactory.generatePublic(new RSAPublicKeySpec(modulus, exponent));
    }
}
