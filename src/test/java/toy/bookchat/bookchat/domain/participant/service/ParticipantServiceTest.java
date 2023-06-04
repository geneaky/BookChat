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

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chat.repository.ChatRepository;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomBlockedUserRepository;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.exception.forbidden.participant.NoPermissionParticipantException;
import toy.bookchat.bookchat.exception.notfound.pariticipant.ParticipantNotFoundException;
import toy.bookchat.bookchat.infrastructure.broker.MessagePublisher;
import toy.bookchat.bookchat.infrastructure.broker.message.NotificationMessage;

@ExtendWith(MockitoExtension.class)
class ParticipantServiceTest {

    @Mock
    private ParticipantRepository participantRepository;
    @Mock
    private ChatRoomBlockedUserRepository chatRoomBlockedUserRepository;
    @Mock
    private ChatRepository chatRepository;
    @Mock
    private MessagePublisher messagePublisher;
    @InjectMocks
    private ParticipantService participantService;

    @Test
    void 요청자가_방장이_아닐경우_예외발생() throws Exception {

        User host = ParticipantServiceTestFixture.createUser(1L);
        User guest = ParticipantServiceTestFixture.createUser(2L);
        ChatRoom chatRoom = ParticipantServiceTestFixture.createChatRoom(host);

        assertThatThrownBy(() -> {
            participantService.changeParticipantRights(chatRoom.getId(), guest.getId(), SUBHOST,
                guest.getId());
        }).isInstanceOf(NoPermissionParticipantException.class);
    }

    @Test
    void 권한변경_지정한_참여자가_채팅방_참여자가_아닐경우_예외발생() throws Exception {
        User host = ParticipantServiceTestFixture.createUser(1L);
        User guest = ParticipantServiceTestFixture.createUser(2L);
        ChatRoom chatRoom = ParticipantServiceTestFixture.createChatRoom(host);

        Participant participant = ParticipantServiceTestFixture.createHostParticipant(1L, host,
            chatRoom);

        when(participantRepository.findWithPessimisticLockByUserIdAndChatRoomId(host.getId(),
            chatRoom.getId())).thenReturn(Optional.ofNullable(participant));

        assertThatThrownBy(() -> {
            participantService.changeParticipantRights(chatRoom.getId(), guest.getId(), SUBHOST,
                host.getId());
        }).isInstanceOf(ParticipantNotFoundException.class);
    }

    @Test
    void 게스트를_부방장으로_위임_성공() throws Exception {
        User host = ParticipantServiceTestFixture.createUser(1L);
        User guest = ParticipantServiceTestFixture.createUser(2L);
        ChatRoom chatRoom = ParticipantServiceTestFixture.createChatRoom(host);
        Participant participant1 = ParticipantServiceTestFixture.createHostParticipant(1L, host,
            chatRoom);
        Participant participant2 = ParticipantServiceTestFixture.createGuestParticipant(2L, guest,
            chatRoom);
        Chat chat = ParticipantServiceTestFixture.createChat(chatRoom);

        when(participantRepository.findWithPessimisticLockByUserIdAndChatRoomId(host.getId(),
            chatRoom.getId())).thenReturn(Optional.ofNullable(participant1));
        when(participantRepository.findByUserIdAndChatRoomId(guest.getId(),
            chatRoom.getId())).thenReturn(Optional.ofNullable(participant2));
        when(chatRepository.save(any())).thenReturn(chat);
        participantService.changeParticipantRights(1L, guest.getId(), SUBHOST, host.getId());

        assertThat(participant2.getParticipantStatus()).isEqualTo(SUBHOST);
        verify(messagePublisher).sendNotificationMessage(anyString(),
            any(NotificationMessage.class));
    }

    @Test
    void 부방장_제한인원수_이상_위임_불가() throws Exception {
        User host = ParticipantServiceTestFixture.createUser(1L);
        User guest = ParticipantServiceTestFixture.createUser(2L);
        ChatRoom chatRoom = ParticipantServiceTestFixture.createChatRoom(host);
        Participant participant1 = ParticipantServiceTestFixture.createHostParticipant(1L, host,
            chatRoom);
        Participant participant2 = ParticipantServiceTestFixture.createGuestParticipant(2L, guest,
            chatRoom);
        Chat chat = ParticipantServiceTestFixture.createChat(chatRoom);

        when(participantRepository.findWithPessimisticLockByUserIdAndChatRoomId(host.getId(),
            chatRoom.getId())).thenReturn(Optional.ofNullable(participant1));
        when(participantRepository.findByUserIdAndChatRoomId(guest.getId(),
            chatRoom.getId())).thenReturn(Optional.ofNullable(participant2));
        when(participantRepository.countSubHostByRoomId(any())).thenReturn(5L);
        participantService.changeParticipantRights(1L, guest.getId(), SUBHOST, host.getId());

        assertThat(participant2.getParticipantStatus()).isEqualTo(GUEST);
    }

