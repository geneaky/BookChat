package toy.bookchat.bookchat.domain.participant;

import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.SUBHOST;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
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
    @GeneratedValue
    private Long id;
    @Enumerated(EnumType.STRING)
    private ParticipantStatus participantStatus;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    protected Participant() {
    }

    @Builder
    private Participant(Long id, ParticipantStatus participantStatus, User user,
        ChatRoom chatRoom) {
        this.id = id;
        this.participantStatus = participantStatus;
        this.user = user;
        this.chatRoom = chatRoom;
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
}
