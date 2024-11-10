package toy.bookchat.bookchat.domain.chat.api.v1.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.participant.Host;

@Getter
public class ChatDetailResponse {

  private Long chatId;
  private Long chatRoomId;
  private String message;
  private LocalDateTime dispatchTime;
  private ChatSenderResponse sender;
  private ChatRoomHostResponse host;

  @Builder
  private ChatDetailResponse(Long chatId, Long chatRoomId, String message, LocalDateTime dispatchTime,
      ChatSenderResponse sender, ChatRoomHostResponse host) {
    this.chatId = chatId;
    this.chatRoomId = chatRoomId;
    this.message = message;
    this.dispatchTime = dispatchTime;
    this.sender = sender;
    this.host = host;
  }

  public static ChatDetailResponse from(Chat chat, Host host) {
    return ChatDetailResponse.builder()
        .chatId(chat.getId())
        .chatRoomId(chat.getChatRoomId())
        .message(chat.getMessage())
        .dispatchTime(chat.getDispatchTime())
        .sender(ChatSenderResponse.from(chat.getSender()))
        .host(ChatRoomHostResponse.from(host))
        .build();
  }
}
