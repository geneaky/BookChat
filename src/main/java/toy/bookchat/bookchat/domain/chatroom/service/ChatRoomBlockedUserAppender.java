package toy.bookchat.bookchat.domain.chatroom.service;

import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomBlockedUserEntity;
import toy.bookchat.bookchat.db_module.chatroom.repository.ChatRoomBlockedUserRepository;
import toy.bookchat.bookchat.domain.chatroom.ChatRoomBlockedUser;

@Component
public class ChatRoomBlockedUserAppender {

  private final ChatRoomBlockedUserRepository chatRoomBlockedUserRepository;

  public ChatRoomBlockedUserAppender(ChatRoomBlockedUserRepository chatRoomBlockedUserRepository) {
    this.chatRoomBlockedUserRepository = chatRoomBlockedUserRepository;
  }


  public void append(ChatRoomBlockedUser chatRoomBlockedUser) {
    ChatRoomBlockedUserEntity chatRoomBlockedUserEntity = ChatRoomBlockedUserEntity.builder()
        .userId(chatRoomBlockedUser.getUserId())
        .chatRoomId(chatRoomBlockedUser.getChatRoomId())
        .build();

    chatRoomBlockedUserRepository.save(chatRoomBlockedUserEntity);
  }
}
