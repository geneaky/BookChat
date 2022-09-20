package toy.bookchat.bookchat.config;

import static org.mockito.ArgumentMatchers.any;

import java.security.Key;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@ExtendWith(MockitoExtension.class)
class OpenIdTokenConfigTest {

    @Mock
    RestTemplateBuilder restTemplateBuilder;

    @InjectMocks
    OpenIdTokenConfig openIdTokenConfig;

    @Test
    public void keyId_oAuth2Provider로_요청시_일치하는_publickey반환() throws Exception {

        Key publicKey = openIdTokenConfig.getPublicKey(any(), OAuth2Provider.KAKAO);


    }
}