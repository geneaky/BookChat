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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.domain.chatroom.ChatRoomEntity;
import toy.bookchat.bookchat.domain.user.UserEntity;

@Getter
@Entity
@Table(name = "participant")
public class ParticipantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipantStatus participantStatus;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoomEntity chatRoomEntity;
    private Boolean isConnected;

    protected ParticipantEntity() {
    }

    @Builder
    private ParticipantEntity(Long id, ParticipantStatus participantStatus, UserEntity userEntity,
        ChatRoomEntity chatRoomEntity, Boolean isConnected) {
        this.id = id;
        this.participantStatus = participantStatus;
        this.userEntity = userEntity;
        this.chatRoomEntity = chatRoomEntity;
        this.isConnected = isConnected;
    }

    public Long getUserId() {
        return this.userEntity.getId();
    }

    public String getUserNickname() {
        return this.userEntity.getNickname();
    }

    public String getUserProfileImageUrl() {
        return this.userEntity.getProfileImageUrl();
    }

    public Integer getUserDefaultProfileImageType() {
        return this.userEntity.getDefaultProfileImageType();
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
        return this.chatRoomEntity.getRoomSid();
    }

    public boolean isNotSubHost() {
        return this.participantStatus != SUBHOST;
    }

    public void connect() {
        this.isConnected = true;
    }
}
