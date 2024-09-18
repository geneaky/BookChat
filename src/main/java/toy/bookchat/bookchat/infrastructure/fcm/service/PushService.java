package toy.bookchat.bookchat.infrastructure.fcm.service;

import toy.bookchat.bookchat.infrastructure.fcm.PushMessageBody;

public interface PushService {

  void send(String fcmToken, PushMessageBody pushMessageBody);
}
