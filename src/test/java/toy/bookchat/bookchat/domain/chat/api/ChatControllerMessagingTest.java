package toy.bookchat.bookchat.domain.chat.api;

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
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chat.repository.ChatRepository;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.infrastructure.broker.message.CommonMessage;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static toy.bookchat.bookchat.exception.ExceptionResponse.BAD_REQUEST;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ChatControllerMessagingTest extends ControllerTestExtension {

    private final WebSocketStompClient webSocketStompClient;
    private final BlockingQueue<CommonMessage> blockingQueue = new LinkedBlockingQueue<>();
    @LocalServerPort
    private int port;
    private StompSession stompSession;
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

        ChatRoom chatRoom = ChatRoom.builder()
                .id(1L)
                .roomSize(3)
                .roomSid("heho")
                .build();

        Chat chat1 = Chat.builder()
                .id(1L)
                .message("test")
                .user(getUser())
                .chatRoom(chatRoom)
                .build();
        chat1.setCreatedAt(LocalDateTime.now());

        Chat chat2 = Chat.builder()
                .id(2L)
                .message("test test")
                .user(getUser())
                .chatRoom(chatRoom)
                .build();
        chat2.setCreatedAt(LocalDateTime.now());

        Chat chat3 = Chat.builder()
                .id(3L)
                .message("test test test")
                .user(getUser())
                .chatRoom(chatRoom)
                .build();
        chat3.setCreatedAt(LocalDateTime.now());

        Participant participant = Participant.builder()
                .chatRoom(chatRoom)
                .user(getUser())
                .id(1L)
                .build();

        CommonMessage dto1 = CommonMessage.builder()
                .senderId(getUserId())
                .senderNickname(getUserNickname())
                .senderProfileImageUrl(getUserProfileImageUrl())
                .senderDefaultProfileImageType(getUserDefaultProfileImageType())
                .chatId(chat1.getId())
                .dispatchTime(chat1.getDispatchTime())
                .message(chat1.getMessage())
                .build();

        CommonMessage dto2 = CommonMessage.builder()
                .senderId(getUserId())
                .senderNickname(getUserNickname())
                .senderProfileImageUrl(getUserProfileImageUrl())
                .senderDefaultProfileImageType(getUserDefaultProfileImageType())
                .chatId(chat2.getId())
                .dispatchTime(chat2.getDispatchTime())
                .message(chat2.getMessage())
                .build();

        CommonMessage dto3 = CommonMessage.builder()
                .senderId(getUserId())
                .senderNickname(getUserNickname())
                .senderProfileImageUrl(getUserProfileImageUrl())
                .senderDefaultProfileImageType(getUserDefaultProfileImageType())
                .chatId(chat3.getId())
                .dispatchTime(chat3.getDispatchTime())
                .message(chat3.getMessage())
                .build();

        Runnable[] chatActions = {
                () -> this.stompSession.send(sendHeader, dto1),
                () -> this.stompSession.send(sendHeader, dto2),
                () -> this.stompSession.send(sendHeader, dto3)
        };

        when(participantRepository.findByUserIdAndChatRoomId(any(), any())).thenReturn(
                Optional.ofNullable(participant));
        when(chatRepository.save(any())).thenReturn(chat1, chat2, chat3);

        CountDownLatch chatAttemptCountLatch = new CountDownLatch(chatActions.length);

        StompSession stompSession2 = stompSession();
        stompSession2.subscribe(subscribeHeader,
                        subscribeFrameSessionHandler(chatAttemptCountLatch))
                .addReceiptTask(() -> doChat(chatActions));

        chatAttemptCountLatch.await();

        assertThat(blockingQueue).containsExactlyInAnyOrder(dto1, dto2, dto3);
        verify(chatRepository, times(3)).save(any());
    }

    @Test
    void 메시지_예외_테스트() throws Exception {
        StompHeaders subscribeHeader = stompSubscribeHeaders("/topic/heho");
        StompHeaders subscribeError = stompSubscribeHeaders("/user/exchange/amq.direct/error");
        StompHeaders sendHeader = stompSendHeaders("/subscriptions/send/chatrooms/1");

        CommonMessage dto = CommonMessage.builder()
                .message(BAD_REQUEST.getValue().toString())
                .build();

        Runnable[] chatActions = {
                () -> this.stompSession.send(sendHeader, null),
        };

        CountDownLatch chatAttemptCountLatch = new CountDownLatch(chatActions.length);
        this.stompSession.subscribe(subscribeError,
                subscribeFrameSessionHandler(chatAttemptCountLatch));

        this.stompSession.subscribe(subscribeHeader,
                        subscribeFrameSessionHandler(chatAttemptCountLatch))
                .addReceiptTask(() -> doChat(chatActions));

        chatAttemptCountLatch.await();

        assertThat(blockingQueue).containsExactlyInAnyOrder(dto);
    }

    private void doChat(Runnable... chatActions) {
        for (Runnable chatAction : chatActions) {
            chatAction.run();
        }
    }
}