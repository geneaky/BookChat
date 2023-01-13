package toy.bookchat.bookchat.domain.chat.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
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
@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ChatControllerTest extends ControllerTestExtension {

    @LocalServerPort
    private int port;
    private StompSession stompSession;
    private final WebSocketClient webSocketClient;
    private final WebSocketStompClient webSocketStompClient;
    private final BlockingQueue<ChatDto> blockingQueue = new LinkedBlockingQueue<>();

    ChatControllerTest() throws Exception {
        this.webSocketClient = new StandardWebSocketClient();
        this.webSocketStompClient = new WebSocketStompClient(this.webSocketClient);
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.afterPropertiesSet();
        this.webSocketStompClient.setTaskScheduler(taskScheduler);
        this.webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    private String getStompConnectionEndPointUrl() {
        return "ws://localhost:" + port + "/stomp-connection";
    }

    private StompSessionHandlerAdapter subscribeFrameSessionHandler(CountDownLatch latch) {
        return new StompSessionHandlerAdapter() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ChatDto.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                ChatDto chatDto = (ChatDto) payload;
                blockingQueue.offer(chatDto);
                latch.countDown();
            }
        };
    }

    private StompSession stompSession()
        throws InterruptedException, ExecutionException, TimeoutException {
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.set(AUTHORIZATION, getTestToken());
        ListenableFuture<StompSession> connect = this.webSocketStompClient.connect(
            getStompConnectionEndPointUrl(), new WebSocketHttpHeaders(), stompHeaders,
            new StompSessionHandlerAdapter() {
            });
        StompSession stompSession = connect.get(60, TimeUnit.SECONDS);
        stompSession.setAutoReceipt(true);
        return stompSession;
    }

    private StompHeaders stompSubscribeHeaders(String destination) {
        StompHeaders subscribeHeader = new StompHeaders();
        subscribeHeader.set(AUTHORIZATION, getTestToken());
        subscribeHeader.setDestination(destination);
        return subscribeHeader;
    }

    private StompHeaders stompSendHeaders(String destination) {
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.set(AUTHORIZATION, getTestToken());
        stompHeaders.setDestination(destination);
        return stompHeaders;
    }

    @BeforeEach
    public void initNewSession() throws Exception {
        this.stompSession = stompSession();
        this.blockingQueue.clear();
    }

    @Test
    void 채팅방_입장_송신_퇴장_메시지_수신_성공() throws Exception {

        StompHeaders subscribeHeader = stompSubscribeHeaders("/topic/heho");
        StompHeaders enterHeader = stompSendHeaders("/subscriptions/enter/chatrooms/heho");
        StompHeaders sendHeader = stompSendHeaders("/subscriptions/send/chatrooms/heho");
        StompHeaders leaveHeader = stompSendHeaders("/subscriptions/leave/chatrooms/heho");
//        StompHeaders subscribeHeader2 = stompSubscribeHeaders("/topic/heho.#"); //채팅방은 user specific한 큐가 아니라 room_name.#으로 routing key를 지정

        ChatDto dto1 = ChatDto.builder()
            .message(getUser().getNickname() + "님이 입장하셨습니다.")
            .build();

        ChatDto dto2 = ChatDto.builder()
            .message("test test test")
            .build();

        ChatDto dto3 = ChatDto.builder()
            .message(getUser().getNickname() + "님이 퇴장하셨습니다.")
            .build();

        Runnable[] chatActions = {
            () -> this.stompSession.send(enterHeader, null),
            () -> this.stompSession.send(sendHeader, dto2),
            () -> this.stompSession.send(leaveHeader, null)
        };

        CountDownLatch chatAttemptCountLatch = new CountDownLatch(chatActions.length);
        this.stompSession.subscribe(subscribeHeader,
                subscribeFrameSessionHandler(chatAttemptCountLatch))
            .addReceiptTask(() -> doChat(chatActions));
        chatAttemptCountLatch.await();

        assertThat(blockingQueue).containsExactlyInAnyOrder(dto1, dto2, dto3);
    }

    private void doChat(Runnable... chatActions) {
        for (Runnable chatAction : chatActions) {
            chatAction.run();
        }
    }
}