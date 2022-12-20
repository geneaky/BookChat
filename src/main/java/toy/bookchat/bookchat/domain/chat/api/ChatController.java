package toy.bookchat.bookchat.domain.chat.api;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import toy.bookchat.bookchat.domain.chat.api.dto.ChatDto;

@Controller
public class ChatController {

    private final RabbitTemplate rabbitTemplate;

    public ChatController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @MessageMapping("chat.enter.{chatRoomSid}")
    public void enter(ChatDto chat, @DestinationVariable String chatRoomSid) {
        
        rabbitTemplate.convertAndSend("amq.topic", "room." + chatRoomSid, chat);
    }
}
