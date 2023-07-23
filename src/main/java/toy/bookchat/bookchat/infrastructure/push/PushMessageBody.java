package toy.bookchat.bookchat.infrastructure.push;

import lombok.Getter;

@Getter
public class PushMessageBody {

    private PushType pushType;
    private Object body;
    private Integer order;

    private PushMessageBody(PushType pushType, Object body) {
        this.pushType = pushType;
        this.body = body;
    }

    private PushMessageBody(PushType pushType, Object body, Integer order) {
        this.pushType = pushType;
        this.body = body;
        this.order = order;
    }

    public static PushMessageBody of(PushType pushType, Object body) {
        return new PushMessageBody(pushType, body);
    }

    public static PushMessageBody of(PushType pushType, Object body, Integer order) {
        return new PushMessageBody(pushType, body, order);
    }
}
