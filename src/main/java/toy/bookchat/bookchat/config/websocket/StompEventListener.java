package toy.bookchat.bookchat.config.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import toy.bookchat.bookchat.domain.participant.service.ParticipantManager;
import toy.bookchat.bookchat.security.user.UserPrincipal;

@Slf4j
@Component
public class StompEventListener {

    public static final int TOPIC_NAME_LENGTH = 7;

    private final ParticipantManager participantManager;

    public StompEventListener(ParticipantManager participantManager) {
        this.participantManager = participantManager;
    }

    @EventListener
    public void handleStompConnectEvent(SessionConnectedEvent event) {
        UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) event.getUser();
        UserPrincipal userPrincipal = (UserPrincipal) user.getPrincipal();
        log.info("Stomp Connect Event :: {}", userPrincipal.getUsername());
    }

    @EventListener
    public void handleStompDisconnectEvent(SessionDisconnectEvent event) {
        UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) event.getUser();
        UserPrincipal userPrincipal = (UserPrincipal) user.getPrincipal();
        participantManager.disconnectAll(userPrincipal.getUserId());
        log.info("Stomp Disconnect Event :: {}", userPrincipal.getUsername());
    }

    @EventListener
    @Transactional
    public void handleStompSubscribeEvent(SessionSubscribeEvent event) {
        UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) event.getUser();
        UserPrincipal userPrincipal = (UserPrincipal) user.getPrincipal();
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String roomSid = accessor.getDestination().substring(TOPIC_NAME_LENGTH);
        participantManager.connect(userPrincipal.getUserId(), roomSid);
        log.info("Stomp Subscribe Event :: {}", userPrincipal.getUsername());
    }

    @EventListener
    public void handleStompUnsubscribeEvent(SessionSubscribeEvent event) {
        UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) event.getUser();
        UserPrincipal userPrincipal = (UserPrincipal) user.getPrincipal();
        String roomSid = StompHeaderAccessor.wrap(event.getMessage()).getDestination().substring(TOPIC_NAME_LENGTH);
        participantManager.disconnect(userPrincipal.getUserId(), roomSid);
        log.info("Stomp Unsubscribe Event :: {}", userPrincipal.getUsername());
    }

}
