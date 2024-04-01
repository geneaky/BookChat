package toy.bookchat.bookchat.domain.chat.api;

import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.chat.api.dto.request.MessageDto;
import toy.bookchat.bookchat.domain.chat.api.dto.response.ChatDetailResponse;
import toy.bookchat.bookchat.domain.chat.service.ChatService;
import toy.bookchat.bookchat.domain.chat.service.dto.response.ChatRoomChatsResponse;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPayload;

@Slf4j
@RestController
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/send/chatrooms/{roomId}")
    public void sendMessage(@Valid MessageDto messageDto, @DestinationVariable Long roomId, @UserPayload TokenPayload tokenPayload) {
        chatService.sendMessage(tokenPayload.getUserId(), roomId, messageDto);
    }

    @GetMapping("/v1/api/chatrooms/{roomId}/chats")
    public ChatRoomChatsResponse getChatRoomChats(@PathVariable Long roomId, Long postCursorId,
        Pageable pageable, @UserPayload TokenPayload tokenPayload) {
        return chatService.getChatRoomChats(roomId, postCursorId, pageable, tokenPayload.getUserId());
    }

    @GetMapping("/v1/api/chats/{chatId}")
    public ChatDetailResponse getChat(@PathVariable Long chatId, @UserPayload TokenPayload tokenPayload) {
        return chatService.getChatDetail(chatId, tokenPayload.getUserId());
    }
}
