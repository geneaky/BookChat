package toy.bookchat.bookchat.infrastructure.fcm;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class FCMConfig {

  @Bean
  public FirebaseMessaging firebaseMessaging() throws IOException {
    ClassPathResource resource = new ClassPathResource("firebase/bookchat-firebase-private.json");
    InputStream refreshToken = resource.getInputStream();

    FirebaseOptions options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(refreshToken))
        .build();

    if (FirebaseApp.getApps().isEmpty()) {
      return FirebaseMessaging.getInstance(FirebaseApp.initializeApp(options));
    } else {
      return FirebaseMessaging.getInstance(FirebaseApp.getInstance());
    }
  }
}
