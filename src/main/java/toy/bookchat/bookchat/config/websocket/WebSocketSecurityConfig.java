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
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    private final ChannelInterceptor webSocketTokenValidationInterceptor;
    private final StompSubProtocolErrorHandler stompErrorHandler;
    private final MessageAuthenticationArgumentResolver messageAuthenticationArgumentResolver;

    public WebSocketSecurityConfig(ChannelInterceptor webSocketTokenValidationInterceptor,
        StompSubProtocolErrorHandler stompErrorHandler,
        MessageAuthenticationArgumentResolver messageAuthenticationArgumentResolver) {
        this.webSocketTokenValidationInterceptor = webSocketTokenValidationInterceptor;
        this.stompErrorHandler = stompErrorHandler;
        this.messageAuthenticationArgumentResolver = messageAuthenticationArgumentResolver;
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
        registry.setErrorHandler(stompErrorHandler)
            .addEndpoint("/stomp-connection").setAllowedOrigins("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setPreservePublishOrder(true); //broker로 메시지 전달 순서 보장
        registry.setApplicationDestinationPrefixes("/subscriptions");
        registry.setUserDestinationPrefix("/user");
        registry.enableStompBrokerRelay("/topic", "/exchange")
            .setRelayHost("localhost")
            .setVirtualHost("/")
            .setRelayPort(61613)
            .setClientLogin("guest")
            .setClientPasscode("guest");
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }

}
