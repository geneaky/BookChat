package toy.bookchat.bookchat.domain.participant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.domain.participant.Participant;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

}
