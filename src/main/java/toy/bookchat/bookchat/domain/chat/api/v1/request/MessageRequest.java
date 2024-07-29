package toy.bookchat.bookchat.domain.chat.api.v1.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.chat.Message;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageRequest {

    @NotNull
    private Integer receiptId;
    @NotBlank
    private String message;

    @Builder
    private MessageRequest(Integer receiptId, String message) {
        this.receiptId = receiptId;
        this.message = message;
    }

    public Message toTarget() {
        return Message.of(receiptId, message);
    }
}
