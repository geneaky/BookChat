package toy.bookchat.bookchat.domain.participant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.repository.query.ParticipantQueryRepository;

public interface ParticipantRepository extends ParticipantQueryRepository,
    JpaRepository<Participant, Long> {

    Long countByChatRoom(ChatRoom chatRoom);

    void deleteByChatRoom(ChatRoom chatRoom);
}
