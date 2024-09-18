package toy.bookchat.bookchat.infrastructure.push.service;

import toy.bookchat.bookchat.infrastructure.push.PushMessageBody;

public interface PushService {

    void send(String fcmToken, PushMessageBody pushMessageBody);
}
