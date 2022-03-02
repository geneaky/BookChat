package toy.bookchat.bookchat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import toy.bookchat.bookchat.domain.chat.ChatMessage;
import toy.bookchat.bookchat.domain.chat.ChatRoom;
import toy.bookchat.bookchat.domain.chat.ChatRoomRepository;

@RequiredArgsConstructor
@Component
public class ChatHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final ChatRoomRepository chatRoomRepository;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();

        ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);
        ChatRoom chatroom = chatRoomRepository.getChatRoom(chatMessage.getChatRoomId());
        chatroom.handleMessage(session, chatMessage, objectMapper);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        chatRoomRepository.remove(session);
    }
}
