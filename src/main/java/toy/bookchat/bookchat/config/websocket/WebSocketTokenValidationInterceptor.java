package toy.bookchat.bookchat.config.websocket;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.messaging.simp.stomp.StompCommand.CONNECT;
import static org.springframework.messaging.simp.stomp.StompCommand.SEND;
import static org.springframework.messaging.simp.stomp.StompCommand.SUBSCRIBE;
import static toy.bookchat.bookchat.domain.common.AuthConstants.BEARER;
import static toy.bookchat.bookchat.domain.common.AuthConstants.BEGIN_INDEX;

import java.util.List;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import toy.bookchat.bookchat.exception.security.DenidedTokenException;
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
                String token = getJwtTokenFromMessage(accessor);
                jwtTokenManager.getTokenPayloadFromToken(token);
            } catch (Exception exception) {
                throw new MessageDeliveryException("Access Denied");
            }
        }
        return message;
    }

    private String getJwtTokenFromMessage(StompHeaderAccessor accessor) {
        List<String> nativeHeader = accessor.getNativeHeader(AUTHORIZATION);
        String bearerToken = nativeHeader.get(0);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
            return bearerToken.substring(BEGIN_INDEX);
        }
        throw new DenidedTokenException();
    }
}
