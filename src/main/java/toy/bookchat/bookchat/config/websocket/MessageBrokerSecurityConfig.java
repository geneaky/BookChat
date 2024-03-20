package toy.bookchat.bookchat.config.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@EnableWebSocketMessageBroker
@Configuration(proxyBeanMethods = false)
public class MessageBrokerSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    private final ChannelInterceptor webSocketTokenValidationInterceptor;
    private final ExternalBrokerProperties externalBrokerProperties;

    public MessageBrokerSecurityConfig(ChannelInterceptor webSocketTokenValidationInterceptor, ExternalBrokerProperties externalBrokerProperties) {
        this.webSocketTokenValidationInterceptor = webSocketTokenValidationInterceptor;
        this.externalBrokerProperties = externalBrokerProperties;
    }

    @Override
    protected void customizeClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketTokenValidationInterceptor);
        registration.taskExecutor().corePoolSize(10);
        registration.taskExecutor().maxPoolSize(10);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp-connection").setAllowedOrigins("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/subscriptions");
        registry.setUserDestinationPrefix("/user");
        registry.enableStompBrokerRelay("/topic")
            .setRelayHost(externalBrokerProperties.getHost())
            .setVirtualHost(externalBrokerProperties.getVirtualHost())
            .setRelayPort(externalBrokerProperties.getStompPort())
            .setClientLogin(externalBrokerProperties.getLogin())
            .setClientPasscode(externalBrokerProperties.getPasscode());
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }

}
