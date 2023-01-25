package toy.bookchat.bookchat.domain.chat.api;

import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import toy.bookchat.bookchat.domain.chat.api.dto.ChatDto;
import toy.bookchat.bookchat.domain.chat.service.ChatService;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPayload;

@Slf4j
@Controller
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/enter/chatrooms/{roomSid}")
    public void enterChatRoom(@UserPayload TokenPayload tokenPayload,
        @DestinationVariable String roomSid) {
        chatService.enterChatRoom(tokenPayload.getUserId(), roomSid);
    }

    @MessageMapping("/leave/chatrooms/{roomSid}")
    public void leaveChatRoom(@UserPayload TokenPayload tokenPayload,
        @DestinationVariable String roomSid) {
        chatService.leaveChatRoom(tokenPayload.getUserId(), roomSid);
    }

    @MessageMapping("/send/chatrooms/{roomSid}")
    public void sendMessage(@Valid ChatDto chat, @UserPayload TokenPayload tokenPayload,
        @DestinationVariable String roomSid) {
        chatService.sendMessage(tokenPayload.getUserId(), roomSid, chat);
    }
}
