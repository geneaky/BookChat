package toy.bookchat.bookchat.domain.chatroom.service;

import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.chatroom.repository.ChatRoomBlockedUserRepository;
import toy.bookchat.bookchat.exception.forbidden.chatroom.BlockedUserInChatRoomException;

@Component
public class ChatRoomUserValidator {

  private final ChatRoomBlockedUserRepository chatRoomBlockedUserRepository;

  public ChatRoomUserValidator(ChatRoomBlockedUserRepository chatRoomBlockedUserRepository) {
    this.chatRoomBlockedUserRepository = chatRoomBlockedUserRepository;
  }

  public void checkIsBlockedUser(Long userId, Long roomId) {
    chatRoomBlockedUserRepository.findByUserIdAndChatRoomId(userId, roomId).ifPresent(blockedUser -> {
      throw new BlockedUserInChatRoomException();
    });
  }
}
