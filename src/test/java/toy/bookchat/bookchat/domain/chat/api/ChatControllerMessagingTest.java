package toy.bookchat.bookchat.domain.chat.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static toy.bookchat.bookchat.exception.ExceptionResponse.BAD_REQUEST;

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
import toy.bookchat.bookchat.domain.StompTestExtension;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chat.api.dto.ChatDto;
import toy.bookchat.bookchat.domain.chat.repository.ChatRepository;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ChatControllerMessagingTest extends StompTestExtension {

    private final WebSocketStompClient webSocketStompClient;
    private final BlockingQueue<ChatDto> blockingQueue = new LinkedBlockingQueue<>();
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
    void 채팅방_입장_메시지_수신_성공() throws Exception {
        StompHeaders subscribeHeader = stompSubscribeHeaders("/topic/heho");
        StompHeaders enterHeader = stompSendHeaders("/subscriptions/enter/chatrooms/heho");

        ChatRoom chatRoom = ChatRoom.builder()
            .id(1L)
            .build();

        Chat chat = Chat.builder()
            .id(1L)
            .message(getUserNickname() + "님이 입장하셨습니다.")
            .userIdForeignKey(getUserId())
            .chatRoomIdForeignKey(chatRoom.getId())
            .build();
        chat.setCreatedAt(LocalDateTime.now());

        ChatDto dto = ChatDto.builder()
            .senderId(getUserId())
            .senderNickname(getUserNickname())
            .senderProfileImageUrl(getUserProfileImageUrl())
            .senderDefaultProfileImageType(getUserDefaultProfileImageType())
            .chatId(chat.getId())
            .dispatchTime(chat.getDispatchTime())
            .message(chat.getMessage())
            .build();

        Runnable[] chatActions = {
            () -> this.stompSession.send(enterHeader, null),
        };

        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(getUser()));
        when(chatRoomRepository.findByRoomSid(any())).thenReturn(Optional.of(chatRoom));
        when(chatRepository.save(any())).thenReturn(chat);

        CountDownLatch chatAttemptCountLatch = new CountDownLatch(chatActions.length);
        this.stompSession.subscribe(subscribeHeader,
                subscribeFrameSessionHandler(chatAttemptCountLatch))
            .addReceiptTask(() -> doChat(chatActions));
        chatAttemptCountLatch.await();

        assertThat(blockingQueue).containsExactlyInAnyOrder(dto);
        verify(chatRepository).save(any());
        verify(participantRepository).insertParticipantNativeQuery(any(), any());
    }

    @Test
    void 서로다른_세션_메시지_송신_수신_성공() throws Exception {
        StompHeaders subscribeHeader = stompSubscribeHeaders("/topic/heho");
        StompHeaders sendHeader = stompSendHeaders("/subscriptions/send/chatrooms/heho");

        ChatRoom chatRoom = ChatRoom.builder()
            .id(1L)
            .build();

        Chat chat1 = Chat.builder()
            .id(1L)
            .message("test")
            .userIdForeignKey(getUserId())
            .chatRoomIdForeignKey(chatRoom.getId())
            .build();
        chat1.setCreatedAt(LocalDateTime.now());

        Chat chat2 = Chat.builder()
            .id(2L)
            .message("test test")
            .userIdForeignKey(getUserId())
            .chatRoomIdForeignKey(chatRoom.getId())
            .build();
        chat2.setCreatedAt(LocalDateTime.now());

        Chat chat3 = Chat.builder()
            .id(3L)
            .message("test test test")
            .userIdForeignKey(getUserId())
            .chatRoomIdForeignKey(chatRoom.getId())
            .build();
        chat3.setCreatedAt(LocalDateTime.now());

        Participant participant = Participant.builder()
            .id(1L)
            .build();

        ChatDto dto1 = ChatDto.builder()
            .senderId(getUserId())
            .senderNickname(getUserNickname())
            .senderProfileImageUrl(getUserProfileImageUrl())
            .senderDefaultProfileImageType(getUserDefaultProfileImageType())
            .chatId(chat1.getId())
            .dispatchTime(chat1.getDispatchTime())
            .message(chat1.getMessage())
            .build();

        ChatDto dto2 = ChatDto.builder()
            .senderId(getUserId())
            .senderNickname(getUserNickname())
            .senderProfileImageUrl(getUserProfileImageUrl())
            .senderDefaultProfileImageType(getUserDefaultProfileImageType())
            .chatId(chat2.getId())
            .dispatchTime(chat2.getDispatchTime())
            .message(chat2.getMessage())
            .build();

        ChatDto dto3 = ChatDto.builder()
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

        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(getUser()));
        when(chatRoomRepository.findByRoomSid(any())).thenReturn(Optional.of(chatRoom));
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
    void 채팅방_퇴장_메시지_수신_성공() throws Exception {
        StompHeaders subscribeHeader = stompSubscribeHeaders("/topic/heho");
        StompHeaders leaveHeader = stompSendHeaders("/subscriptions/leave/chatrooms/heho");

        ChatRoom chatRoom = ChatRoom.builder()
            .id(1L)
            .build();

        Chat chat = Chat.builder()
            .id(1L)
            .message(getUserNickname() + "님이 퇴장하셨습니다.")
            .userIdForeignKey(getUserId())
            .chatRoomIdForeignKey(chatRoom.getId())
            .build();
        chat.setCreatedAt(LocalDateTime.now());

        Participant participant = Participant.builder()
            .id(1L)
            .build();

        ChatDto dto = ChatDto.builder()
            .senderId(getUserId())
            .senderNickname(getUserNickname())
            .senderProfileImageUrl(getUserProfileImageUrl())
            .senderDefaultProfileImageType(getUserDefaultProfileImageType())
            .chatId(chat.getId())
            .dispatchTime(chat.getDispatchTime())
            .message(chat.getMessage())
            .build();

        Runnable[] chatActions = {
            () -> this.stompSession.send(leaveHeader, null)
        };

        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(getUser()));
        when(chatRoomRepository.findByRoomSid(any())).thenReturn(Optional.of(chatRoom));
        when(participantRepository.findByUserIdAndChatRoomId(any(), any())).thenReturn(
            Optional.ofNullable(participant));
        when(chatRepository.save(any())).thenReturn(chat);

        CountDownLatch chatAttemptCountLatch = new CountDownLatch(chatActions.length);
        this.stompSession.subscribe(subscribeHeader,
                subscribeFrameSessionHandler(chatAttemptCountLatch))
            .addReceiptTask(() -> doChat(chatActions));
        chatAttemptCountLatch.await();

        assertThat(blockingQueue).containsExactlyInAnyOrder(dto);
        verify(participantRepository).deleteById(any());
        verify(chatRepository).save(any());
    }

    @Test
    void 메시지_예외_테스트() throws Exception {
        StompHeaders subscribeHeader = stompSubscribeHeaders("/topic/heho");
        StompHeaders subscribeError = stompSubscribeHeaders("/user/exchange/amq.direct/error");
        StompHeaders enterHeader = stompSendHeaders("/subscriptions/enter/chatrooms/heho");

        ChatDto dto = ChatDto.builder()
            .message(BAD_REQUEST.getValue().toString())
            .build();

        Runnable[] chatActions = {
            () -> this.stompSession.send(enterHeader, null),
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