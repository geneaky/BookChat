package toy.bookchat.bookchat.config.websocket;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.util.Arrays;
import java.util.List;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenManager;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPayload;

@Component
public class MessageAuthenticationArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenManager jwtTokenManager;

    public MessageAuthenticationArgumentResolver(JwtTokenManager jwtTokenManager) {
        this.jwtTokenManager = jwtTokenManager;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Arrays.stream(parameter.getParameterAnnotations())
            .anyMatch(annotation -> annotation.annotationType().equals(UserPayload.class));
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, Message<?> message) throws Exception {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        return resolveTokenPayloadFromAccessor(accessor);
    }

    private TokenPayload resolveTokenPayloadFromAccessor(StompHeaderAccessor accessor) {
        try {
            String bearerToken = jwtTokenManager.extractTokenFromAuthorizationHeader(
                getAuthorizationHeader(accessor));
            return jwtTokenManager.getTokenPayloadFromToken(bearerToken);
        } catch (Exception exception) {
            throw new MessageDeliveryException("Access Denied");
        }
    }

    private String getAuthorizationHeader(StompHeaderAccessor accessor) {
        List<String> nativeHeader = accessor.getNativeHeader(AUTHORIZATION);
        if (nativeHeader != null) {
            return nativeHeader.get(0);
        }
        return null;
    }
}
