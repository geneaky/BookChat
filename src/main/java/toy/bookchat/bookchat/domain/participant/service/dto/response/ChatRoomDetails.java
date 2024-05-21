package toy.bookchat.bookchat.domain.participant.service.dto.response;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.service.dto.RoomGuest;
import toy.bookchat.bookchat.domain.participant.service.dto.RoomHost;
import toy.bookchat.bookchat.domain.participant.service.dto.RoomSubHost;
import toy.bookchat.bookchat.domain.user.User;

@Getter
@EqualsAndHashCode
public class ChatRoomDetails {

    private Integer roomSize;
    private List<String> roomTags;
    private String roomName;
    private String bookTitle;
    private String bookCoverImageUrl;
    private List<String> bookAuthors;
    private RoomHost roomHost;
    private List<RoomSubHost> roomSubHostList;
    private List<RoomGuest> roomGuestList;
    private Boolean isBanned;
    private Boolean isExploded;

    @Builder
    private ChatRoomDetails(Integer roomSize, List<String> roomTags, String roomName,
        String bookTitle,
        String bookCoverImageUrl, List<String> bookAuthors, RoomHost roomHost,
        List<RoomSubHost> roomSubHostList,
        List<RoomGuest> roomGuestList, Boolean isBanned, Boolean isExploded) {
        this.roomSize = roomSize;
        this.roomTags = roomTags;
        this.roomName = roomName;
        this.bookTitle = bookTitle;
        this.bookCoverImageUrl = bookCoverImageUrl;
        this.bookAuthors = bookAuthors;
        this.roomHost = roomHost;
        this.roomSubHostList = roomSubHostList;
        this.roomGuestList = roomGuestList;
        this.isBanned = isBanned;
        this.isExploded = isExploded;
    }

    public static ChatRoomDetails from(List<Participant> participants, List<String> roomTags, Boolean isBanned) {
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
        ChatRoom chatRoom = getChatRoom(participants);

        //isBanned 정보 추가
        return new ChatRoomDetails(chatRoom.getRoomSize(), roomTags, chatRoom.getRoomName(), chatRoom.getBookTitle(), chatRoom.getBookCoverImageUrl(), chatRoom.getBookAuthors(), roomHost,
            roomSubHostList, roomGuestList, isBanned, chatRoom.getIsDeleted());
    }

    private static ChatRoom getChatRoom(List<Participant> participants) {
        return participants.get(0).getChatRoom();
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
