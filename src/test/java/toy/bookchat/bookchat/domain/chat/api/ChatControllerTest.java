package toy.bookchat.bookchat.domain.chat.api;

import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import toy.bookchat.bookchat.domain.ControllerTestExtension;

@Slf4j
@Testcontainers
@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ChatControllerTest extends ControllerTestExtension {

    @Container
    static RabbitMQContainer rabbitMQContainer;

    static {
        rabbitMQContainer = new RabbitMQContainer(
            "rabbitmq:3.11-management")
            .withPluginsEnabled("rabbitmq_stomp", "rabbitmq_web_stomp")
            .withUser("guest", "guest");
        rabbitMQContainer.setPortBindings(List.of("5672:5672", "15672:15672", "61613:61613"));
    }

    @LocalServerPort
    private int port;
    private StompSession stompSession;
    private final WebSocketClient webSocketClient;
    private final WebSocketStompClient webSocketStompClient;

    ChatControllerTest() throws Exception {
        this.webSocketClient = new StandardWebSocketClient();
        this.webSocketStompClient = new WebSocketStompClient(this.webSocketClient);
        this.webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());
        ;
    }

    @Test
    void 웹소켓_연결성공() throws Exception {
        WebSocketHttpHeaders webSocketHttpHeaders = new WebSocketHttpHeaders();
        webSocketHttpHeaders.setBearerAuth(
            getTestToken()); //최초 websocket connection을 맺기위한 요청은 http이므로 토큰을 달아준다.
        StompHeaders stompHeaders = new StompHeaders(); // stomp protocol로 통신시에는 메시지로 요청을 주고 받으므로 stomp header spec에 토큰을 달아주고 interceptor에서 검증 후 security context에 넣어줌.
        stompHeaders.set("Authorization", "Bearer " + getTestToken());
        ListenableFuture<StompSession> connect = this.webSocketStompClient.connect(
            "ws://localhost:" + port + "/stomp-connection", webSocketHttpHeaders, stompHeaders,
            new StompSessionHandlerAdapter() {
                @Override
                public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                    log.info("=======connected========");
                }
            });

        SuccessCallback<StompSession> successCallback = (result) -> {
            log.info("Success Callback +==============");
            log.info("success result::{}", result);
        };
        FailureCallback failureCallback = (result) -> {
            log.info("failure result :: {}", result);
        };
        connect.addCallback(successCallback, failureCallback);
        this.stompSession = connect.get(30, TimeUnit.SECONDS);
        this.stompSession.disconnect();
    }
}