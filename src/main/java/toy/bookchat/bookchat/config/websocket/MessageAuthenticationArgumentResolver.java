package toy.bookchat.bookchat.config.websocket;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static toy.bookchat.bookchat.domain.common.AuthConstants.BEARER;
import static toy.bookchat.bookchat.domain.common.AuthConstants.BEGIN_INDEX;

import java.lang.annotation.Annotation;
import java.util.List;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import toy.bookchat.bookchat.exception.security.DenidedTokenException;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenManager;
import toy.bookchat.bookchat.security.user.UserPayload;

@Component
public class MessageAuthenticationArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenManager jwtTokenManager;

    public MessageAuthenticationArgumentResolver(JwtTokenManager jwtTokenManager) {
        this.jwtTokenManager = jwtTokenManager;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return findMethodAnnotation(UserPayload.class, parameter) != null;
        SimpAnnotationMethodMessageHandler
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, Message<?> message) throws Exception {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        try {
            String token = getJwtTokenFromMessage(accessor);
            return jwtTokenManager.getTokenPayloadFromToken(token);
        } catch (Exception exception) {
            throw new MessageDeliveryException("Access Denied");
        }
    }

    private String getJwtTokenFromMessage(StompHeaderAccessor accessor) {
        List<String> nativeHeader = accessor.getNativeHeader(AUTHORIZATION);
        String bearerToken = nativeHeader.get(0);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
            return bearerToken.substring(BEGIN_INDEX);
        }
        throw new DenidedTokenException();
    }

    private <T extends Annotation> T findMethodAnnotation(Class<T> annotationClass,
        MethodParameter parameter) {
        T annotation = parameter.getParameterAnnotation(annotationClass);
        if (annotation != null) {
            return annotation;
        }
        Annotation[] annotationsToSearch = parameter.getParameterAnnotations();
        for (Annotation toSearch : annotationsToSearch) {
            annotation = AnnotationUtils.findAnnotation(toSearch.annotationType(), annotationClass);
            if (annotation != null) {
                return annotation;
            }
        }
        return null;
    }
}
