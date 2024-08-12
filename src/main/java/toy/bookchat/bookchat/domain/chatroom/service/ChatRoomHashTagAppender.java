package toy.bookchat.bookchat.domain.chatroom.service;

import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomHashTagEntity;
import toy.bookchat.bookchat.db_module.chatroom.repository.ChatRoomHashTagRepository;
import toy.bookchat.bookchat.domain.chatroom.HashTag;

@Component
public class ChatRoomHashTagAppender {

  private final ChatRoomHashTagRepository chatRoomHashTagRepository;

  public ChatRoomHashTagAppender(ChatRoomHashTagRepository chatRoomHashTagRepository) {
    this.chatRoomHashTagRepository = chatRoomHashTagRepository;
  }

  public void append(Long chatRoomId, HashTag hashTag) {
    ChatRoomHashTagEntity chatRoomHashTagEntity = ChatRoomHashTagEntity.of(chatRoomId, hashTag.getId());
    chatRoomHashTagRepository.save(chatRoomHashTagEntity);
  }
}
