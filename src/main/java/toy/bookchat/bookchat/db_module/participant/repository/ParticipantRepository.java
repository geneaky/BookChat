package toy.bookchat.bookchat.db_module.participant.repository;

import java.util.List;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomEntity;
import toy.bookchat.bookchat.db_module.participant.ParticipantEntity;
import toy.bookchat.bookchat.db_module.participant.repository.query.ParticipantQueryRepository;

public interface ParticipantRepository extends ParticipantQueryRepository,
    JpaRepository<ParticipantEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<ParticipantEntity> findWithPessimisticLockByChatRoomEntity(ChatRoomEntity chatRoomEntity);

    void deleteByChatRoomEntity(ChatRoomEntity chatRoomEntity);

    Long countByChatRoomEntity(ChatRoomEntity chatroom);

}
