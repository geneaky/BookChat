package toy.bookchat.bookchat.domain.participant.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.GUEST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.HOST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.SUBHOST;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomBlockedUserRepository;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.exception.participant.NotHostException;
import toy.bookchat.bookchat.exception.participant.ParticipantNotFoundException;

@ExtendWith(MockitoExtension.class)
class ParticipantServiceTest {

    @Mock
    private ParticipantRepository participantRepository;
    @Mock
    private ChatRoomBlockedUserRepository chatRoomBlockedUserRepository;
    @InjectMocks
    private ParticipantService participantService;

    @Test
    void 요청자가_방장이_아닐경우_예외발생() throws Exception {

        User user = User.builder()
            .id(1L)
            .build();
        User guest = User.builder()
            .id(2L)
            .build();
        ChatRoom chatRoom = ChatRoom.builder()
            .id(1L)
            .host(user)
            .build();

        assertThatThrownBy(() -> {
            participantService.changeParticipantRights(chatRoom.getId(), guest.getId(), SUBHOST,
                guest.getId());
        }).isInstanceOf(NotHostException.class);
    }

    @Test
    void 권한변경_지정한_참여자가_채팅방_참여자가_아닐경우_예외발생() throws Exception {
        User user = User.builder()
            .id(1L)
            .build();
        User guest = User.builder()
            .id(2L)
            .build();
        ChatRoom chatRoom = ChatRoom.builder()
            .id(1L)
            .host(user)
            .build();

        Participant participant = Participant.builder()
            .user(user)
            .chatRoom(chatRoom)
            .build();

        when(participantRepository.findByUserIdAndChatRoomId(user.getId(),
            chatRoom.getId())).thenReturn(
            Optional.ofNullable(participant));

        assertThatThrownBy(() -> {
            participantService.changeParticipantRights(chatRoom.getId(), guest.getId(), SUBHOST,
                user.getId());
        }).isInstanceOf(ParticipantNotFoundException.class);
    }

    @Test
    void 게스트를_부방장으로_승격_성공() throws Exception {
        User host = User.builder()
            .id(1L)
            .build();
        User guest = User.builder()
            .id(2L)
            .build();
        ChatRoom chatRoom = ChatRoom.builder()
            .id(1L)
            .host(host)
            .build();

        Participant participant1 = Participant.builder()
            .id(1L)
            .user(host)
            .participantStatus(HOST)
            .chatRoom(chatRoom)
            .build();
        Participant participant2 = Participant.builder()
            .id(2L)
            .user(guest)
            .participantStatus(GUEST)
            .chatRoom(chatRoom)
            .build();

        when(participantRepository.findByUserIdAndChatRoomId(host.getId(),
            chatRoom.getId())).thenReturn(Optional.ofNullable(participant1));
        when(participantRepository.findByUserIdAndChatRoomId(guest.getId(),
            chatRoom.getId())).thenReturn(Optional.ofNullable(participant2));
        participantService.changeParticipantRights(1L, guest.getId(), SUBHOST, host.getId());

        assertThat(participant2.getParticipantStatus()).isEqualTo(SUBHOST);
    }

    @Test
    void 부방장을_게스트로_강등_성공() throws Exception {
        User host = User.builder()
            .id(1L)
            .build();
        User subHost = User.builder()
            .id(2L)
            .build();
        ChatRoom chatRoom = ChatRoom.builder()
            .id(1L)
            .host(host)
            .build();

        Participant participant1 = Participant.builder()
            .id(1L)
            .user(host)
            .participantStatus(HOST)
            .chatRoom(chatRoom)
            .build();
        Participant participant2 = Participant.builder()
            .id(2L)
            .user(subHost)
            .participantStatus(SUBHOST)
            .chatRoom(chatRoom)
            .build();

        when(participantRepository.findByUserIdAndChatRoomId(host.getId(),
            chatRoom.getId())).thenReturn(Optional.ofNullable(participant1));
        when(participantRepository.findByUserIdAndChatRoomId(subHost.getId(),
            chatRoom.getId())).thenReturn(Optional.ofNullable(participant2));
        participantService.changeParticipantRights(1L, subHost.getId(), GUEST, host.getId());

        assertThat(participant2.getParticipantStatus()).isEqualTo(GUEST);
    }

    @Test
    void 부방장을_방장으로_승격후_방장은_게스트로_강등_성공() throws Exception {
        User host = User.builder()
            .id(1L)
            .build();
        User subHost = User.builder()
            .id(2L)
            .build();
        ChatRoom chatRoom = ChatRoom.builder()
            .id(1L)
            .host(host)
            .build();

        Participant participant1 = Participant.builder()
            .id(1L)
            .user(host)
            .participantStatus(HOST)
            .chatRoom(chatRoom)
            .build();
        Participant participant2 = Participant.builder()
            .id(2L)
            .user(subHost)
            .participantStatus(SUBHOST)
            .chatRoom(chatRoom)
            .build();

        when(participantRepository.findByUserIdAndChatRoomId(host.getId(),
            chatRoom.getId())).thenReturn(Optional.ofNullable(participant1));
        when(participantRepository.findByUserIdAndChatRoomId(subHost.getId(),
            chatRoom.getId())).thenReturn(Optional.ofNullable(participant2));
        participantService.changeParticipantRights(1L, subHost.getId(), HOST, host.getId());

        assertAll(
            () -> {
                assertThat(participant2.getParticipantStatus()).isEqualTo(HOST);
            },
            () -> {
                assertThat(participant1.getParticipantStatus()).isEqualTo(GUEST);
            },
            () -> {
                assertThat(chatRoom.getHost()).isEqualTo(subHost);
            }
        );
    }

