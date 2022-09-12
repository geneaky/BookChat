package toy.bookchat.bookchat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import toy.bookchat.bookchat.security.openid.keys.GooglePublicKeys;
import toy.bookchat.bookchat.security.openid.keys.KakaoPublicKeys;

@Component
@RequiredArgsConstructor
public class OpenIdTokenConfig {

    private final RestTemplate restTemplate;

    private final KakaoPublicKeys kakaoPublicKeys;

    private final GooglePublicKeys googlePublicKeys;


    public byte[] getSecret(String keyId) {

    }
}
