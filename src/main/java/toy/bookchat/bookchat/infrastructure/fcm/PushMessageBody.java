package toy.bookchat.bookchat.infrastructure.fcm;

import lombok.Getter;

@Getter
public class PushMessageBody {

  private PushType pushType;
  private Object body;

  private PushMessageBody(PushType pushType, Object body) {
    this.pushType = pushType;
    this.body = body;
  }

  public static PushMessageBody of(PushType pushType, Object body) {
    return new PushMessageBody(pushType, body);
  }
}
