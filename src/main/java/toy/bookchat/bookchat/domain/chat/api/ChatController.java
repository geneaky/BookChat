package toy.bookchat.bookchat.domain.chat.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import toy.bookchat.bookchat.domain.chat.api.dto.ChatDto;
import toy.bookchat.bookchat.domain.chat.service.ChatService;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPayload;

@Slf4j
@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    public ChatController(SimpMessagingTemplate messagingTemplate, ChatService chatService) {
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
    }

    @MessageMapping("/enter/chatrooms/{chatRoomSid}")
    public void enterChatRoom(@UserPayload TokenPayload tokenPayload,
        @DestinationVariable String chatRoomSid) {
        chatService.enterChatRoom(tokenPayload.getUserId(), chatRoomSid);
    }

    @MessageMapping("/leave/chatrooms/{chatRoomSid}")
    public void leaveChatRoom(@UserPayload TokenPayload tokenPayload,
        @DestinationVariable String chatRoomSid) {
        chatService.leaveChatRoom(tokenPayload.getUserId(), chatRoomSid);
    }

    @MessageMapping("/send/chatrooms/{chatRoomSid}")
    public void sendMessage(ChatDto chat, @UserPayload TokenPayload tokenPayload,
        @DestinationVariable String chatRoomSid) {
        chatService.sendMessage(tokenPayload.getUserId(), chatRoomSid, chat);
    }
}
