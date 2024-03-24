package toy.bookchat.bookchat.domain.chatroom.api;

import java.net.URI;
import javax.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import toy.bookchat.bookchat.domain.chatroom.repository.query.dto.response.ChatRoomsResponseSlice;
import toy.bookchat.bookchat.domain.chatroom.repository.query.dto.response.UserChatRoomsResponseSlice;
import toy.bookchat.bookchat.domain.chatroom.service.ChatRoomService;
import toy.bookchat.bookchat.domain.chatroom.service.dto.request.ChatRoomRequest;
import toy.bookchat.bookchat.domain.chatroom.service.dto.request.CreateChatRoomRequest;
import toy.bookchat.bookchat.domain.chatroom.service.dto.request.ReviseChatRoomRequest;
import toy.bookchat.bookchat.domain.chatroom.service.dto.response.CreatedChatRoomDto;
import toy.bookchat.bookchat.domain.participant.service.dto.response.ChatRoomDetails;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPayload;

@RestController
@RequestMapping("/v1/api")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    public ChatRoomController(
        ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    @PostMapping("/chatrooms")
    public ResponseEntity<Void> createChatRoom(@Valid @RequestPart CreateChatRoomRequest createChatRoomRequest, @RequestPart(required = false) MultipartFile chatRoomImage,
        @UserPayload TokenPayload tokenPayload) {
        CreatedChatRoomDto createdChatRoomDto = chatRoomService.createChatRoom(createChatRoomRequest, chatRoomImage, tokenPayload.getUserId());

        return ResponseEntity.status(HttpStatus.CREATED)
            .headers(hs -> hs.setLocation(URI.create("/topic/" + createdChatRoomDto.getRoomSid())))
            .headers(hs -> hs.set("RoomId", createdChatRoomDto.getRoomId()))
            .headers(hs -> hs.set("RoomImageUri", createdChatRoomDto.getRoomImageUri()))
            .build();
    }

    @GetMapping("/users/chatrooms")
    public UserChatRoomsResponseSlice getUserChatRooms(Long bookId, Long postCursorId, Pageable pageable, @UserPayload TokenPayload tokenPayload) {
        return chatRoomService.getUserChatRooms(bookId, postCursorId, pageable, tokenPayload.getUserId());
    }

    @GetMapping("/chatrooms")
    public ChatRoomsResponseSlice getChatRooms(@ModelAttribute ChatRoomRequest chatRoomRequest, Pageable pageable) {
        chatRoomRequest.validate();
        return chatRoomService.getChatRooms(chatRoomRequest, pageable);
    }

    @GetMapping("/chatrooms/{roomId}")
    public ChatRoomDetails getChatRoomDetails(@PathVariable Long roomId, @UserPayload TokenPayload tokenPayload) {
        return chatRoomService.getChatRoomDetails(roomId, tokenPayload.getUserId());
    }

    @PostMapping("/chatrooms/{roomId}")
    public void reviseChatRoom(@Valid @RequestPart ReviseChatRoomRequest reviseChatRoomRequest, @RequestPart(required = false) MultipartFile chatRoomImage, @UserPayload TokenPayload tokenPayload) {
        chatRoomService.reviseChatRoom(reviseChatRoomRequest, chatRoomImage,
            tokenPayload.getUserId());
    }

    @PostMapping("/enter/chatrooms/{roomId}")
    public void enterChatRoom(@UserPayload TokenPayload tokenPayload, @PathVariable Long roomId) {
        chatRoomService.enterChatRoom(tokenPayload.getUserId(), roomId);
    }

    @DeleteMapping("/leave/chatrooms/{roomId}")
    public void leaveChatRoom(@UserPayload TokenPayload tokenPayload, @PathVariable Long roomId) {
        chatRoomService.exitChatRoom(tokenPayload.getUserId(), roomId);
    }

}
