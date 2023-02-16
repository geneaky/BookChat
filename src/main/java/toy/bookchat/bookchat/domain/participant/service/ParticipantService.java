package toy.bookchat.bookchat.domain.participant.service;

import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.GUEST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.HOST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.SUBHOST;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.ParticipantStatus;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.participant.service.dto.ChatRoomParticipantsResponse;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.exception.chatroom.ChatRoomNotFoundException;
import toy.bookchat.bookchat.exception.participant.NotHostException;
import toy.bookchat.bookchat.exception.participant.ParticipantNotFoundException;
import toy.bookchat.bookchat.exception.user.UserNotFoundException;

@Service
public class ParticipantService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ParticipantRepository participantRepository;

    public ParticipantService(UserRepository userRepository,
        ChatRoomRepository chatRoomRepository, ParticipantRepository participantRepository) {
        this.userRepository = userRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.participantRepository = participantRepository;
    }

    @Transactional(readOnly = true)
    public ChatRoomParticipantsResponse getChatRoomUsers(Long roomId, Long userId) {
        return ChatRoomParticipantsResponse.from(
            participantRepository.findChatRoomUsers(roomId, userId));
    }

    @Transactional
    public void changeParticipantRights(Long roomId,
        Long userId, ParticipantStatus participantStatus,
        Long requesterId) {
        User requester = userRepository.findById(requesterId)
            .orElseThrow(UserNotFoundException::new);
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
            .orElseThrow(ChatRoomNotFoundException::new);

        validateIsHost(requester, chatRoom);

        Participant host = participantRepository.findByUserAndChatRoom(requester, chatRoom)
            .orElseThrow(ParticipantNotFoundException::new);
        Participant participant = participantRepository.findByUserAndChatRoom(user, chatRoom)
            .orElseThrow(ParticipantNotFoundException::new);

        if (participantStatus == SUBHOST) {
            participant.toSubHost();
        }

        if (participantStatus == GUEST) {
            participant.toGuest();
        }

        if (participantStatus == HOST) {
            host.toGuest();
            participant.toHost();
            chatRoom.changeHost(user);
        }
    }

    private static void validateIsHost(User user, ChatRoom chatRoom) {
        if (chatRoom.getHost() != user) {
            throw new NotHostException();
        }
    }
}