    @Test
    void 부방장을_위임해제_성공() throws Exception {
        User host = ParticipantServiceTestFixture.createUser(1L);
        User subHost = ParticipantServiceTestFixture.createUser(2L);
        ChatRoom chatRoom = ParticipantServiceTestFixture.createChatRoom(host);
        Participant participant1 = ParticipantServiceTestFixture.createHostParticipant(1L, host,
            chatRoom);
        Participant participant2 = ParticipantServiceTestFixture.createSubHostParticipant(2L,
            subHost, chatRoom);
        Chat chat = ParticipantServiceTestFixture.createChat(chatRoom);

        when(participantRepository.findWithPessimisticLockByUserIdAndChatRoomId(host.getId(),
            chatRoom.getId())).thenReturn(Optional.ofNullable(participant1));
        when(participantRepository.findByUserIdAndChatRoomId(subHost.getId(),
            chatRoom.getId())).thenReturn(Optional.ofNullable(participant2));
        when(chatRepository.save(any())).thenReturn(chat);
        participantService.changeParticipantRights(1L, subHost.getId(), GUEST, host.getId());

        assertThat(participant2.getParticipantStatus()).isEqualTo(GUEST);
        verify(messagePublisher).sendNotificationMessage(anyString(),
            any(NotificationMessage.class));
    }

    @Test
    void 부방장을_방장으로_위임후_방장은_위임해제_성공() throws Exception {
        User host = ParticipantServiceTestFixture.createUser(1L);
        User subHost = ParticipantServiceTestFixture.createUser(2L);
        ChatRoom chatRoom = ParticipantServiceTestFixture.createChatRoom(host);
        Participant participant1 = ParticipantServiceTestFixture.createHostParticipant(1L, host,
            chatRoom);
        Participant participant2 = ParticipantServiceTestFixture.createSubHostParticipant(2L,
            subHost, chatRoom);
        Chat chat = ParticipantServiceTestFixture.createChat(chatRoom);

        when(participantRepository.findWithPessimisticLockByUserIdAndChatRoomId(host.getId(),
            chatRoom.getId())).thenReturn(Optional.ofNullable(participant1));
        when(participantRepository.findByUserIdAndChatRoomId(subHost.getId(),
            chatRoom.getId())).thenReturn(Optional.ofNullable(participant2));
        when(chatRepository.save(any())).thenReturn(chat);
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
        verify(messagePublisher).sendNotificationMessage(anyString(),
            any(NotificationMessage.class));
    }

    @Test
    void 게스트_방장으로_위임후_방장_위임해제_성공() throws Exception {
        User host = ParticipantServiceTestFixture.createUser(1L);
        User guest = ParticipantServiceTestFixture.createUser(2L);
        ChatRoom chatRoom = ParticipantServiceTestFixture.createChatRoom(host);
        Participant participant1 = ParticipantServiceTestFixture.createHostParticipant(1L, host,
            chatRoom);
        Participant participant2 = ParticipantServiceTestFixture.createGuestParticipant(2L, guest,
            chatRoom);
        Chat chat = ParticipantServiceTestFixture.createChat(chatRoom);

        when(participantRepository.findWithPessimisticLockByUserIdAndChatRoomId(host.getId(),
            chatRoom.getId())).thenReturn(Optional.ofNullable(participant1));
        when(participantRepository.findByUserIdAndChatRoomId(guest.getId(),
            chatRoom.getId())).thenReturn(Optional.ofNullable(participant2));
        when(chatRepository.save(any())).thenReturn(chat);
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
        verify(messagePublisher).sendNotificationMessage(anyString(),
            any(NotificationMessage.class));
    }

    @Test
    void 방장의_부방장_강퇴_성공() throws Exception {
        User host = ParticipantServiceTestFixture.createUser(1L);
        User subHost = ParticipantServiceTestFixture.createUser(2L);
        ChatRoom chatRoom = ParticipantServiceTestFixture.createChatRoom(host);
        Participant participant1 = ParticipantServiceTestFixture.createHostParticipant(1L, host,
            chatRoom);
        Participant participant2 = ParticipantServiceTestFixture.createSubHostParticipant(2L,
            subHost, chatRoom);
        Chat chat = ParticipantServiceTestFixture.createChat(chatRoom);

        when(participantRepository.findByUserIdAndChatRoomId(host.getId(),
            chatRoom.getId())).thenReturn(Optional.ofNullable(participant1));
        when(participantRepository.findByUserIdAndChatRoomId(subHost.getId(),
            chatRoom.getId())).thenReturn(Optional.ofNullable(participant2));
        when(chatRepository.save(any())).thenReturn(chat);
        participantService.kickParticipant(chatRoom.getId(), subHost.getId(), host.getId());

        verify(participantRepository).delete(participant2);
        verify(chatRoomBlockedUserRepository).save(any());
        verify(messagePublisher).sendNotificationMessage(anyString(),
            any(NotificationMessage.class));
    }

