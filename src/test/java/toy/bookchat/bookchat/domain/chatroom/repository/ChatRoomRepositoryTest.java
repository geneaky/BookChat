package toy.bookchat.bookchat.domain.chatroom.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import toy.bookchat.bookchat.domain.RepositoryTest;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;

@RepositoryTest
class ChatRoomRepositoryTest {

    @Autowired
    ChatRoomRepository chatRoomRepository;

    @Test
    void 채팅방_저장_성공() throws Exception {
        ChatRoom chatRoom = ChatRoom.builder()
            .roomName("test room")
            .roomSize(5)
            .roomSid("test sid")
            .build();

        ChatRoom findChatRoom = chatRoomRepository.save(chatRoom);

        assertThat(findChatRoom).isEqualTo(chatRoom);
    }
}