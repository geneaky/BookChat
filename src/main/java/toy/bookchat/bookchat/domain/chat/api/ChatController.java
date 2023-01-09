package toy.bookchat.bookchat.domain.chat.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import toy.bookchat.bookchat.domain.chat.api.dto.ChatDto;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPayload;

@Slf4j
@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("chat.enter.{chatRoomSid}")
    public void send(ChatDto chat, @UserPayload TokenPayload tokenPayload,
        @DestinationVariable String chatRoomSid) throws InterruptedException {
        messagingTemplate.convertAndSend("/topic/" + chatRoomSid, chat);
    }

//    @SubscribeMapping("topic.chatrooms.{chatRoomSid}")
//    public void enter() {
//        log.info("start subscribing chatroom");
//    }
}
