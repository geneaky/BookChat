package toy.bookchat.bookchat.domain.chatroomhost.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import toy.bookchat.bookchat.domain.RepositoryTest;
import toy.bookchat.bookchat.domain.chatroomhost.ChatRoomHost;

@RepositoryTest
class ChatRoomHostRepositoryTest {

    @Autowired
    ChatRoomHostRepository chatRoomHostRepository;

    @Test
    void 채팅방_방장_등록_성공() throws Exception {
        ChatRoomHost chatRoomHost = ChatRoomHost.builder().build();

        ChatRoomHost findChatRoomHost = chatRoomHostRepository.save(chatRoomHost);

        assertThat(findChatRoomHost).isEqualTo(chatRoomHost);
    }
}