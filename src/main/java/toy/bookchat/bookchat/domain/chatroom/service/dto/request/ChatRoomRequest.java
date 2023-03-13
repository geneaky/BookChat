package toy.bookchat.bookchat.domain.chatroom.service.dto.request;

import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomRequest {

    private Optional<Long> postCursorId;
    private Optional<String> roomName;
    private Optional<String> title;
    private Optional<String> isbn;
    private List<String> tags;

    @Builder
    private ChatRoomRequest(Optional<Long> postCursorId, Optional<String> roomName,
        Optional<String> title, Optional<String> isbn, List<String> tags) {
        this.postCursorId = postCursorId;
        this.roomName = roomName;
        this.title = title;
        this.isbn = isbn;
        this.tags = tags;
    }

    public void validate() {
        if (this.roomName.isEmpty() && this.title.isEmpty() && this.isbn.isEmpty()
            && this.tags.isEmpty()) {
            throw new IllegalArgumentException("At Least Require One NotNull Parameter");
        }
    }
}
