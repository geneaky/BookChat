package toy.bookchat.bookchat.domain.chatroom.service;

import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomEntity;
import toy.bookchat.bookchat.db_module.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.exception.notfound.chatroom.ChatRoomNotFoundException;

@Component
public class ChatRoomReader {

  private final ChatRoomRepository chatRoomRepository;

  public ChatRoomReader(ChatRoomRepository chatRoomRepository) {
    this.chatRoomRepository = chatRoomRepository;
  }

  public ChatRoom readChatRoom(Long roomId) {
    ChatRoomEntity chatRoomEntity = chatRoomRepository.findById(roomId).orElseThrow(ChatRoomNotFoundException::new);

    return ChatRoom.builder()
        .id(chatRoomEntity.getId())
        .hostId(chatRoomEntity.getHostId())
        .sid(chatRoomEntity.getRoomSid())
        .roomSize(chatRoomEntity.getRoomSize())
        .build();
  }

  public ChatRoom readChatRoomWithLock(Long roomId) {
    ChatRoomEntity chatRoomEntity = chatRoomRepository.findWithLockById(roomId)
        .orElseThrow(ChatRoomNotFoundException::new);

    return ChatRoom.builder()
        .id(chatRoomEntity.getId())
        .hostId(chatRoomEntity.getHostId())
        .sid(chatRoomEntity.getRoomSid())
        .roomSize(chatRoomEntity.getRoomSize())
        .build();
  }
}
