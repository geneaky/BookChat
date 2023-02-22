package toy.bookchat.bookchat.domain.participant.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.repository.query.ParticipantQueryRepository;
import toy.bookchat.bookchat.domain.user.User;

public interface ParticipantRepository extends ParticipantQueryRepository,
    JpaRepository<Participant, Long> {

    Optional<Participant> findByUserAndChatRoom(User user, ChatRoom chatRoom);

    @Modifying
    @Query(value = "INSERT INTO PARTICIPANT VALUES(user_id =: userId, chat_room_id =: chatRoomId, participant_status = 'GUEST' )", nativeQuery = true)
    void insertParticipantNativeQuery(@Param("userId") Long userId,
        @Param("chatRoomId") Long chatRoomId);
}
