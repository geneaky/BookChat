package toy.bookchat.bookchat.domain.participant.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.GUEST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.HOST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.SUBHOST;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chat.service.ChatAppender;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.service.ChatRoomBlockedUserAppender;
import toy.bookchat.bookchat.domain.chatroom.service.ChatRoomManager;
import toy.bookchat.bookchat.domain.participant.Host;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.ParticipantAdmin;
import toy.bookchat.bookchat.domain.participant.ParticipantWithChatRoom;
import toy.bookchat.bookchat.infrastructure.rabbitmq.MessagePublisher;

@ExtendWith(MockitoExtension.class)
class ParticipantServiceTest {

  @Mock
  private ParticipantReader participantReader;
  @Mock
  private ChatRoomBlockedUserAppender chatRoomBlockedUserAppender;
  @Mock
  private MessagePublisher messagePublisher;
  @Mock
  private ParticipantManager participantManager;
  @Mock
  private ParticipantCleaner participantCleaner;
  @Mock
  private ChatRoomManager chatRoomManager;
  @Mock
  private ChatAppender chatAppender;
  @InjectMocks
  private ParticipantService participantService;

  @Test
  @DisplayName("부방장을 방장으로 위임후 방장은 위임해제 성공")
  void changeParticipantRights1() throws Exception {
    Host host = Host.builder().id(1L).build();
    given(participantReader.readHostForUpdate(any(), any())).willReturn(host);

    Participant participant = Participant.builder().status(SUBHOST).build();
    ChatRoom chatRoom = ChatRoom.builder().sid("L21").build();
    ParticipantWithChatRoom participantWithChatRoom = ParticipantWithChatRoom.builder()
        .participant(participant)
        .chatRoom(chatRoom)
        .build();
    given(participantReader.readParticipantWithChatRoom(any(), any())).willReturn(participantWithChatRoom);

    Chat chat = Chat.builder().dispatchTime(LocalDateTime.now()).build();
    given(chatAppender.appendAnnouncement(any(), any())).willReturn(chat);

    participantService.changeParticipantRights(1L, 2L, HOST, 1L);

    assertAll(
        () -> assertThat(host.getStatus()).isEqualTo(GUEST),
        () -> assertThat(participant.getStatus()).isEqualTo(HOST)
    );
  }

  @Test
  @DisplayName("부방장을 게스트로 변경 성공")
  void changeParticipantRights2() throws Exception {
    Host host = Host.builder().id(1L).build();
    given(participantReader.readHostForUpdate(any(), any())).willReturn(host);

    Participant participant = Participant.builder().status(SUBHOST).build();
    ChatRoom chatRoom = ChatRoom.builder().sid("L21").build();
    ParticipantWithChatRoom participantWithChatRoom = ParticipantWithChatRoom.builder()
        .participant(participant)
        .chatRoom(chatRoom)
        .build();
    given(participantReader.readParticipantWithChatRoom(any(), any())).willReturn(participantWithChatRoom);

    Chat chat = Chat.builder().dispatchTime(LocalDateTime.now()).build();
    given(chatAppender.appendAnnouncement(any(), any())).willReturn(chat);

    participantService.changeParticipantRights(1L, 2L, GUEST, 1L);

    assertThat(participant.getStatus()).isEqualTo(GUEST);
  }

  @Test
  @DisplayName("게스트를 부방장으로 변경 성공")
  void changeParticipantRights3() throws Exception {
    Host host = Host.builder().id(1L).build();
    given(participantReader.readHostForUpdate(any(), any())).willReturn(host);

    Participant participant = Participant.builder().status(GUEST).build();
    ChatRoom chatRoom = ChatRoom.builder().sid("L21").build();
    ParticipantWithChatRoom participantWithChatRoom = ParticipantWithChatRoom.builder()
        .participant(participant)
        .chatRoom(chatRoom)
        .build();
    given(participantReader.readParticipantWithChatRoom(any(), any())).willReturn(participantWithChatRoom);

    Chat chat = Chat.builder().dispatchTime(LocalDateTime.now()).build();
    given(chatAppender.appendAnnouncement(any(), any())).willReturn(chat);

    participantService.changeParticipantRights(1L, 2L, SUBHOST, 1L);

    assertThat(participant.getStatus()).isEqualTo(SUBHOST);
  }

