package toy.bookchat.bookchat.domain.chat.api;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import toy.bookchat.bookchat.domain.chat.api.dto.ChatDto;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPayload;

@Controller
public class ChatController {

    private final RabbitTemplate rabbitTemplate;

    public ChatController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @MessageMapping("chat.enter.{chatRoomSid}")
    public void enter(ChatDto chat, @DestinationVariable String chatRoomSid, @UserPayload
    TokenPayload tokenPayload) {
        System.out.println(chat);
        System.out.println(chatRoomSid);
        System.out.println(tokenPayload);
        rabbitTemplate.convertAndSend("chat.exchange", "room." + chatRoomSid, chat);
    }
}
