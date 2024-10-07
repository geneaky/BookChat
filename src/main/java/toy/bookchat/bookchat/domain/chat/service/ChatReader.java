package toy.bookchat.bookchat.domain.chat.service;

import static java.util.function.Function.identity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.chat.ChatEntity;
import toy.bookchat.bookchat.db_module.chat.repository.ChatRepository;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chat.Sender;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.service.UserReader;
import toy.bookchat.bookchat.exception.notfound.pariticipant.ParticipantNotFoundException;

@Component
public class ChatReader {

  private final ChatRepository chatRepository;
  private final UserReader userReader;

  public ChatReader(ChatRepository chatRepository, UserReader userReader) {
    this.chatRepository = chatRepository;
    this.userReader = userReader;
  }

  public Chat readChat(Long userId, Long chatId) {
    ChatEntity chatEntity = chatRepository.getUserChatRoomChat(chatId, userId)
        .orElseThrow(ParticipantNotFoundException::new);
    User user = userReader.readUser(chatEntity.getUserId());

    Sender sender = Sender.from(user);

    return Chat.builder()
        .id(chatEntity.getId())
        .chatRoomId(chatEntity.getChatRoomId())
        .sender(sender)
        .message(chatEntity.getMessage())
        .dispatchTime(chatEntity.getCreatedAt())
        .build();
  }

  public Slice<Chat> readSlicedChat(Long userId, Long roomId, Long postCursorId, Pageable pageable) {
    Slice<ChatEntity> slicedChatEntity = chatRepository.getChatRoomChats(roomId, postCursorId, pageable, userId);
    List<Long> userIds = slicedChatEntity.stream().map(ChatEntity::getUserId).collect(Collectors.toList());
    List<User> users = userReader.readUsers(userIds);
    Map<Long, User> userIdUserMap = users.stream().collect(Collectors.toMap(User::getId, identity()));

    return slicedChatEntity.map(sce ->
        Chat.builder()
            .id(sce.getId())
            .chatRoomId(sce.getChatRoomId())
            .sender(Sender.from(userIdUserMap.get(sce.getUserId())))
            .message(sce.getMessage())
            .dispatchTime(sce.getCreatedAt())
            .build());
  }

}
