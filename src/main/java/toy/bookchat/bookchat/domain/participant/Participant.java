package toy.bookchat.bookchat.domain.participant;

import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.GUEST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.HOST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.SUBHOST;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.user.User;

@Entity
@Getter
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipantStatus participantStatus;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;
    private Boolean isConnected;

    protected Participant() {
    }

    @Builder
    private Participant(Long id, ParticipantStatus participantStatus, User user,
        ChatRoom chatRoom, Boolean isConnected) {
        this.id = id;
        this.participantStatus = participantStatus;
        this.user = user;
        this.chatRoom = chatRoom;
        this.isConnected = isConnected;
    }

    public Long getUserId() {
        return this.user.getId();
    }

    public String getUserNickname() {
        return this.user.getNickname();
    }

    public String getUserProfileImageUrl() {
        return this.user.getProfileImageUrl();
    }

    public Integer getUserDefaultProfileImageType() {
        return this.user.getDefaultProfileImageType();
    }

    public boolean isSubHost() {
        return this.participantStatus == SUBHOST;
    }

    public boolean isHost() {
        return this.participantStatus == HOST;
    }

    public boolean isNotHost() {
        return this.participantStatus != HOST;
    }

    public boolean isGuest() {
        return this.participantStatus == GUEST;
    }

    public void toGuest() {
        this.participantStatus = GUEST;
    }

    public void toHost() {
        this.participantStatus = HOST;
    }

    public void toSubHost() {
        this.participantStatus = SUBHOST;
    }

    public String getChatRoomSid() {
        return this.chatRoom.getRoomSid();
    }

    public boolean isNotSubHost() {
        return this.participantStatus != SUBHOST;
    }
}
