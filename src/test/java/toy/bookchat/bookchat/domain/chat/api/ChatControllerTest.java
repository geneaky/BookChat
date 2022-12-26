package toy.bookchat.bookchat.domain.chat.api;

import java.util.concurrent.TimeUnit;
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

@Testcontainers
@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ChatControllerTest extends ControllerTestExtension {

    @Container
    static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer()
        .withPluginsEnabled("rabbitmq_stomp")
        .withUser("guest", "guest");
//        .waitingFor(Wait.forHttp("/"));

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
        webSocketHttpHeaders.setBearerAuth(getTestToken());
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.set("Authorization", "Bearer " + getTestToken());
        ListenableFuture<StompSession> connect = this.webSocketStompClient.connect(
            "ws://localhost:" + port + "/stomp-connection", webSocketHttpHeaders, stompHeaders,
            new StompSessionHandlerAdapter() {
                @Override
                public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                    System.out.println("=======connected========");
                }
            });

        SuccessCallback<StompSession> successCallback = (result) -> {
            System.out.println("Success Callback +==============");
            System.out.println(result);
        };
        FailureCallback failureCallback = (result) -> {
            System.out.println(result.getMessage());
        };
        connect.addCallback(successCallback, failureCallback);
        this.stompSession = connect.get(30, TimeUnit.SECONDS);
        Thread.sleep(10000);
    }
}