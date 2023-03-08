package toy.bookchat.bookchat.domain.participant.service.dto;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CacheClearMessage {

    @NotBlank
    private String blockedUserNickname;
    @NotNull
    private Long adminId;
    @NotNull
    private Long userId;
    @NotNull
    private Long chatRoomId;
    @NotBlank
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
