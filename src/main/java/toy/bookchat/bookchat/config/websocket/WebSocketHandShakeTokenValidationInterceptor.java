package toy.bookchat.bookchat.config.websocket;

import java.util.Map;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Component
public class WebSocketHandShakeTokenValidationInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
        WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        System.out.println("before handshake ++++++===+++++++++++=");
        System.out.println(request.getHeaders().entrySet());
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
        WebSocketHandler wsHandler, Exception exception) {
        System.out.println("after handshake ++++++=======+++++++++");
        System.out.println(request.getHeaders().entrySet());
    }
}
