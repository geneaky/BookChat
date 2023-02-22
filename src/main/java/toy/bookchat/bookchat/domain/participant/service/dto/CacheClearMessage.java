package toy.bookchat.bookchat.domain.participant.service.dto;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CacheClearMessage {

    private String blockedUserNickname;
    private Long adminId;
    private Long userId;
    private Long chatRoomId;
    private String roomSid;

    @Builder
    private CacheClearMessage(String blockedUserNickname, Long adminId, Long userId,
        Long chatRoomId, String roomSid) {
        this.blockedUserNickname = blockedUserNickname;
        this.adminId = adminId;
        this.userId = userId;
        this.chatRoomId = chatRoomId;
        this.roomSid = roomSid;
    }

    public String blockingComment() {
        return "채팅방 관리자가 " + this.blockedUserNickname + "님을 내보냈습니다.";
    }
}
