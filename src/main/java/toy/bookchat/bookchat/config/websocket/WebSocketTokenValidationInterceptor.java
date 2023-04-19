package toy.bookchat.bookchat.config.websocket;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.messaging.simp.stomp.StompCommand.CONNECT;
import static org.springframework.messaging.simp.stomp.StompCommand.SEND;
import static org.springframework.messaging.simp.stomp.StompCommand.SUBSCRIBE;

import java.util.List;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenManager;

@Component
public class WebSocketTokenValidationInterceptor implements ChannelInterceptor {

    private final JwtTokenManager jwtTokenManager;

    public WebSocketTokenValidationInterceptor(JwtTokenManager jwtTokenManager) {
        this.jwtTokenManager = jwtTokenManager;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (CONNECT.equals(accessor.getCommand()) || SUBSCRIBE.equals(
            accessor.getCommand()) || SEND.equals(accessor.getCommand())) {
            try {
                String bearerToken = jwtTokenManager.extractTokenFromAuthorizationHeader(
                    getAuthorizationHeader(accessor));
                jwtTokenManager.getTokenPayloadFromToken(bearerToken);
            } catch (Exception exception) {
                throw new MessageDeliveryException("Access Denied");
            }
        }
        return message;
    }

    private String getAuthorizationHeader(StompHeaderAccessor accessor) {
        List<String> nativeHeader = accessor.getNativeHeader(AUTHORIZATION);
        if (nativeHeader != null) {
            return nativeHeader.get(0);
        }
        return null;
    }
}
