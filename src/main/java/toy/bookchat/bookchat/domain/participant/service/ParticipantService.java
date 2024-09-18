package toy.bookchat.bookchat.domain.participant.service;

import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.GUEST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.HOST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.SUBHOST;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chat.service.ChatAppender;
import toy.bookchat.bookchat.domain.chatroom.ChatRoomBlockedUser;
import toy.bookchat.bookchat.domain.chatroom.service.ChatRoomBlockedUserAppender;
import toy.bookchat.bookchat.domain.chatroom.service.ChatRoomManager;
import toy.bookchat.bookchat.domain.participant.Host;
import toy.bookchat.bookchat.domain.participant.ParticipantAdmin;
import toy.bookchat.bookchat.domain.participant.ParticipantStatus;
import toy.bookchat.bookchat.domain.participant.ParticipantWithChatRoom;
import toy.bookchat.bookchat.infrastructure.rabbitmq.MessagePublisher;
import toy.bookchat.bookchat.infrastructure.rabbitmq.message.NotificationMessage;

@Service
public class ParticipantService {

  private final int SUB_HOST_COUNT = 5;
  private final ParticipantReader participantReader;
  private final ParticipantManager participantManager;
  private final ParticipantCleaner participantCleaner;
  private final ChatRoomManager chatRoomManager;
  private final ChatRoomBlockedUserAppender chatRoomBlockedUserAppender;
  private final ChatAppender chatAppender;
  private final MessagePublisher messagePublisher;

  public ParticipantService(ParticipantReader participantReader, MessagePublisher messagePublisher,
      ParticipantManager participantManager, ChatAppender chatAppender, ChatRoomManager chatRoomManager,
      ChatRoomBlockedUserAppender chatRoomBlockedUserAppender, ParticipantCleaner participantCleaner) {
    this.participantReader = participantReader;
    this.messagePublisher = messagePublisher;
    this.participantManager = participantManager;
    this.chatAppender = chatAppender;
    this.chatRoomManager = chatRoomManager;
    this.chatRoomBlockedUserAppender = chatRoomBlockedUserAppender;
    this.participantCleaner = participantCleaner;
  }

  @Transactional
  public void changeParticipantRights(Long roomId, Long userId, ParticipantStatus participantStatus, Long requesterId) {
    Host host = participantReader.readHostForUpdate(roomId, requesterId);
    ParticipantWithChatRoom participant = participantReader.readParticipantWithChatRoom(userId, roomId);
    Long subHostCount = participantReader.readParticipantCount(roomId, SUBHOST);

    if (participant.canBeSubHost(participantStatus) && subHostCount < SUB_HOST_COUNT) {
      participant.changeStatus(SUBHOST);
      participantManager.update(participant);

      Chat chat = chatAppender.appendAnnouncement(roomId, "#" + userId + "#님이 부방장이 되었습니다.");
      messagePublisher.sendNotificationMessage(participant.getChatRoomSid(),
          NotificationMessage.createSubHostDelegateMessage(chat, userId));

      return;
    }

    if (participant.canBeGuest(participantStatus)) {
      participant.changeStatus(GUEST);
      participantManager.update(participant);

      Chat chat = chatAppender.appendAnnouncement(roomId, "#" + userId + "#님이 부방장에서 해제되었습니다.");
      messagePublisher.sendNotificationMessage(participant.getChatRoomSid(),
          NotificationMessage.createSubHostDismissMessage(chat, userId));

      return;
    }

    if (participant.canBeHost(participantStatus)) {
      host.changeStatus(GUEST);
      participantManager.update(host);

      participant.changeStatus(HOST);
      participantManager.update(participant);

      Chat chat = chatAppender.appendAnnouncement(roomId, "#" + participant.getParticipantId() + "#님이 방장이 되었습니다.");
      messagePublisher.sendNotificationMessage(participant.getChatRoomSid(),
          NotificationMessage.createHostDelegateMessage(chat, userId));
    }
  }

  @Transactional
  public void kickParticipant(Long roomId, Long userId, Long adminId) {
    ParticipantAdmin admin = participantReader.readAdmin(adminId, roomId);
    ParticipantWithChatRoom participantWithChatRoom = participantReader.readParticipantWithChatRoom(userId, roomId);

    ChatRoomBlockedUser chatRoomBlockedUser = ChatRoomBlockedUser.builder()
        .userId(participantWithChatRoom.getParticipantUserId())
        .build();
    chatRoomBlockedUserAppender.append(chatRoomBlockedUser);

    if (admin.isSubHost() && participantWithChatRoom.isGuest()) {
      participantCleaner.clean(participantWithChatRoom.getParticipant());
    }

    if (admin.isHost() && participantWithChatRoom.isNotHost()) {
      participantCleaner.clean(participantWithChatRoom.getParticipant());
    }

    Chat chat = chatAppender.appendAnnouncement(roomId, "#" + userId + "#님을 내보냈습니다.");
    messagePublisher.sendNotificationMessage(participantWithChatRoom.getChatRoomSid(),
        NotificationMessage.createKickMessage(chat, userId));
  }
}
