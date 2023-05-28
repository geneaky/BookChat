package toy.bookchat.bookchat.config.websocket;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    private final ChannelInterceptor webSocketTokenValidationInterceptor;
    private final MessageAuthenticationArgumentResolver messageAuthenticationArgumentResolver;
    private final ExternalBrokerProperties externalBrokerProperties;

    public WebSocketSecurityConfig(ChannelInterceptor webSocketTokenValidationInterceptor,
        MessageAuthenticationArgumentResolver messageAuthenticationArgumentResolver,
        ExternalBrokerProperties externalBrokerProperties) {
        this.webSocketTokenValidationInterceptor = webSocketTokenValidationInterceptor;
        this.messageAuthenticationArgumentResolver = messageAuthenticationArgumentResolver;
        this.externalBrokerProperties = externalBrokerProperties;
    }

    @Override
    protected void customizeClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketTokenValidationInterceptor);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(messageAuthenticationArgumentResolver);
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
