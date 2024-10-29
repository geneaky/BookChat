package toy.bookchat.bookchat.domain.chat.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.chat.ChatEntity;
import toy.bookchat.bookchat.db_module.chat.repository.ChatRepository;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chat.Sender;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.user.User;

@Component
public class ChatAppender {

  private final ChatRepository chatRepository;

  public ChatAppender(ChatRepository chatRepository) {
    this.chatRepository = chatRepository;
  }

  public Chat append(User user, ChatRoom chatRoom, String message) {
    ChatEntity chatEntity = ChatEntity.builder()
        .userId(user.getId())
        .chatRoomId(chatRoom.getId())
        .message(message)
        .build();
    chatRepository.save(chatEntity);

    Sender sender = Sender.from(user);

    return Chat.builder()
        .id(chatEntity.getId())
        .chatRoomId(chatEntity.getChatRoomId())
        .sender(sender)
        .message(message)
        .dispatchTime(chatEntity.getCreatedAt())
        .build();
  }


  public Chat appendAnnouncement(Long roomId, String announcement) {
    ChatEntity chatEntity = ChatEntity.builder()
        .chatRoomId(roomId)
        .message(announcement)
        .build();
    chatRepository.save(chatEntity);

    return Chat.builder()
        .id(chatEntity.getId())
        .chatRoomId(roomId)
        .message(announcement)
        .dispatchTime(chatEntity.getCreatedAt())
        .build();
  }

  public void appendAnnouncements(List<Long> chatRoomIds, String announcement) {
    List<ChatEntity> chatEntities = chatRoomIds.stream().map(chatRoomId -> ChatEntity.builder()
        .chatRoomId(chatRoomId)
        .message(announcement)
        .build()
    ).collect(Collectors.toList());

    chatRepository.saveAll(chatEntities);
  }
}
