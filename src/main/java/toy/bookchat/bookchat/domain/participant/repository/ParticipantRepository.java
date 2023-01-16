package toy.bookchat.bookchat.domain.participant.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.user.User;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    Optional<Participant> findByUserAndChatRoom(User user, ChatRoom chatRoom);
}
