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

    @MessageMapping("/enter/chatrooms/{chatRoomSid}")
    public void enterChatRoom(@UserPayload TokenPayload tokenPayload,
        @DestinationVariable String chatRoomSid) {
        /* TODO: 2023-01-11 해당 방에 이미 참여했는지 여부 판단해서 로직 진행
         */
        ChatDto chatDto = ChatDto.builder()
            .message(tokenPayload.getUserNickname() + "님이 입장하셨습니다.")
            .build();
        messagingTemplate.convertAndSend("/topic/" + chatRoomSid,
            chatDto);
    }

    @MessageMapping("/leave/chatrooms/{chatRoomSid}")
    public void leaveChatRoom(@UserPayload TokenPayload tokenPayload,
        @DestinationVariable String chatRoomSid) {
        ChatDto chatDto = ChatDto.builder()
            .message(tokenPayload.getUserNickname() + "님이 퇴장하셨습니다.")
            .build();
        messagingTemplate.convertAndSend("/topic/" + chatRoomSid,
            chatDto);
    }

    @MessageMapping("/send/chatrooms/{chatRoomSid}")
    public void sendChat(ChatDto chat, @UserPayload TokenPayload tokenPayload,
        @DestinationVariable String chatRoomSid) {
        messagingTemplate.convertAndSend("/topic/" + chatRoomSid,
            chat);
    }

}
