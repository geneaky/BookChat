package toy.bookchat.bookchat.domain;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import toy.bookchat.bookchat.security.ipblock.IpBlockManager;
/*
    controller 테스트는 security까지 포함시켜 테스트하여 restdoc 문서에
    token, provider_type과 같은 정보가 포함되도록 진행
 */
public abstract class AuthenticationTestExtension {

    @MockBean
    IpBlockManager ipBlockManager;

    @BeforeEach
    public void setUp() {
        when(ipBlockManager.validateRequest(any())).thenReturn(true);
    }

}
