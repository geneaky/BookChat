package toy.bookchat.bookchat.domain;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import toy.bookchat.bookchat.security.ipblock.IpBlockManager;

public abstract class AuthenticationTestExtension {

    @MockBean
    IpBlockManager ipBlockManager;

    @BeforeEach
    public void setUp() {
        when(ipBlockManager.validateRequest(any())).thenReturn(true);
    }

}
