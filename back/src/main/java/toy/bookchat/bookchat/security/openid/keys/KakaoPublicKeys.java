package toy.bookchat.bookchat.security.openid.keys;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KakaoPublicKeys {

    private LocalDateTime localDateTime;

    private List<KakakoPublicKey> keys;
}
