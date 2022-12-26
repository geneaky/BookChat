package toy.bookchat.bookchat.config.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Configuration
@EnableWebSocketMessageBroker
public class StompConfig implements WebSocketMessageBrokerConfigurer {

    private final ChannelInterceptor webSocketTokenValidationInterceptor;
    private final WebSocketHandShakeTokenValidationInterceptor webSocketHandShakeTokenValidationInterceptor;
    private final StompSubProtocolErrorHandler stompErrorHandler;

    public StompConfig(ChannelInterceptor webSocketTokenValidationInterceptor,
        WebSocketHandShakeTokenValidationInterceptor webSocketHandShakeTokenValidationInterceptor,
        StompSubProtocolErrorHandler stompErrorHandler) {
        this.webSocketTokenValidationInterceptor = webSocketTokenValidationInterceptor;
        this.webSocketHandShakeTokenValidationInterceptor = webSocketHandShakeTokenValidationInterceptor;
        this.stompErrorHandler = stompErrorHandler;
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketTokenValidationInterceptor);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.setErrorHandler(stompErrorHandler)
            .addEndpoint("/stomp-connection")
            .addInterceptors(webSocketHandShakeTokenValidationInterceptor);

    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setPathMatcher(new AntPathMatcher("."));
        registry.setApplicationDestinationPrefixes("/pub");
        registry.enableStompBrokerRelay("/queue", "/topic", "/exchange", "/amq/queue");
    }
}
