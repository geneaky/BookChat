package toy.bookchat.bookchat.domain.chat.api;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

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
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import toy.bookchat.bookchat.domain.ControllerTestExtension;
import toy.bookchat.bookchat.domain.chat.api.dto.ChatDto;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ChatControllerTest extends ControllerTestExtension {

    @LocalServerPort
    private int port;
    private StompSession stompSession;
    private final WebSocketClient webSocketClient;
    private final WebSocketStompClient webSocketStompClient;

    ChatControllerTest() throws Exception {
        this.webSocketClient = new StandardWebSocketClient();
        this.webSocketStompClient = new WebSocketStompClient(this.webSocketClient);
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.afterPropertiesSet();
        this.webSocketStompClient.setTaskScheduler(taskScheduler);
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

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                log.info("handle frame");
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

    private StompSession stompSession() throws Exception {
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.set(AUTHORIZATION, getTestToken());
        ListenableFuture<StompSession> connect = this.webSocketStompClient.connect(
            getStompConnectionEndPointUrl(), new WebSocketHttpHeaders(), stompHeaders,
            getStompSessionHandlerAdapter());

        return connect.get(30, TimeUnit.SECONDS);
    }

    @Test
    void STOMP_메시지_전송() throws Exception {
        StompHeaders sendHeader = new StompHeaders();
        sendHeader.set(AUTHORIZATION, getTestToken());
        sendHeader.setDestination("/pub/chat.enter.heho");

        ChatDto dto = ChatDto.builder()
            .message("test test test")
            .build();

//        this.stompSession.subscribe("/pub/topic.chatrooms.heho", getStompSessionHandlerAdapter());
        StompSession user1 = stompSession();
        user1.setAutoReceipt(true);
        user1.subscribe("/topic/heho", getStompSessionHandlerAdapter()).addReceiptTask(() -> {
            log.info("메시지 전달 받음");
        });

        this.stompSession.send(sendHeader, dto);
        Thread.sleep(3000);
        this.stompSession.disconnect();
    }
}