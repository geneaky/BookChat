package toy.bookchat.bookchat.domain.chatroom.service;

import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomEntity;
import toy.bookchat.bookchat.db_module.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.exception.notfound.pariticipant.ParticipantNotFoundException;

@Component
public class ChatRoomManager {

  private final ChatRoomRepository chatRoomRepository;

  public ChatRoomManager(ChatRoomRepository chatRoomRepository) {
    this.chatRoomRepository = chatRoomRepository;
  }

  public void update(ChatRoom chatRoom) {
    ChatRoomEntity chatRoomEntity = chatRoomRepository.findById(chatRoom.getId())
        .orElseThrow(ParticipantNotFoundException::new);

    chatRoomEntity.changeRoomName(chatRoom.getName());
    chatRoomEntity.changeRoomSize(chatRoom.getRoomSize());
    chatRoomEntity.changeRoomImageUri(chatRoom.getRoomImageUri());
  }
}
