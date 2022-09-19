package toy.bookchat.bookchat.security.openid.keys;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KakakoPublicKey {

    private String kid;
    private String kty;
    private String alg;
    private String use;
    private String n;
    private String e;

}
