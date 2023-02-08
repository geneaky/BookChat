package toy.bookchat.bookchat.domain.participant.service.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.user.User;

@Getter
@EqualsAndHashCode
@ToString
public class ChatRoomParticipantsResponse {

    private RoomHost roomHost;
    private List<RoomSubHost> roomSubHostList;
    private List<RoomGuest> roomGuestList;

    @Builder
    private ChatRoomParticipantsResponse(RoomHost roomHost, List<RoomSubHost> roomSubHostList,
        List<RoomGuest> roomGuestList) {
        this.roomHost = roomHost;
        this.roomSubHostList = roomSubHostList;
        this.roomGuestList = roomGuestList;
    }

    public static ChatRoomParticipantsResponse from(List<Participant> participants) {
        User host = getHost(participants);
        RoomHost roomHost = RoomHost.builder()
            .id(host.getId())
            .nickname(host.getNickname())
            .profileImageUrl(host.getProfileImageUrl())
            .defaultProfileImageType(host.getDefaultProfileImageType())
            .build();
        List<RoomSubHost> roomSubHostList = new ArrayList<>();
        List<RoomGuest> roomGuestList = new ArrayList<>();
        fillParticipantsResponse(participants, host, roomSubHostList, roomGuestList);

        return new ChatRoomParticipantsResponse(roomHost, roomSubHostList, roomGuestList);
    }

    private static User getHost(List<Participant> participants) {
        return participants.get(0).getChatRoom().getHost();
    }

    private static void fillParticipantsResponse(List<Participant> participants, User host,
        List<RoomSubHost> roomSubHostList, List<RoomGuest> roomGuestList) {
        participants.stream().filter(participant -> !participant.getUser().equals(host))
            .forEach(participant -> {
                if (participant.isSubHost()) {
                    roomSubHostList.add(RoomSubHost.builder()
                        .id(participant.getUserId())
                        .nickname(participant.getUserNickname())
                        .profileImageUrl(participant.getUserProfileImageUrl())
                        .defaultProfileImageType(participant.getUserDefaultProfileImageType())
                        .build());
                    return;
                }
                roomGuestList.add(RoomGuest.builder()
                    .id(participant.getUserId())
                    .nickname(participant.getUserNickname())
                    .profileImageUrl(participant.getUserProfileImageUrl())
                    .defaultProfileImageType(participant.getUserDefaultProfileImageType())
                    .build());
            });
    }
}
