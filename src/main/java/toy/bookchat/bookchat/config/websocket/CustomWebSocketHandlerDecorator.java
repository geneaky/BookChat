package toy.bookchat.bookchat.config.websocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;

public class CustomWebSocketHandlerDecorator extends WebSocketHandlerDecorator {

    private final ParticipantRepository participantRepository;

    public CustomWebSocketHandlerDecorator(ParticipantRepository participantRepository,
        WebSocketHandler webSocketHandler) {
        super(webSocketHandler);
        this.participantRepository = participantRepository;
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus)
        throws Exception {
        if (closeStatus != CloseStatus.NORMAL) {
            participantRepository.disconnectAll(session.getPrincipal().getName());
        }
        super.afterConnectionClosed(session, closeStatus);
    }
}
