package toy.bookchat.bookchat.domain.chat.service.cache;

import lombok.Getter;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;

@Getter
public class ChatRoomCache {

    private final Long chatRoomId;

    private ChatRoomCache(Long chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public static ChatRoomCache of(ChatRoom chatRoom) {
        return new ChatRoomCache(chatRoom.getId());
    }
}
