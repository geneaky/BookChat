package toy.bookchat.bookchat.domain.chatroomhashtag.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import toy.bookchat.bookchat.domain.RepositoryTest;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.domain.chatroomhashtag.ChatRoomHashTag;
import toy.bookchat.bookchat.domain.hashtag.HashTag;
import toy.bookchat.bookchat.domain.hashtag.repository.HashTagRepository;

@RepositoryTest
class ChatRoomHashTagRepositoryTest {

    @Autowired
    ChatRoomHashTagRepository chatRoomHashTagRepository;
    @Autowired
    ChatRoomRepository chatRoomRepository;
    @Autowired
    HashTagRepository hashTagRepository;

    @Test
    void 채팅방에대한_해시태그_저장성공() throws Exception {
        ChatRoom chatRoom = ChatRoom.builder()
            .build();
        HashTag hashTag = HashTag.of("test");
        ChatRoomHashTag chatRoomHashTag = ChatRoomHashTag.of(chatRoom, hashTag);

        ChatRoomHashTag findChatRoomHashTag = chatRoomHashTagRepository.save(chatRoomHashTag);

        assertThat(findChatRoomHashTag).isEqualTo(chatRoomHashTag);
    }
}