package toy.bookchat.bookchat.domain.chat.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

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
import toy.bookchat.bookchat.db_module.chat.repository.ChatRepository;
import toy.bookchat.bookchat.db_module.participant.ParticipantEntity;
import toy.bookchat.bookchat.db_module.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.ControllerTestExtension;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chat.Sender;
import toy.bookchat.bookchat.domain.chat.service.ChatAppender;
import toy.bookchat.bookchat.domain.chat.service.ChatReader;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.service.ChatRoomReader;
import toy.bookchat.bookchat.domain.participant.service.ParticipantValidator;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.service.UserReader;
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
    private PushService pushService;
    @MockBean
    private ParticipantRepository participantRepository;
    @MockBean
    private ChatRepository chatRepository;
    @MockBean
    private ChatReader chatReader;
    @MockBean
    private ChatAppender chatAppender;
    @MockBean
    private ChatRoomReader chatRoomReader;
    @MockBean
    private ParticipantValidator participantValidator;
    @MockBean
    private UserReader userReader;

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

        User user = User.builder()
            .id(1L)
            .build();
        given(userReader.readUser(any())).willReturn(user);

        ChatRoom chatRoom = ChatRoom.builder()
            .id(1L)
            .roomSize(10)
            .sid("heho")
            .build();
        given(chatRoomReader.readChatRoom(any())).willReturn(chatRoom);

        Sender sender = Sender.builder()
            .id(user.getId())
            .build();
        Chat chat1 = Chat.builder()
            .id(1L)
            .sender(sender)
            .message("test")
            .chatRoomId(chatRoom.getId())
            .dispatchTime(LocalDateTime.now())
            .build();
        Chat chat2 = Chat.builder()
            .id(2L)
            .sender(sender)
            .message("test test")
            .chatRoomId(chatRoom.getId())
            .dispatchTime(LocalDateTime.now())
            .build();
        Chat chat3 = Chat.builder()
            .id(3L)
            .sender(sender)
            .message("test test test")
            .chatRoomId(chatRoom.getId())
            .dispatchTime(LocalDateTime.now())
            .build();
        given(chatAppender.append(any(), any(), any())).willReturn(chat1, chat2, chat3);

        ParticipantEntity participantEntity = ParticipantEntity.builder()
            .chatRoomId(chatRoom.getId())
            .userId(getUserId())
            .id(1L)
            .build();
        when(participantRepository.findByUserIdAndChatRoomId(any(), any())).thenReturn(
            Optional.of(participantEntity));
        given(participantRepository.findByUserIdAndChatRoomSid(any(), any())).willReturn(Optional.of(participantEntity));

        CommonMessage dto1 = CommonMessage.builder()
            .senderId(getUserId())
            .chatId(chat1.getId())
            .receiptId(1)
            .dispatchTime(chat1.getDispatchTime().toString())
            .message(chat1.getMessage())
            .build();

        CommonMessage dto2 = CommonMessage.builder()
            .senderId(getUserId())
            .chatId(chat2.getId())
            .receiptId(2)
            .dispatchTime(chat2.getDispatchTime().toString())
            .message(chat2.getMessage())
            .build();

        CommonMessage dto3 = CommonMessage.builder()
            .senderId(getUserId())
            .chatId(chat3.getId())
            .receiptId(3)
            .dispatchTime(chat3.getDispatchTime().toString())
            .message(chat3.getMessage())
            .build();

        Runnable[] chatActions = {
            () -> this.stompSession.send(sendHeader, dto1),
            () -> this.stompSession.send(sendHeader, dto2),
            () -> this.stompSession.send(sendHeader, dto3)
        };

        CountDownLatch chatAttemptCountLatch = new CountDownLatch(chatActions.length);

        StompSession stompSession2 = stompSession();
        stompSession2.subscribe(subscribeHeader,
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