    @Test
    void 게스트를_방장으로_승격후_방장은_게스트로_강등_성공() throws Exception {
        User host = User.builder()
            .id(1L)
            .build();
        User guest = User.builder()
            .id(2L)
            .build();
        ChatRoom chatRoom = ChatRoom.builder()
            .id(1L)
            .host(host)
            .build();

        Participant participant1 = Participant.builder()
            .id(1L)
            .user(host)
            .participantStatus(HOST)
            .chatRoom(chatRoom)
            .build();
        Participant participant2 = Participant.builder()
            .id(2L)
            .user(guest)
            .participantStatus(GUEST)
            .chatRoom(chatRoom)
            .build();

        when(participantRepository.findByUserIdAndChatRoomId(host.getId(),
            chatRoom.getId())).thenReturn(Optional.ofNullable(participant1));
        when(participantRepository.findByUserIdAndChatRoomId(guest.getId(),
            chatRoom.getId())).thenReturn(Optional.ofNullable(participant2));
        participantService.changeParticipantRights(1L, guest.getId(), HOST, host.getId());

        assertAll(
            () -> {
                assertThat(participant2.getParticipantStatus()).isEqualTo(HOST);
            },
            () -> {
                assertThat(participant1.getParticipantStatus()).isEqualTo(GUEST);
            },
            () -> {
                assertThat(chatRoom.getHost()).isEqualTo(guest);
            }
        );
    }

    @Test
    void 방장의_부방장_강퇴_성공() throws Exception {
        User host = User.builder()
            .id(1L)
            .build();
        User subHost = User.builder()
            .id(2L)
            .build();
        ChatRoom chatRoom = ChatRoom.builder()
            .id(1L)
            .host(host)
            .build();

        Participant participant1 = Participant.builder()
            .id(1L)
            .user(host)
            .participantStatus(HOST)
            .chatRoom(chatRoom)
            .build();
        Participant participant2 = Participant.builder()
            .id(2L)
            .user(subHost)
            .participantStatus(SUBHOST)
            .chatRoom(chatRoom)
            .build();

        when(participantRepository.findByUserIdAndChatRoomId(host.getId(),
            chatRoom.getId())).thenReturn(Optional.ofNullable(participant1));
        when(participantRepository.findByUserIdAndChatRoomId(subHost.getId(),
            chatRoom.getId())).thenReturn(Optional.ofNullable(participant2));

        participantService.deleteParticipant(chatRoom.getId(), subHost.getId(), host.getId());

        verify(participantRepository).delete(participant2);
        verify(chatRoomBlockedUserRepository).save(any());
    }

    @Test
    void 방장의_게스트_강퇴_성공() throws Exception {
        User host = User.builder()
            .id(1L)
            .build();
        User guest = User.builder()
            .id(2L)
            .build();
        ChatRoom chatRoom = ChatRoom.builder()
            .id(1L)
            .host(host)
            .build();

        Participant participant1 = Participant.builder()
            .id(1L)
            .user(host)
            .participantStatus(HOST)
            .chatRoom(chatRoom)
            .build();
        Participant participant2 = Participant.builder()
            .id(2L)
            .user(guest)
            .participantStatus(GUEST)
            .chatRoom(chatRoom)
            .build();

        when(participantRepository.findByUserIdAndChatRoomId(1L, 1L)).thenReturn(
            Optional.ofNullable(participant1));
        when(participantRepository.findByUserIdAndChatRoomId(2L, 1L)).thenReturn(
            Optional.ofNullable(participant2));

        participantService.deleteParticipant(chatRoom.getId(), guest.getId(), host.getId());

        verify(participantRepository).delete(participant2);
        verify(chatRoomBlockedUserRepository).save(any());
    }

    @Test
    void 부방장의_게스트_강퇴_성공() throws Exception {
        User host = User.builder()
            .id(1L)
            .build();
        User subHost = User.builder()
            .id(2L)
            .build();
        User guest = User.builder()
            .id(3L)
            .build();
        ChatRoom chatRoom = ChatRoom.builder()
            .id(1L)
            .host(host)
            .build();

        Participant participant1 = Participant.builder()
            .id(1L)
            .user(subHost)
            .participantStatus(SUBHOST)
            .chatRoom(chatRoom)
            .build();
        Participant participant2 = Participant.builder()
            .id(2L)
            .user(guest)
            .participantStatus(GUEST)
            .chatRoom(chatRoom)
            .build();

        when(participantRepository.findByUserIdAndChatRoomId(subHost.getId(),
            chatRoom.getId())).thenReturn(Optional.ofNullable(participant1));
        when(participantRepository.findByUserIdAndChatRoomId(guest.getId(),
            chatRoom.getId())).thenReturn(Optional.ofNullable(participant2));

        participantService.deleteParticipant(chatRoom.getId(), guest.getId(), subHost.getId());

        verify(participantRepository).delete(participant2);
        verify(chatRoomBlockedUserRepository).save(any());
    }
}