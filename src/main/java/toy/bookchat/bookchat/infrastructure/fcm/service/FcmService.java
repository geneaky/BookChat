package toy.bookchat.bookchat.infrastructure.push.service;

import static com.google.firebase.ErrorCode.NOT_FOUND;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidConfig.Priority;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import java.util.HashMap;
import java.util.Map;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
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
    @Retryable(value = PushServiceCallException.class, maxAttempts = 5, backoff = @Backoff(delay = 2000L, multiplier = 2.0))
    @Override
    public void send(String fcmToken, PushMessageBody messageBody) {
        try {
            Map<String, String> data = new HashMap<>();
            data.put("title", BOOK_CHAT);
            data.put("body", objectMapper.writeValueAsString(messageBody));

            Message message = Message.builder()
                .setToken(fcmToken)
                .setAndroidConfig(AndroidConfig.builder()
                    .setDirectBootOk(true)
                    .setPriority(Priority.HIGH)
                    .build())
                .putAllData(data)
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
