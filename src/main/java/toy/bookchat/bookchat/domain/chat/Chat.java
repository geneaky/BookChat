package toy.bookchat.bookchat.domain.chat;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Chat {

    private Long id;
    private Long chatRoomId;
    private Sender sender;
    private String message;
    private LocalDateTime dispatchTime;

    @Builder
    private Chat(Long id, Long chatRoomId, Sender sender, String message, LocalDateTime dispatchTime) {
        this.id = id;
        this.chatRoomId = chatRoomId;
        this.sender = sender;
        this.message = message;
        this.dispatchTime = dispatchTime;
    }

    public Boolean isAnnouncement() {
        return this.sender == null;
    }

    public Long getSenderId() {
        return this.sender.getId();
    }
}
