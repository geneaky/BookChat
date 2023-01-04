package toy.bookchat.bookchat.domain.chat.api;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import toy.bookchat.bookchat.domain.ControllerTestExtension;
import toy.bookchat.bookchat.domain.chat.api.dto.ChatDto;

@Slf4j
@Testcontainers
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
    }

    @NotNull
    private String getStompConnectionEndPointUrl() {
        return "ws://localhost:" + port + "/stomp-connection";
    }

    @NotNull
    private StompSessionHandlerAdapter getStompSessionHandlerAdapter() {
        return new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                log.info("=======connected========");
            }
        };
    }

    @BeforeEach
    public void connect() throws Exception {
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.set(AUTHORIZATION, getTestToken());
        ListenableFuture<StompSession> connect = this.webSocketStompClient.connect(
            getStompConnectionEndPointUrl(), new WebSocketHttpHeaders(), stompHeaders,
            getStompSessionHandlerAdapter());
        this.stompSession = connect.get(30, TimeUnit.SECONDS);
    }

    @Test
    void STOMP_메시지_전송() throws Exception {
        StompHeaders sendHeader = new StompHeaders();
        sendHeader.set(AUTHORIZATION, getTestToken());
        sendHeader.setDestination("/pub/chat.enter.heho");

        ChatDto dto = ChatDto.builder()
            .message("test test test")
            .build();

        this.stompSession.send(sendHeader, dto);
        Thread.sleep(1000);
        this.stompSession.disconnect();
    }
}