    @Test
    void 방장의_게스트_강퇴_성공() throws Exception {
        User host = ParticipantServiceTestFixture.createUser(1L);
        User guest = ParticipantServiceTestFixture.createUser(2L);
        ChatRoom chatRoom = ParticipantServiceTestFixture.createChatRoom(host);
        Participant participant1 = ParticipantServiceTestFixture.createHostParticipant(1L, host,
            chatRoom);
        Participant participant2 = ParticipantServiceTestFixture.createGuestParticipant(2L, guest,
            chatRoom);
        Chat chat = ParticipantServiceTestFixture.createChat(chatRoom);

        when(participantRepository.findByUserIdAndChatRoomId(1L, 1L)).thenReturn(
            Optional.ofNullable(participant1));
        when(participantRepository.findByUserIdAndChatRoomId(2L, 1L)).thenReturn(
            Optional.ofNullable(participant2));
        when(chatRepository.save(any())).thenReturn(chat);
        participantService.kickParticipant(chatRoom.getId(), guest.getId(), host.getId());

        verify(participantRepository).delete(participant2);
        verify(chatRoomBlockedUserRepository).save(any());
        verify(messagePublisher).sendNotificationMessage(anyString(),
            any(NotificationMessage.class));
    }

    @Test
    void 부방장의_게스트_강퇴_성공() throws Exception {
        User host = ParticipantServiceTestFixture.createUser(1L);
        User subHost = ParticipantServiceTestFixture.createUser(2L);
        User guest = ParticipantServiceTestFixture.createUser(3L);
        ChatRoom chatRoom = ParticipantServiceTestFixture.createChatRoom(host);
        Participant participant1 = ParticipantServiceTestFixture.createHostParticipant(1L, host,
            chatRoom);
        Participant participant2 = ParticipantServiceTestFixture.createGuestParticipant(2L, guest,
            chatRoom);
        Chat chat = ParticipantServiceTestFixture.createChat(chatRoom);

        when(participantRepository.findByUserIdAndChatRoomId(subHost.getId(),
            chatRoom.getId())).thenReturn(Optional.ofNullable(participant1));
        when(participantRepository.findByUserIdAndChatRoomId(guest.getId(),
            chatRoom.getId())).thenReturn(Optional.ofNullable(participant2));
        when(chatRepository.save(any())).thenReturn(chat);
        participantService.kickParticipant(chatRoom.getId(), guest.getId(), subHost.getId());

        verify(participantRepository).delete(participant2);
        verify(chatRoomBlockedUserRepository).save(any());
        verify(messagePublisher).sendNotificationMessage(anyString(),
            any(NotificationMessage.class));
    }

    @Test
    void 게스트가_강퇴_요청시_예외발생() throws Exception {
        User host = ParticipantServiceTestFixture.createUser(1L);
        User guest1 = ParticipantServiceTestFixture.createUser(2L);
        User guest2 = ParticipantServiceTestFixture.createUser(3L);
        ChatRoom chatRoom = ParticipantServiceTestFixture.createChatRoom(host);
        Participant participant1 = ParticipantServiceTestFixture.createGuestParticipant(1L, guest1,
            chatRoom);
        Participant participant2 = ParticipantServiceTestFixture.createGuestParticipant(2L, guest2,
            chatRoom);

        when(participantRepository.findByUserIdAndChatRoomId(guest1.getId(),
            chatRoom.getId())).thenReturn(Optional.ofNullable(participant1));
        when(participantRepository.findByUserIdAndChatRoomId(guest2.getId(),
            chatRoom.getId())).thenReturn(Optional.ofNullable(participant2));

        assertThatThrownBy(() -> {
            participantService.kickParticipant(chatRoom.getId(), guest1.getId(), guest2.getId());
        }).isInstanceOf(NoPermissionParticipantException.class);
    }

    private static class ParticipantServiceTestFixture {

        public static User createUser(Long userId) {
            return User.builder()
                .id(userId)
                .build();
        }

        public static ChatRoom createChatRoom(User host) {
            return ChatRoom.builder()
                .id(1L)
                .host(host)
                .roomSid("L21")
                .build();
        }

        public static Participant createHostParticipant(Long participantId, User host,
            ChatRoom chatRoom) {
            return Participant.builder()
                .id(participantId)
                .user(host)
                .participantStatus(HOST)
                .chatRoom(chatRoom)
                .build();
        }

        public static Participant createSubHostParticipant(Long participantId, User subHost,
            ChatRoom chatRoom) {
            return Participant.builder()
                .id(participantId)
                .user(subHost)
                .participantStatus(SUBHOST)
                .chatRoom(chatRoom)
                .build();
        }

        public static Participant createGuestParticipant(Long participantId, User guest,
            ChatRoom chatRoom) {
            return Participant.builder()
                .id(participantId)
                .user(guest)
                .participantStatus(GUEST)
                .chatRoom(chatRoom)
                .build();
        }

        public static Chat createChat(ChatRoom chatRoom) {
            Chat chat = Chat.builder()
                .id(1L)
                .chatRoom(chatRoom)
                .build();
            chat.setCreatedAt(LocalDateTime.now());

            return chat;
        }
    }
}