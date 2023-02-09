package toy.bookchat.bookchat.domain.chat.api;

import java.util.Optional;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.chat.api.dto.ChatDto;
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

    @GetMapping("/v1/api/chatrooms/{roomId}/chats")
    public ChatRoomChatsResponse getChatRoomChats(@PathVariable Long roomId,
        @RequestParam Optional<Long> postCursorId, Pageable pageable,
        @UserPayload TokenPayload tokenPayload) {

        return chatService.getChatRoomChats(roomId, postCursorId, pageable,
            tokenPayload.getUserId());
    }
}
