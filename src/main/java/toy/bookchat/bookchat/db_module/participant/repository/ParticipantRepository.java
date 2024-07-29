package toy.bookchat.bookchat.db_module.participant.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.db_module.participant.ParticipantEntity;
import toy.bookchat.bookchat.db_module.participant.repository.query.ParticipantQueryRepository;

public interface ParticipantRepository extends ParticipantQueryRepository, JpaRepository<ParticipantEntity, Long> {

  void deleteByChatRoomId(Long chatRoomId);

  Long countByChatRoomId(Long chatroomId);

  List<ParticipantEntity> findByChatRoomId(Long chatRoomId);
}
