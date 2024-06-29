package toy.bookchat.bookchat.domain.participant.service.dto.response;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import toy.bookchat.bookchat.domain.chatroom.ChatRoomEntity;
import toy.bookchat.bookchat.domain.participant.ParticipantEntity;
import toy.bookchat.bookchat.domain.participant.service.dto.RoomGuest;
import toy.bookchat.bookchat.domain.participant.service.dto.RoomHost;
import toy.bookchat.bookchat.domain.participant.service.dto.RoomSubHost;
import toy.bookchat.bookchat.domain.user.UserEntity;

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

    @Builder
    private ChatRoomDetails(Integer roomSize, List<String> roomTags, String roomName, String bookTitle, String bookCoverImageUrl, List<String> bookAuthors, RoomHost roomHost,
        List<RoomSubHost> roomSubHostList, List<RoomGuest> roomGuestList) {
        this.roomSize = roomSize;
        this.roomTags = roomTags;
        this.roomName = roomName;
        this.bookTitle = bookTitle;
        this.bookCoverImageUrl = bookCoverImageUrl;
        this.bookAuthors = bookAuthors;
        this.roomHost = roomHost;
        this.roomSubHostList = roomSubHostList;
        this.roomGuestList = roomGuestList;
    }

    public static ChatRoomDetails from(List<ParticipantEntity> participantEntities, List<String> roomTags) {
        UserEntity host = getHost(participantEntities);
        RoomHost roomHost = RoomHost.builder()
            .id(host.getId())
            .nickname(host.getNickname())
            .profileImageUrl(host.getProfileImageUrl())
            .defaultProfileImageType(host.getDefaultProfileImageType())
            .build();
        List<RoomSubHost> roomSubHostList = new ArrayList<>();
        List<RoomGuest> roomGuestList = new ArrayList<>();
        fillParticipantsResponse(participantEntities, host, roomSubHostList, roomGuestList);
        ChatRoomEntity chatRoomEntity = getChatRoom(participantEntities);

        return new ChatRoomDetails(chatRoomEntity.getRoomSize(), roomTags, chatRoomEntity.getRoomName(), chatRoomEntity.getBookTitle(), chatRoomEntity.getBookCoverImageUrl(),
            chatRoomEntity.getBookAuthors(), roomHost,
            roomSubHostList, roomGuestList);
    }

    private static ChatRoomEntity getChatRoom(List<ParticipantEntity> participantEntities) {
        return participantEntities.get(0).getChatRoomEntity();
    }

    private static UserEntity getHost(List<ParticipantEntity> participantEntities) {
        return participantEntities.get(0).getChatRoomEntity().getHost();
    }

    private static void fillParticipantsResponse(List<ParticipantEntity> participantEntities, UserEntity host,
        List<RoomSubHost> roomSubHostList, List<RoomGuest> roomGuestList) {
        participantEntities.stream().filter(participant -> !participant.getUserEntity().equals(host))
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
