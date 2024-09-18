package toy.bookchat.bookchat.infrastructure.broker;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.infrastructure.broker.message.CommonMessage;
import toy.bookchat.bookchat.infrastructure.broker.message.NotificationMessage;

@Component
public class MessagePublisher {

    private final String DESTINATION_PREFIX = "/topic/";

    private final SimpMessagingTemplate messagingTemplate;

    public MessagePublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendCommonMessage(String roomSid, CommonMessage commonMessage) {
        messagingTemplate.convertAndSend(DESTINATION_PREFIX + roomSid, commonMessage);
    }

    public void sendNotificationMessage(String roomSid, NotificationMessage notificationMessage) {
        messagingTemplate.convertAndSend(DESTINATION_PREFIX + roomSid, notificationMessage);
    }
}
