package toy.bookchat.bookchat.db_module.participant.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.bookchat.bookchat.db_module.participant.ParticipantEntity;
import toy.bookchat.bookchat.db_module.participant.repository.query.ParticipantQueryRepository;
import toy.bookchat.bookchat.domain.participant.ParticipantStatus;

public interface ParticipantRepository extends ParticipantQueryRepository, JpaRepository<ParticipantEntity, Long> {

  @Modifying
  @Query("delete from ParticipantEntity p where p.chatRoomId = :chatRoomId")
  void deleteByChatRoomId(@Param("chatRoomId") Long chatRoomId);

  Long countByChatRoomId(Long chatroomId);

  List<ParticipantEntity> findByChatRoomId(Long chatRoomId);

  Optional<ParticipantEntity> findByChatRoomIdAndParticipantStatus(Long chatRoomId,
      ParticipantStatus participantStatus);
}
