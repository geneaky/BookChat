package toy.bookchat.bookchat.domain.chat.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.google.firebase.messaging.FirebaseMessaging;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import toy.bookchat.bookchat.domain.ControllerTestExtension;
import toy.bookchat.bookchat.domain.chat.ChatEntity;
import toy.bookchat.bookchat.domain.chat.repository.ChatRepository;
import toy.bookchat.bookchat.domain.chatroom.ChatRoomEntity;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.domain.device.repository.DeviceRepository;
import toy.bookchat.bookchat.domain.participant.ParticipantEntity;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.infrastructure.broker.message.CommonMessage;
import toy.bookchat.bookchat.infrastructure.push.service.PushService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ChatControllerMessagingTest extends ControllerTestExtension {

    private final WebSocketStompClient webSocketStompClient;
    private final BlockingQueue<CommonMessage> blockingQueue = new LinkedBlockingQueue<>();
    @LocalServerPort
    private int port;
    private StompSession stompSession;
    @MockBean
    private DeviceRepository deviceRepository;
    @MockBean
    private PushService pushService;
    @MockBean
    private FirebaseMessaging firebaseMessaging;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ChatRoomRepository chatRoomRepository;
    @MockBean
    private ParticipantRepository participantRepository;
    @MockBean
    private ChatRepository chatRepository;

    ChatControllerMessagingTest() throws Exception {
        this.webSocketStompClient = new WebSocketStompClient(new StandardWebSocketClient());
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.afterPropertiesSet();
        this.webSocketStompClient.setTaskScheduler(taskScheduler);
        this.webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());
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

    private String getStompConnectionEndPointUrl() {
        return "ws://localhost:" + port + "/stomp-connection";
    }

    private StompSessionHandlerAdapter subscribeFrameSessionHandler(CountDownLatch latch) {
        return new StompSessionHandlerAdapter() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return CommonMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                CommonMessage commonMessage = (CommonMessage) payload;
                blockingQueue.offer(commonMessage);
                latch.countDown();
            }
        };
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
    void 서로다른_세션_메시지_송신_수신_성공() throws Exception {
        StompHeaders subscribeHeader = stompSubscribeHeaders("/topic/heho");
        StompHeaders sendHeader = stompSendHeaders("/subscriptions/send/chatrooms/1");

        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
            .id(1L)
            .roomSize(3)
            .roomSid("heho")
            .build();

        ChatEntity chatEntity1 = ChatEntity.builder()
            .id(1L)
            .message("test")
            .userEntity(getUser())
            .chatRoomEntity(chatRoomEntity)
            .build();
        chatEntity1.setCreatedAt(LocalDateTime.now());

        ChatEntity chatEntity2 = ChatEntity.builder()
            .id(2L)
            .message("test test")
            .userEntity(getUser())
            .chatRoomEntity(chatRoomEntity)
            .build();
        chatEntity2.setCreatedAt(LocalDateTime.now());

        ChatEntity chatEntity3 = ChatEntity.builder()
            .id(3L)
            .message("test test test")
            .userEntity(getUser())
            .chatRoomEntity(chatRoomEntity)
            .build();
        chatEntity3.setCreatedAt(LocalDateTime.now());

        ParticipantEntity participantEntity = ParticipantEntity.builder()
            .chatRoomEntity(chatRoomEntity)
            .userEntity(getUser())
            .id(1L)
            .build();

        CommonMessage dto1 = CommonMessage.builder()
            .senderId(getUserId())
            .chatId(chatEntity1.getId())
            .receiptId(1)
            .dispatchTime(chatEntity1.getDispatchTime())
            .message(chatEntity1.getMessage())
            .build();

        CommonMessage dto2 = CommonMessage.builder()
            .senderId(getUserId())
            .chatId(chatEntity2.getId())
            .receiptId(2)
            .dispatchTime(chatEntity2.getDispatchTime())
            .message(chatEntity2.getMessage())
            .build();

        CommonMessage dto3 = CommonMessage.builder()
            .senderId(getUserId())
            .chatId(chatEntity3.getId())
            .receiptId(3)
            .dispatchTime(chatEntity3.getDispatchTime())
            .message(chatEntity3.getMessage())
            .build();

        Runnable[] chatActions = {
            () -> this.stompSession.send(sendHeader, dto1),
            () -> this.stompSession.send(sendHeader, dto2),
            () -> this.stompSession.send(sendHeader, dto3)
        };

        when(participantRepository.findByUserIdAndChatRoomId(any(), any())).thenReturn(
            Optional.ofNullable(participantEntity));
        when(chatRepository.save(any())).thenReturn(chatEntity1, chatEntity2, chatEntity3);

        CountDownLatch chatAttemptCountLatch = new CountDownLatch(chatActions.length);

        StompSession stompSession2 = stompSession();
        stompSession2.subscribe(subscribeHeader,
                subscribeFrameSessionHandler(chatAttemptCountLatch))
            .addReceiptTask(() -> doChat(chatActions));

        chatAttemptCountLatch.await();

        assertThat(blockingQueue).containsExactlyInAnyOrder(dto1, dto2, dto3);
        verify(chatRepository, times(chatActions.length)).save(any());
    }

    private void doChat(Runnable... chatActions) {
        for (Runnable chatAction : chatActions) {
            chatAction.run();
        }
    }
}