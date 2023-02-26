package toy.bookchat.bookchat.config.websocket;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static toy.bookchat.bookchat.domain.common.AuthConstants.BEARER;
import static toy.bookchat.bookchat.domain.common.AuthConstants.BEGIN_INDEX;

import java.util.Arrays;
import java.util.Objects;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import toy.bookchat.bookchat.exception.security.DeniedTokenException;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenManager;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPayload;

@Component
public class MessageAuthenticationArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenManager jwtTokenManager;

    public MessageAuthenticationArgumentResolver(JwtTokenManager jwtTokenManager) {
        this.jwtTokenManager = jwtTokenManager;
    }

    private static String getBearerTokenFromNativeHeader(StompHeaderAccessor accessor) {
        return Objects.requireNonNull(accessor.getNativeHeader(AUTHORIZATION)).get(0);
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
            String token = getJwtTokenFromMessage(accessor);
            return jwtTokenManager.getTokenPayloadFromToken(token);
        } catch (Exception exception) {
            throw new MessageDeliveryException("Access Denied");
        }
    }

    private String getJwtTokenFromMessage(StompHeaderAccessor accessor) {
        String bearerToken = getBearerTokenFromNativeHeader(accessor);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
            return bearerToken.substring(BEGIN_INDEX);
        }
        throw new DeniedTokenException();
    }
}
