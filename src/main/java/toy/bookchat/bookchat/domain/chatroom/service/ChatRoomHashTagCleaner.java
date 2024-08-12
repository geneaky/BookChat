package toy.bookchat.bookchat.domain.chatroom.service;

import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.chatroom.repository.ChatRoomHashTagRepository;

@Component
public class ChatRoomHashTagCleaner {

  private final ChatRoomHashTagRepository chatRoomHashTagRepository;

  public ChatRoomHashTagCleaner(ChatRoomHashTagRepository chatRoomHashTagRepository) {
    this.chatRoomHashTagRepository = chatRoomHashTagRepository;
  }

  public void cleanAll(Long chatRoomId) {
    chatRoomHashTagRepository.deleteAllByChatRoomId(chatRoomId);
  }
}
