package toy.bookchat.bookchat.domain.chatroom.service.dto.request;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomRequest {

    private Long postCursorId;
    private String roomName;
    private String title;
    private String isbn;
    private List<String> tags;

    @Builder
    private ChatRoomRequest(Long postCursorId, String roomName, String title, String isbn,
        List<String> tags) {
        this.postCursorId = postCursorId;
        this.roomName = roomName;
        this.title = title;
        this.isbn = isbn;
        this.tags = tags;
    }

    public void validate() {
        if (roomNamePresent()) {
            this.title = null;
            this.isbn = null;
            this.tags = new ArrayList<>();
            return;
        }

        if (titlePresent()) {
            this.roomName = null;
            this.isbn = null;
            this.tags = new ArrayList<>();
            return;
        }

        if (isbnPresent()) {
            this.roomName = null;
            this.title = null;
            this.tags = new ArrayList<>();
            return;
        }

        if (!tags.isEmpty()) {
            this.roomName = null;
            this.title = null;
            this.isbn = null;
            return;
        }

        throw new IllegalArgumentException("Require Just One NotNull Parameter");
    }

    private boolean roomNamePresent() {
        return this.roomName != null;
    }

    private boolean titlePresent() {
        return this.title != null;
    }

    private boolean isbnPresent() {
        return this.isbn != null;
    }
}
