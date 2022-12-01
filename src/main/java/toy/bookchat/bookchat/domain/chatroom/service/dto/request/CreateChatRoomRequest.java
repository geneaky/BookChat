package toy.bookchat.bookchat.domain.chatroom.service.dto.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateChatRoomRequest {

    @NotBlank
    private String roomName;
    @Min(2)
    private Integer roomSize;
    @NotNull
    private Long bookId;

    @Builder
    private CreateChatRoomRequest(String roomName, Integer roomSize, Long bookId) {
        this.roomName = roomName;
        this.roomSize = roomSize;
        this.bookId = bookId;
    }
}
