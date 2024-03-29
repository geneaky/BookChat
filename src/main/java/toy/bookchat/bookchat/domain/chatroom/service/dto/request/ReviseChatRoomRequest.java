package toy.bookchat.bookchat.domain.chatroom.service.dto.request;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.HashTag;
import toy.bookchat.bookchat.exception.badrequest.chatroom.NotEnoughRoomSizeException;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviseChatRoomRequest {

    @NotNull
    private Long roomId;
    private String roomName;
    private Integer roomSize;
    private List<String> tags;

    @Builder
    private ReviseChatRoomRequest(Long roomId, String roomName, Integer roomSize,
        List<String> tags) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomSize = roomSize;
        this.tags = tags;
    }

    public void reviseChatRoom(ChatRoom chatRoom) {
        if (StringUtils.hasText(this.roomName)) {
            chatRoom.changeRoomName(this.roomName);
        }
        if (roomSize >= chatRoom.getRoomSize()) {
            chatRoom.changeRoomSize(this.roomSize);
        } else {
            throw new NotEnoughRoomSizeException();
        }
    }

    public boolean tagExistent() {
        return this.tags != null && !this.tags.isEmpty();
    }

    public List<HashTag> createHashTag() {
        return this.tags.stream().map(HashTag::of).collect(Collectors.toList());
    }
}
