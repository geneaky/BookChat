package toy.bookchat.bookchat.security.token.openid.keys;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.Base64Utils;
import toy.bookchat.bookchat.config.openid.PublicKeys;
import toy.bookchat.bookchat.security.exception.ExpiredPublicKeyCachedException;
import toy.bookchat.bookchat.security.exception.WrongKeySpecException;

@Getter
@Setter
@NoArgsConstructor
public class KakaoPublicKeys implements PublicKeys {

    private List<KakakoPublicKey> keys;

    public KakaoPublicKeys(List<KakakoPublicKey> keys) {
        this.keys = keys;
    }

    public Key getKey(String keyId, KeyFactory keyFactory) {
        try {
            for (KakakoPublicKey publicKey : this.keys) {
                if (keyId.equals(publicKey.getKid())) {
                    BigInteger modulus = new BigInteger(1,
                        Base64Utils.decode(publicKey.getN().getBytes()));
                    BigInteger exponent = new BigInteger(1,
                        Base64Utils.decode(publicKey.getE().getBytes()));
                    return keyFactory.generatePublic(new RSAPublicKeySpec(modulus, exponent));
                }
            }
        } catch (InvalidKeySpecException exception) {
            throw new WrongKeySpecException("Wrong KeySpec");
        }
        throw new ExpiredPublicKeyCachedException("Retry Please");
    }
}
