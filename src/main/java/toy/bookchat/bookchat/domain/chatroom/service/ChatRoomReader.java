package toy.bookchat.bookchat.domain.chatroom.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomEntity;
import toy.bookchat.bookchat.db_module.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.ChatRoomResponse;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.UserChatRoomResponse;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.api.v1.request.ChatRoomRequest;
import toy.bookchat.bookchat.domain.chatroom.api.v1.response.ChatRoomDetails;
import toy.bookchat.bookchat.domain.participant.ParticipantStatus;
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
        .sid(chatRoomEntity.getRoomSid())
        .roomSize(chatRoomEntity.getRoomSize())
        .build();
  }

  public ChatRoom readChatRoomWithLock(Long roomId) {
    ChatRoomEntity chatRoomEntity = chatRoomRepository.findWithLockById(roomId)
        .orElseThrow(ChatRoomNotFoundException::new);

    return ChatRoom.builder()
        .id(chatRoomEntity.getId())
        .sid(chatRoomEntity.getRoomSid())
        .roomSize(chatRoomEntity.getRoomSize())
        .build();
  }

  public Slice<UserChatRoomResponse> readSliceUserChatRooms(Long userId, Long bookId, Long postCursorId,
      Pageable pageable) {
    return chatRoomRepository.findUserChatRoomsWithLastChat(pageable, bookId, postCursorId, userId);
  }

  public ChatRoom readChatRoom(Long userId, Long roomId) {
    ChatRoomEntity chatRoomEntity = chatRoomRepository.findUserChatRoom(roomId, userId, null)
        .orElseThrow(ChatRoomNotFoundException::new);

    return ChatRoom.builder()
        .id(chatRoomEntity.getId())
        .name(chatRoomEntity.getRoomName())
        .roomImageUri(chatRoomEntity.getRoomImageUri())
        .defaultRoomImageType(chatRoomEntity.getDefaultRoomImageType())
        .sid(chatRoomEntity.getRoomSid())
        .roomSize(chatRoomEntity.getRoomSize())
        .build();
  }

  public ChatRoom readChatRoom(Long userId, Long roomId, ParticipantStatus participantStatus) {
    ChatRoomEntity chatRoomEntity = chatRoomRepository.findUserChatRoom(roomId, userId, participantStatus)
        .orElseThrow(ChatRoomNotFoundException::new);

    return ChatRoom.builder()
        .id(chatRoomEntity.getId())
        .name(chatRoomEntity.getRoomName())
        .roomImageUri(chatRoomEntity.getRoomImageUri())
        .defaultRoomImageType(chatRoomEntity.getDefaultRoomImageType())
        .sid(chatRoomEntity.getRoomSid())
        .roomSize(chatRoomEntity.getRoomSize())
        .build();
  }

  public Slice<ChatRoomResponse> readSlicedChatRooms(ChatRoomRequest chatRoomRequest, Pageable pageable) {
    return chatRoomRepository.findChatRooms(chatRoomRequest, pageable);
  }

  public ChatRoomDetails readChatRoomDetails(Long roomId, Long userId) {
    return chatRoomRepository.findChatRoomDetails(roomId, userId);
  }

}
