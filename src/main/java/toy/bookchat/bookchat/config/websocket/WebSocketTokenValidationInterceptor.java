package toy.bookchat.bookchat.config.websocket;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.messaging.simp.stomp.StompCommand.CONNECT;
import static org.springframework.messaging.simp.stomp.StompCommand.SEND;
import static org.springframework.messaging.simp.stomp.StompCommand.SUBSCRIBE;
import static org.springframework.messaging.simp.stomp.StompCommand.UNSUBSCRIBE;

import java.util.List;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenManager;
import toy.bookchat.bookchat.security.user.TokenPayload;

@Component
public class WebSocketTokenValidationInterceptor implements ChannelInterceptor {

    public static final int TOPIC_NAME_LENGTH = 7;
    private final JwtTokenManager jwtTokenManager;
    private final ParticipantRepository participantRepository;

    public WebSocketTokenValidationInterceptor(JwtTokenManager jwtTokenManager,
        ParticipantRepository participantRepository) {
        this.jwtTokenManager = jwtTokenManager;
        this.participantRepository = participantRepository;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (CONNECT.equals(accessor.getCommand()) || SUBSCRIBE.equals(
            accessor.getCommand()) || SEND.equals(accessor.getCommand())) {
            try {
                String bearerToken = jwtTokenManager.extractTokenFromAuthorizationHeader(
                    getAuthorizationHeader(accessor));
                TokenPayload payload = jwtTokenManager.getTokenPayloadFromToken(
                    bearerToken);

                if (SUBSCRIBE.equals(accessor.getCommand())) {
                    Long userId = payload.getUserId();
                    String destination = accessor.getDestination().substring(TOPIC_NAME_LENGTH);
                    participantRepository.connect(userId, destination);
                }

                if (UNSUBSCRIBE.equals(accessor.getCommand())) {
                    Long userId = payload.getUserId();
                    String destination = accessor.getDestination().substring(TOPIC_NAME_LENGTH);
                    participantRepository.disconnect(userId, destination);
                }
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
