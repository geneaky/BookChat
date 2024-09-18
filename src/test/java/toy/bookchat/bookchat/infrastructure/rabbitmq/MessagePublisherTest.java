package toy.bookchat.bookchat.infrastructure.rabbitmq;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import toy.bookchat.bookchat.infrastructure.rabbitmq.message.CommonMessage;
import toy.bookchat.bookchat.infrastructure.rabbitmq.message.NotificationMessage;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MessagePublisherTest {

  @Mock
  private SimpMessagingTemplate messagingTemplate;

  @InjectMocks
  private MessagePublisher messagePublisher;

  @Test
  void 일반_메시지_전송_성공() throws Exception {
    CommonMessage commonMessage = CommonMessage.builder().build();
    messagePublisher.sendCommonMessage("eDISVS1", commonMessage);

    verify(messagingTemplate).convertAndSend(anyString(), any(CommonMessage.class));
  }

  @Test
  void 공지_메시지_전송_성공() throws Exception {
    NotificationMessage notificationMessage = NotificationMessage.builder().build();
    messagePublisher.sendNotificationMessage("Zbg2pw5W", notificationMessage);

    verify(messagingTemplate).convertAndSend(anyString(), any(NotificationMessage.class));
  }
}