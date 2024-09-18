package toy.bookchat.bookchat.domain.chat.api.v1;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chat.api.v1.request.MessageRequest;
import toy.bookchat.bookchat.domain.chat.api.v1.response.ChatDetailResponse;
import toy.bookchat.bookchat.domain.chat.api.v1.response.ChatRoomChatsResponse;
import toy.bookchat.bookchat.domain.chat.service.ChatService;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPayload;

@RequiredArgsConstructor

@RestController
public class ChatController {

  private final ChatService chatService;

  @MessageMapping("/send/chatrooms/{roomId}")
  public void sendMessage(@Valid MessageRequest messageRequest, @DestinationVariable Long roomId,
      @UserPayload TokenPayload tokenPayload) {
    chatService.sendMessage(tokenPayload.getUserId(), roomId, messageRequest.toTarget());
  }

  @GetMapping("/v1/api/chatrooms/{roomId}/chats")
  public ChatRoomChatsResponse getChatRoomChats(@PathVariable Long roomId, Long postCursorId, Pageable pageable,
      @UserPayload TokenPayload tokenPayload) {
    Slice<Chat> slicedChat = chatService.getChatRoomChats(roomId, postCursorId, pageable, tokenPayload.getUserId());
    return new ChatRoomChatsResponse(slicedChat);
  }

  @GetMapping("/v1/api/chats/{chatId}")
  public ChatDetailResponse getChat(@PathVariable Long chatId, @UserPayload TokenPayload tokenPayload) {
    Chat chat = chatService.getChatDetail(chatId, tokenPayload.getUserId());
    return ChatDetailResponse.from(chat);
  }
}
