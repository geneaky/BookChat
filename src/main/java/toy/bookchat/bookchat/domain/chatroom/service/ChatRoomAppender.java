package toy.bookchat.bookchat.domain.chatroom.service;

import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomEntity;
import toy.bookchat.bookchat.db_module.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;

@Component
public class ChatRoomAppender {

  private final ChatRoomRepository chatRoomRepository;

  public ChatRoomAppender(ChatRoomRepository chatRoomRepository) {
    this.chatRoomRepository = chatRoomRepository;
  }

  public Long append(ChatRoom chatRoom) {
    ChatRoomEntity entity = ChatRoomEntity.builder()
        .bookId(chatRoom.getBookId())
        .roomName(chatRoom.getName())
        .roomSize(chatRoom.getRoomSize())
        .roomImageUri(chatRoom.getRoomImageUri())
        .defaultRoomImageType(chatRoom.getDefaultRoomImageType())
        .roomSid(chatRoom.getSid())
        .build();

    chatRoomRepository.save(entity);

    return entity.getId();
  }
}
