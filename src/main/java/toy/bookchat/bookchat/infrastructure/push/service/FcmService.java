package toy.bookchat.bookchat.infrastructure.push.service;

import static com.google.firebase.ErrorCode.NOT_FOUND;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import toy.bookchat.bookchat.exception.serviceunavailable.push.PushServiceCallException;
import toy.bookchat.bookchat.infrastructure.push.PushMessageBody;

@Service
public class FcmService implements PushService {

    private final String BOOK_CHAT = "Book Chat";
    private final FirebaseMessaging firebaseMessaging;
    private final ObjectMapper objectMapper;

    public FcmService(FirebaseMessaging firebaseMessaging, ObjectMapper objectMapper) {
        this.firebaseMessaging = firebaseMessaging;
        this.objectMapper = objectMapper;
    }

    @Async
    @Override
    public void send(String fcmToken, PushMessageBody messageBody) {
        try {
            Notification notification = Notification.builder()
                .setTitle(BOOK_CHAT)
                .setBody(objectMapper.writeValueAsString(messageBody))
                .build();

            Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(notification)
                .build();

            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            if (e.getErrorCode() == NOT_FOUND) {
                return;
            }
            throw new PushServiceCallException();
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