  @Test
  @DisplayName("부방장은 제한 인원수 이상 위임할 수 없다")
  void changeParticipantRights4() throws Exception {
    Host host = Host.builder().id(1L).build();
    given(participantReader.readHostForUpdate(any(), any())).willReturn(host);

    Participant participant = Participant.builder().status(GUEST).build();
    ChatRoom chatRoom = ChatRoom.builder().sid("L21").build();
    ParticipantWithChatRoom participantWithChatRoom = ParticipantWithChatRoom.builder()
        .participant(participant)
        .chatRoom(chatRoom)
        .build();
    given(participantReader.readParticipantWithChatRoom(any(), any())).willReturn(participantWithChatRoom);

    given(participantReader.readParticipantCount(any(), any())).willReturn(5L);

    participantService.changeParticipantRights(1L, 2L, SUBHOST, 1L);

    assertThat(participant.getStatus()).isEqualTo(GUEST);
  }

  @Test
  @DisplayName("게스트를 방장으로 위임후 방장은 게스트가 될 수 있다")
  void changeParticipantRight5() throws Exception {
    Host host = Host.builder().id(1L).build();
    given(participantReader.readHostForUpdate(any(), any())).willReturn(host);

    Participant participant = Participant.builder().status(GUEST).build();
    ChatRoom chatRoom = ChatRoom.builder().sid("L21").build();
    ParticipantWithChatRoom participantWithChatRoom = ParticipantWithChatRoom.builder()
        .participant(participant)
        .chatRoom(chatRoom)
        .build();
    given(participantReader.readParticipantWithChatRoom(any(), any())).willReturn(participantWithChatRoom);

    Chat chat = Chat.builder().dispatchTime(LocalDateTime.now()).build();
    given(chatAppender.appendAnnouncement(any(), any())).willReturn(chat);

    participantService.changeParticipantRights(1L, 2L, HOST, 1L);

    assertThat(participant.getStatus()).isEqualTo(HOST);
    assertThat(host.getStatus()).isEqualTo(GUEST);
  }

  @Test
  @DisplayName("방장이 게스트를 강퇴할 수 있다")
  void kickParticipant1() throws Exception {
    ParticipantAdmin admin = ParticipantAdmin.builder().status(HOST).build();
    given(participantReader.readAdmin(any(), any())).willReturn(admin);

    Participant participant = Participant.builder().status(GUEST).build();
    ChatRoom chatRoom = ChatRoom.builder().build();
    ParticipantWithChatRoom participantWithChatRoom = ParticipantWithChatRoom.builder()
        .participant(participant)
        .chatRoom(chatRoom)
        .build();
    given(participantReader.readParticipantWithChatRoom(any(), any())).willReturn(participantWithChatRoom);
    Chat chat = Chat.builder().dispatchTime(LocalDateTime.now()).build();
    given(chatAppender.appendAnnouncement(any(), any())).willReturn(chat);

    participantService.kickParticipant(1L, 2L, 1L);

    verify(participantCleaner).clean(any());
    verify(chatRoomBlockedUserAppender).append(any());
  }

  @Test
  @DisplayName("방장은 부방장을 강퇴할 수 있다")
  void kickParticipant2() throws Exception {
    ParticipantAdmin admin = ParticipantAdmin.builder().status(HOST).build();
    given(participantReader.readAdmin(any(), any())).willReturn(admin);

    Participant participant = Participant.builder().status(SUBHOST).build();
    ChatRoom chatRoom = ChatRoom.builder().build();
    ParticipantWithChatRoom participantWithChatRoom = ParticipantWithChatRoom.builder()
        .participant(participant)
        .chatRoom(chatRoom)
        .build();
    given(participantReader.readParticipantWithChatRoom(any(), any())).willReturn(participantWithChatRoom);
    Chat chat = Chat.builder().dispatchTime(LocalDateTime.now()).build();
    given(chatAppender.appendAnnouncement(any(), any())).willReturn(chat);

    participantService.kickParticipant(1L, 2L, 1L);

    verify(participantCleaner).clean(any());
    verify(chatRoomBlockedUserAppender).append(any());
  }

  @Test
  @DisplayName("부방장은 게스트를 강퇴할 수 있다")
  void kickParticipant3() throws Exception {
    ParticipantAdmin admin = ParticipantAdmin.builder().status(HOST).build();
    given(participantReader.readAdmin(any(), any())).willReturn(admin);

    Participant participant = Participant.builder().status(GUEST).build();
    ChatRoom chatRoom = ChatRoom.builder().build();
    ParticipantWithChatRoom participantWithChatRoom = ParticipantWithChatRoom.builder()
        .participant(participant)
        .chatRoom(chatRoom)
        .build();
    given(participantReader.readParticipantWithChatRoom(any(), any())).willReturn(participantWithChatRoom);
    Chat chat = Chat.builder().dispatchTime(LocalDateTime.now()).build();
    given(chatAppender.appendAnnouncement(any(), any())).willReturn(chat);

    participantService.kickParticipant(1L, 2L, 1L);

    verify(participantCleaner).clean(any());
    verify(chatRoomBlockedUserAppender).append(any());
  }
}