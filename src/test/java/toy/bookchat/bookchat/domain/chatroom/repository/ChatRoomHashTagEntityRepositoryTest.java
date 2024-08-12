package toy.bookchat.bookchat.domain.chatroom.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomHashTagEntity;
import toy.bookchat.bookchat.db_module.chatroom.repository.ChatRoomHashTagRepository;
import toy.bookchat.bookchat.db_module.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.db_module.chatroom.repository.HashTagRepository;
import toy.bookchat.bookchat.domain.RepositoryTest;

class ChatRoomHashTagEntityRepositoryTest extends RepositoryTest {

  @Autowired
  ChatRoomHashTagRepository chatRoomHashTagRepository;
  @Autowired
  ChatRoomRepository chatRoomRepository;
  @Autowired
  HashTagRepository hashTagRepository;

  @Test
  void 채팅방에대한_해시태그_저장성공() throws Exception {
    ChatRoomHashTagEntity chatRoomHashTagEntity = ChatRoomHashTagEntity.of(1L, 1L);
    ChatRoomHashTagEntity findChatRoomHashTagEntity = chatRoomHashTagRepository.save(chatRoomHashTagEntity);
    assertThat(findChatRoomHashTagEntity).isEqualTo(chatRoomHashTagEntity);
  }
}