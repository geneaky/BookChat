package toy.bookchat.bookchat.security.token.openid.keys;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import toy.bookchat.bookchat.config.openid.PublicKeys;

@Getter
@Setter
@NoArgsConstructor
public class KakaoPublicKeys implements PublicKeys {

    private List<KakakoPublicKey> keys;
}
