package toy.bookchat.bookchat.config.websocket;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "rabbit")
public class ExternalBrokerProperties {

    private final String host;
    private final String virtualHost;
    private final int stompPort;
    private final String login;
    private final String passcode;

    public ExternalBrokerProperties(String host, String virtualHost, int stompPort, String login,
        String passcode) {
        this.host = host;
        this.virtualHost = virtualHost;
        this.stompPort = stompPort;
        this.login = login;
        this.passcode = passcode;
    }
}
