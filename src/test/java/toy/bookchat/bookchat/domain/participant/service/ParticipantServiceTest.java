package toy.bookchat.bookchat.domain.participant.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.GUEST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.HOST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.SUBHOST;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomBlockedUserRepository;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.participant.service.dto.CacheClearMessage;
import toy.bookchat.bookchat.domain.participant.service.dto.response.ChatRoomParticipantsResponse;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.exception.participant.NotHostException;
import toy.bookchat.bookchat.exception.participant.ParticipantNotFoundException;

@ExtendWith(MockitoExtension.class)
class ParticipantServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private ParticipantRepository participantRepository;
    @Mock
    private ChatRoomBlockedUserRepository chatRoomBlockedUserRepository;
    @Mock
    private RabbitTemplate rabbitTemplate;
    @InjectMocks
    private ParticipantService participantService;

    @Test
    void 채팅방_참여인원_조회_성공() throws Exception {
        User aUser = User.builder()
            .id(1L)
            .nickname("AUser")
            .defaultProfileImageType(1)
            .build();
        User bUser = User.builder()
            .id(2L)
            .nickname("BUser")
            .profileImageUrl("testB@s3.com")
            .defaultProfileImageType(1)
            .build();
        User cUser = User.builder()
            .id(2L)
            .nickname("CUser")
            .profileImageUrl("testC@s3.com")
            .defaultProfileImageType(1)
            .build();
        ChatRoom chatRoom = ChatRoom.builder()
            .host(aUser)
            .build();

        Participant participant1 = Participant.builder()
            .user(aUser)
            .participantStatus(HOST)
            .chatRoom(chatRoom)
            .build();
        Participant participant2 = Participant.builder()
            .user(bUser)
            .participantStatus(SUBHOST)
            .chatRoom(chatRoom)
            .build();
        Participant participant3 = Participant.builder()
            .user(cUser)
            .participantStatus(GUEST)
            .chatRoom(chatRoom)
            .build();

        List<Participant> participantList = List.of(participant1, participant2, participant3);
        ChatRoomParticipantsResponse expect = ChatRoomParticipantsResponse.from(participantList);
        when(participantRepository.findChatRoomUsers(any(), any())).thenReturn(participantList);
        ChatRoomParticipantsResponse result = participantService.getChatRoomUsers(1L, 1L);

        assertThat(result).isEqualTo(expect);
        verify(participantRepository).findChatRoomUsers(any(), any());
    }

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

        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(guest));
        when(chatRoomRepository.findById(any())).thenReturn(Optional.ofNullable(chatRoom));
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

        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(guest));
        when(chatRoomRepository.findById(any())).thenReturn(Optional.ofNullable(chatRoom));

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

        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(host));
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(guest));
        when(chatRoomRepository.findById(any())).thenReturn(Optional.ofNullable(chatRoom));
        when(participantRepository.findByUserAndChatRoom(host, chatRoom)).thenReturn(
            Optional.ofNullable(participant1));
        when(participantRepository.findByUserAndChatRoom(guest, chatRoom)).thenReturn(
            Optional.ofNullable(participant2));
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

        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(host));
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(subHost));
        when(chatRoomRepository.findById(any())).thenReturn(Optional.ofNullable(chatRoom));
        when(participantRepository.findByUserAndChatRoom(host, chatRoom)).thenReturn(
            Optional.ofNullable(participant1));
        when(participantRepository.findByUserAndChatRoom(subHost, chatRoom)).thenReturn(
            Optional.ofNullable(participant2));
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

        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(host));
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(subHost));
        when(chatRoomRepository.findById(any())).thenReturn(Optional.ofNullable(chatRoom));
        when(participantRepository.findByUserAndChatRoom(host, chatRoom)).thenReturn(
            Optional.ofNullable(participant1));
        when(participantRepository.findByUserAndChatRoom(subHost, chatRoom)).thenReturn(
            Optional.ofNullable(participant2));
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

        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(host));
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(guest));
        when(chatRoomRepository.findById(any())).thenReturn(Optional.ofNullable(chatRoom));
        when(participantRepository.findByUserAndChatRoom(host, chatRoom)).thenReturn(
            Optional.ofNullable(participant1));
        when(participantRepository.findByUserAndChatRoom(guest, chatRoom)).thenReturn(
            Optional.ofNullable(participant2));
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

        when(userRepository.findById(host.getId())).thenReturn(Optional.of(host));
        when(userRepository.findById(subHost.getId())).thenReturn(Optional.of(subHost));
        when(chatRoomRepository.findById(chatRoom.getId())).thenReturn(Optional.of(chatRoom));
        when(participantRepository.findByUserAndChatRoom(host, chatRoom)).thenReturn(
            Optional.ofNullable(participant1));
        when(participantRepository.findByUserAndChatRoom(subHost, chatRoom)).thenReturn(
            Optional.ofNullable(participant2));

        participantService.deleteParticipant(chatRoom.getId(), subHost.getId(), host.getId());

        verify(participantRepository).delete(participant2);
        verify(chatRoomBlockedUserRepository).save(any());
        verify(rabbitTemplate).convertAndSend(anyString(), anyString(),
            any(CacheClearMessage.class));
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

        when(userRepository.findById(host.getId())).thenReturn(Optional.of(host));
        when(userRepository.findById(guest.getId())).thenReturn(Optional.of(guest));
        when(chatRoomRepository.findById(chatRoom.getId())).thenReturn(Optional.of(chatRoom));
        when(participantRepository.findByUserAndChatRoom(host, chatRoom)).thenReturn(
            Optional.ofNullable(participant1));
        when(participantRepository.findByUserAndChatRoom(guest, chatRoom)).thenReturn(
            Optional.ofNullable(participant2));

        participantService.deleteParticipant(chatRoom.getId(), guest.getId(), host.getId());

        verify(participantRepository).delete(participant2);
        verify(chatRoomBlockedUserRepository).save(any());
        verify(rabbitTemplate).convertAndSend(anyString(), anyString(),
            any(CacheClearMessage.class));
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

        when(userRepository.findById(subHost.getId())).thenReturn(Optional.of(subHost));
        when(userRepository.findById(guest.getId())).thenReturn(Optional.of(guest));
        when(chatRoomRepository.findById(chatRoom.getId())).thenReturn(Optional.of(chatRoom));
        when(participantRepository.findByUserAndChatRoom(subHost, chatRoom)).thenReturn(
            Optional.ofNullable(participant1));
        when(participantRepository.findByUserAndChatRoom(guest, chatRoom)).thenReturn(
            Optional.ofNullable(participant2));

        participantService.deleteParticipant(chatRoom.getId(), guest.getId(), subHost.getId());

        verify(participantRepository).delete(participant2);
        verify(chatRoomBlockedUserRepository).save(any());
        verify(rabbitTemplate).convertAndSend(anyString(), anyString(),
            any(CacheClearMessage.class));
    }
}