package toy.bookchat.bookchat.domain.chat.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import toy.bookchat.bookchat.domain.chat.api.dto.ChatDto;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPayload;

@Slf4j
@Controller
public class ChatController {

    private final RabbitTemplate rabbitTemplate;
    private final SimpMessageSendingOperations simpMessageSendingOperations;

    public ChatController(RabbitTemplate rabbitTemplate,
        SimpMessageSendingOperations simpMessageSendingOperations) {
        this.rabbitTemplate = rabbitTemplate;
        this.simpMessageSendingOperations = simpMessageSendingOperations;
    }

    @MessageMapping("chat.enter.{chatRoomSid}")
    public void enter(ChatDto chat, @UserPayload TokenPayload tokenPayload,
        @DestinationVariable String chatRoomSid) {
//        rabbitTemplate.convertAndSend("chat.exchange", "room." + chatRoomSid, chat);
        log.info(chat.getMessage());
        log.info(tokenPayload.getUserName());
        log.info(chatRoomSid);
    }
}
