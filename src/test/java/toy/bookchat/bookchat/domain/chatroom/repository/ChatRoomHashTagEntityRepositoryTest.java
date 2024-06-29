package toy.bookchat.bookchat.domain.chatroom.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import toy.bookchat.bookchat.domain.RepositoryTest;
import toy.bookchat.bookchat.domain.chatroom.ChatRoomEntity;
import toy.bookchat.bookchat.domain.chatroom.ChatRoomHashTagEntity;
import toy.bookchat.bookchat.domain.chatroom.HashTagEntity;

class ChatRoomHashTagEntityRepositoryTest extends RepositoryTest {

    @Autowired
    ChatRoomHashTagRepository chatRoomHashTagRepository;
    @Autowired
    ChatRoomRepository chatRoomRepository;
    @Autowired
    HashTagRepository hashTagRepository;

    @Test
    void 채팅방에대한_해시태그_저장성공() throws Exception {
        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
            .build();
        HashTagEntity hashTagEntity = HashTagEntity.of("test");
        ChatRoomHashTagEntity chatRoomHashTagEntity = ChatRoomHashTagEntity.of(chatRoomEntity, hashTagEntity);

        ChatRoomHashTagEntity findChatRoomHashTagEntity = chatRoomHashTagRepository.save(chatRoomHashTagEntity);

        assertThat(findChatRoomHashTagEntity).isEqualTo(chatRoomHashTagEntity);
    }
}