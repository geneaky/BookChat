package toy.bookchat.bookchat.infrastructure.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.infrastructure.rabbitmq.message.CommonMessage;
import toy.bookchat.bookchat.infrastructure.rabbitmq.message.NotificationMessage;

@Slf4j
@Component
public class MessagePublisher {

  private final String DESTINATION_PREFIX = "/topic/";

  private final SimpMessagingTemplate messagingTemplate;

  public MessagePublisher(SimpMessagingTemplate messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
  }

  public void sendCommonMessage(String roomSid, CommonMessage commonMessage) {
    try {
      messagingTemplate.convertAndSend(DESTINATION_PREFIX + roomSid, commonMessage);
    } catch (Exception e) {
      log.info("채팅방 메시지 릴레이 실패", e);
    }
  }

  public void sendNotificationMessage(String roomSid, NotificationMessage notificationMessage) {
    try {
      messagingTemplate.convertAndSend(DESTINATION_PREFIX + roomSid, notificationMessage);
    } catch (Exception e) {
      log.info("채팅방 공지 메시지 릴레이 실패", e);
    }
  }
}
