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
import toy.bookchat.bookchat.db_module.chat.ChatEntity;
import toy.bookchat.bookchat.db_module.chat.repository.ChatRepository;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomEntity;
import toy.bookchat.bookchat.db_module.chatroom.repository.ChatRoomBlockedUserRepository;
import toy.bookchat.bookchat.db_module.participant.ParticipantEntity;
import toy.bookchat.bookchat.db_module.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.db_module.user.UserEntity;
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

        UserEntity host = ParticipantServiceTestFixture.createUser(1L);
        UserEntity guest = ParticipantServiceTestFixture.createUser(2L);
        ChatRoomEntity chatRoomEntity = ParticipantServiceTestFixture.createChatRoom(host);

        assertThatThrownBy(() -> {
            participantService.changeParticipantRights(chatRoomEntity.getId(), guest.getId(), SUBHOST,
                guest.getId());
        }).isInstanceOf(NoPermissionParticipantException.class);
    }

    @Test
    void 권한변경_지정한_참여자가_채팅방_참여자가_아닐경우_예외발생() throws Exception {
        UserEntity host = ParticipantServiceTestFixture.createUser(1L);
        UserEntity guest = ParticipantServiceTestFixture.createUser(2L);
        ChatRoomEntity chatRoomEntity = ParticipantServiceTestFixture.createChatRoom(host);

        ParticipantEntity participantEntity = ParticipantServiceTestFixture.createHostParticipant(1L, host,
            chatRoomEntity);

        when(participantRepository.findWithPessimisticLockByUserIdAndChatRoomId(host.getId(),
            chatRoomEntity.getId())).thenReturn(Optional.ofNullable(participantEntity));

        assertThatThrownBy(() -> {
            participantService.changeParticipantRights(chatRoomEntity.getId(), guest.getId(), SUBHOST,
                host.getId());
        }).isInstanceOf(ParticipantNotFoundException.class);
    }

    @Test
    void 게스트를_부방장으로_위임_성공() throws Exception {
        UserEntity host = ParticipantServiceTestFixture.createUser(1L);
        UserEntity guest = ParticipantServiceTestFixture.createUser(2L);
        ChatRoomEntity chatRoomEntity = ParticipantServiceTestFixture.createChatRoom(host);
        ParticipantEntity participantEntity1 = ParticipantServiceTestFixture.createHostParticipant(1L, host,
            chatRoomEntity);
        ParticipantEntity participantEntity2 = ParticipantServiceTestFixture.createGuestParticipant(2L, guest,
            chatRoomEntity);
        ChatEntity chatEntity = ParticipantServiceTestFixture.createChat(chatRoomEntity);

        when(participantRepository.findWithPessimisticLockByUserIdAndChatRoomId(host.getId(),
            chatRoomEntity.getId())).thenReturn(Optional.ofNullable(participantEntity1));
        when(participantRepository.findByUserIdAndChatRoomId(guest.getId(),
            chatRoomEntity.getId())).thenReturn(Optional.ofNullable(participantEntity2));
        when(chatRepository.save(any())).thenReturn(chatEntity);
        participantService.changeParticipantRights(1L, guest.getId(), SUBHOST, host.getId());

        assertThat(participantEntity2.getParticipantStatus()).isEqualTo(SUBHOST);
        verify(messagePublisher).sendNotificationMessage(anyString(),
            any(NotificationMessage.class));
    }

    @Test
    void 부방장_제한인원수_이상_위임_불가() throws Exception {
        UserEntity host = ParticipantServiceTestFixture.createUser(1L);
        UserEntity guest = ParticipantServiceTestFixture.createUser(2L);
        ChatRoomEntity chatRoomEntity = ParticipantServiceTestFixture.createChatRoom(host);
        ParticipantEntity participantEntity1 = ParticipantServiceTestFixture.createHostParticipant(1L, host,
            chatRoomEntity);
        ParticipantEntity participantEntity2 = ParticipantServiceTestFixture.createGuestParticipant(2L, guest,
            chatRoomEntity);
        ChatEntity chatEntity = ParticipantServiceTestFixture.createChat(chatRoomEntity);

        when(participantRepository.findWithPessimisticLockByUserIdAndChatRoomId(host.getId(),
            chatRoomEntity.getId())).thenReturn(Optional.ofNullable(participantEntity1));
        when(participantRepository.findByUserIdAndChatRoomId(guest.getId(),
            chatRoomEntity.getId())).thenReturn(Optional.ofNullable(participantEntity2));
        when(participantRepository.countSubHostByRoomId(any())).thenReturn(5L);
        participantService.changeParticipantRights(1L, guest.getId(), SUBHOST, host.getId());

        assertThat(participantEntity2.getParticipantStatus()).isEqualTo(GUEST);
    }

    @Test
    void 부방장을_위임해제_성공() throws Exception {
        UserEntity host = ParticipantServiceTestFixture.createUser(1L);
        UserEntity subHost = ParticipantServiceTestFixture.createUser(2L);
        ChatRoomEntity chatRoomEntity = ParticipantServiceTestFixture.createChatRoom(host);
        ParticipantEntity participantEntity1 = ParticipantServiceTestFixture.createHostParticipant(1L, host,
            chatRoomEntity);
        ParticipantEntity participantEntity2 = ParticipantServiceTestFixture.createSubHostParticipant(2L,
            subHost, chatRoomEntity);
        ChatEntity chatEntity = ParticipantServiceTestFixture.createChat(chatRoomEntity);

        when(participantRepository.findWithPessimisticLockByUserIdAndChatRoomId(host.getId(),
            chatRoomEntity.getId())).thenReturn(Optional.ofNullable(participantEntity1));
        when(participantRepository.findByUserIdAndChatRoomId(subHost.getId(),
            chatRoomEntity.getId())).thenReturn(Optional.ofNullable(participantEntity2));
        when(chatRepository.save(any())).thenReturn(chatEntity);
        participantService.changeParticipantRights(1L, subHost.getId(), GUEST, host.getId());

        assertThat(participantEntity2.getParticipantStatus()).isEqualTo(GUEST);
        verify(messagePublisher).sendNotificationMessage(anyString(),
            any(NotificationMessage.class));
    }

    @Test
    void 부방장을_방장으로_위임후_방장은_위임해제_성공() throws Exception {
        UserEntity host = ParticipantServiceTestFixture.createUser(1L);
        UserEntity subHost = ParticipantServiceTestFixture.createUser(2L);
        ChatRoomEntity chatRoomEntity = ParticipantServiceTestFixture.createChatRoom(host);
        ParticipantEntity participantEntity1 = ParticipantServiceTestFixture.createHostParticipant(1L, host,
            chatRoomEntity);
        ParticipantEntity participantEntity2 = ParticipantServiceTestFixture.createSubHostParticipant(2L,
            subHost, chatRoomEntity);
        ChatEntity chatEntity = ParticipantServiceTestFixture.createChat(chatRoomEntity);

        when(participantRepository.findWithPessimisticLockByUserIdAndChatRoomId(host.getId(),
            chatRoomEntity.getId())).thenReturn(Optional.ofNullable(participantEntity1));
        when(participantRepository.findByUserIdAndChatRoomId(subHost.getId(),
            chatRoomEntity.getId())).thenReturn(Optional.ofNullable(participantEntity2));
        when(chatRepository.save(any())).thenReturn(chatEntity);
        participantService.changeParticipantRights(1L, subHost.getId(), HOST, host.getId());

        assertAll(
            () -> {
                assertThat(participantEntity2.getParticipantStatus()).isEqualTo(HOST);
            },
            () -> {
                assertThat(participantEntity1.getParticipantStatus()).isEqualTo(GUEST);
            },
            () -> {
                assertThat(chatRoomEntity.getHost()).isEqualTo(subHost);
            }
        );
        verify(messagePublisher).sendNotificationMessage(anyString(),
            any(NotificationMessage.class));
    }

    @Test
    void 게스트_방장으로_위임후_방장_위임해제_성공() throws Exception {
        UserEntity host = ParticipantServiceTestFixture.createUser(1L);
        UserEntity guest = ParticipantServiceTestFixture.createUser(2L);
        ChatRoomEntity chatRoomEntity = ParticipantServiceTestFixture.createChatRoom(host);
        ParticipantEntity participantEntity1 = ParticipantServiceTestFixture.createHostParticipant(1L, host,
            chatRoomEntity);
        ParticipantEntity participantEntity2 = ParticipantServiceTestFixture.createGuestParticipant(2L, guest,
            chatRoomEntity);
        ChatEntity chatEntity = ParticipantServiceTestFixture.createChat(chatRoomEntity);

        when(participantRepository.findWithPessimisticLockByUserIdAndChatRoomId(host.getId(),
            chatRoomEntity.getId())).thenReturn(Optional.ofNullable(participantEntity1));
        when(participantRepository.findByUserIdAndChatRoomId(guest.getId(),
            chatRoomEntity.getId())).thenReturn(Optional.ofNullable(participantEntity2));
        when(chatRepository.save(any())).thenReturn(chatEntity);
        participantService.changeParticipantRights(1L, guest.getId(), HOST, host.getId());

        assertAll(
            () -> {
                assertThat(participantEntity2.getParticipantStatus()).isEqualTo(HOST);
            },
            () -> {
                assertThat(participantEntity1.getParticipantStatus()).isEqualTo(GUEST);
            },
            () -> {
                assertThat(chatRoomEntity.getHost()).isEqualTo(guest);
            }
        );
        verify(messagePublisher).sendNotificationMessage(anyString(),
            any(NotificationMessage.class));
    }

    @Test
    void 방장의_부방장_강퇴_성공() throws Exception {
        UserEntity host = ParticipantServiceTestFixture.createUser(1L);
        UserEntity subHost = ParticipantServiceTestFixture.createUser(2L);
        ChatRoomEntity chatRoomEntity = ParticipantServiceTestFixture.createChatRoom(host);
        ParticipantEntity participantEntity1 = ParticipantServiceTestFixture.createHostParticipant(1L, host,
            chatRoomEntity);
        ParticipantEntity participantEntity2 = ParticipantServiceTestFixture.createSubHostParticipant(2L,
            subHost, chatRoomEntity);
        ChatEntity chatEntity = ParticipantServiceTestFixture.createChat(chatRoomEntity);

        when(participantRepository.findByUserIdAndChatRoomId(host.getId(),
            chatRoomEntity.getId())).thenReturn(Optional.ofNullable(participantEntity1));
        when(participantRepository.findByUserIdAndChatRoomId(subHost.getId(),
            chatRoomEntity.getId())).thenReturn(Optional.ofNullable(participantEntity2));
        when(chatRepository.save(any())).thenReturn(chatEntity);
        participantService.kickParticipant(chatRoomEntity.getId(), subHost.getId(), host.getId());

        verify(participantRepository).delete(participantEntity2);
        verify(chatRoomBlockedUserRepository).save(any());
        verify(messagePublisher).sendNotificationMessage(anyString(),
            any(NotificationMessage.class));
    }

    @Test
    void 방장의_게스트_강퇴_성공() throws Exception {
        UserEntity host = ParticipantServiceTestFixture.createUser(1L);
        UserEntity guest = ParticipantServiceTestFixture.createUser(2L);
        ChatRoomEntity chatRoomEntity = ParticipantServiceTestFixture.createChatRoom(host);
        ParticipantEntity participantEntity1 = ParticipantServiceTestFixture.createHostParticipant(1L, host,
            chatRoomEntity);
        ParticipantEntity participantEntity2 = ParticipantServiceTestFixture.createGuestParticipant(2L, guest,
            chatRoomEntity);
        ChatEntity chatEntity = ParticipantServiceTestFixture.createChat(chatRoomEntity);

        when(participantRepository.findByUserIdAndChatRoomId(1L, 1L)).thenReturn(
            Optional.ofNullable(participantEntity1));
        when(participantRepository.findByUserIdAndChatRoomId(2L, 1L)).thenReturn(
            Optional.ofNullable(participantEntity2));
        when(chatRepository.save(any())).thenReturn(chatEntity);
        participantService.kickParticipant(chatRoomEntity.getId(), guest.getId(), host.getId());

        verify(participantRepository).delete(participantEntity2);
        verify(chatRoomBlockedUserRepository).save(any());
        verify(messagePublisher).sendNotificationMessage(anyString(),
            any(NotificationMessage.class));
    }

    @Test
    void 부방장의_게스트_강퇴_성공() throws Exception {
        UserEntity host = ParticipantServiceTestFixture.createUser(1L);
        UserEntity subHost = ParticipantServiceTestFixture.createUser(2L);
        UserEntity guest = ParticipantServiceTestFixture.createUser(3L);
        ChatRoomEntity chatRoomEntity = ParticipantServiceTestFixture.createChatRoom(host);
        ParticipantEntity participantEntity1 = ParticipantServiceTestFixture.createHostParticipant(1L, host,
            chatRoomEntity);
        ParticipantEntity participantEntity2 = ParticipantServiceTestFixture.createGuestParticipant(2L, guest,
            chatRoomEntity);
        ChatEntity chatEntity = ParticipantServiceTestFixture.createChat(chatRoomEntity);

        when(participantRepository.findByUserIdAndChatRoomId(subHost.getId(),
            chatRoomEntity.getId())).thenReturn(Optional.ofNullable(participantEntity1));
        when(participantRepository.findByUserIdAndChatRoomId(guest.getId(),
            chatRoomEntity.getId())).thenReturn(Optional.ofNullable(participantEntity2));
        when(chatRepository.save(any())).thenReturn(chatEntity);
        participantService.kickParticipant(chatRoomEntity.getId(), guest.getId(), subHost.getId());

        verify(participantRepository).delete(participantEntity2);
        verify(chatRoomBlockedUserRepository).save(any());
        verify(messagePublisher).sendNotificationMessage(anyString(),
            any(NotificationMessage.class));
    }

    @Test
    void 게스트가_강퇴_요청시_예외발생() throws Exception {
        UserEntity host = ParticipantServiceTestFixture.createUser(1L);
        UserEntity guest1 = ParticipantServiceTestFixture.createUser(2L);
        UserEntity guest2 = ParticipantServiceTestFixture.createUser(3L);
        ChatRoomEntity chatRoomEntity = ParticipantServiceTestFixture.createChatRoom(host);
        ParticipantEntity participantEntity1 = ParticipantServiceTestFixture.createGuestParticipant(1L, guest1,
            chatRoomEntity);
        ParticipantEntity participantEntity2 = ParticipantServiceTestFixture.createGuestParticipant(2L, guest2,
            chatRoomEntity);

        when(participantRepository.findByUserIdAndChatRoomId(guest1.getId(),
            chatRoomEntity.getId())).thenReturn(Optional.ofNullable(participantEntity1));
        when(participantRepository.findByUserIdAndChatRoomId(guest2.getId(),
            chatRoomEntity.getId())).thenReturn(Optional.ofNullable(participantEntity2));

        assertThatThrownBy(() -> {
            participantService.kickParticipant(chatRoomEntity.getId(), guest1.getId(), guest2.getId());
        }).isInstanceOf(NoPermissionParticipantException.class);
    }

    private static class ParticipantServiceTestFixture {

        public static UserEntity createUser(Long userId) {
            return UserEntity.builder()
                .id(userId)
                .build();
        }

        public static ChatRoomEntity createChatRoom(UserEntity host) {
            return ChatRoomEntity.builder()
                .id(1L)
                .host(host)
                .roomSid("L21")
                .build();
        }

        public static ParticipantEntity createHostParticipant(Long participantId, UserEntity host,
            ChatRoomEntity chatRoomEntity) {
            return ParticipantEntity.builder()
                .id(participantId)
                .userEntity(host)
                .participantStatus(HOST)
                .chatRoomEntity(chatRoomEntity)
                .build();
        }

        public static ParticipantEntity createSubHostParticipant(Long participantId, UserEntity subHost,
            ChatRoomEntity chatRoomEntity) {
            return ParticipantEntity.builder()
                .id(participantId)
                .userEntity(subHost)
                .participantStatus(SUBHOST)
                .chatRoomEntity(chatRoomEntity)
                .build();
        }

        public static ParticipantEntity createGuestParticipant(Long participantId, UserEntity guest,
            ChatRoomEntity chatRoomEntity) {
            return ParticipantEntity.builder()
                .id(participantId)
                .userEntity(guest)
                .participantStatus(GUEST)
                .chatRoomEntity(chatRoomEntity)
                .build();
        }

        public static ChatEntity createChat(ChatRoomEntity chatRoomEntity) {
            ChatEntity chatEntity = ChatEntity.builder()
                .id(1L)
                .chatRoomEntity(chatRoomEntity)
                .build();
            chatEntity.setCreatedAt(LocalDateTime.now());

            return chatEntity;
        }
    }
}