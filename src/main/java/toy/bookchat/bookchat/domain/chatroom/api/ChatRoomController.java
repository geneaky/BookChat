package toy.bookchat.bookchat.domain.chatroom.api;

import java.net.URI;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.ChatRoomResponse;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.ChatRoomsResponseSlice;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.UserChatRoomResponse;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.UserChatRoomsResponseSlice;
import toy.bookchat.bookchat.domain.chatroom.UserChatRoomDetail;
import toy.bookchat.bookchat.domain.chatroom.api.v1.request.ChatRoomRequest;
import toy.bookchat.bookchat.domain.chatroom.api.v1.request.CreateChatRoomRequest;
import toy.bookchat.bookchat.domain.chatroom.api.v1.request.ReviseChatRoomRequest;
import toy.bookchat.bookchat.domain.chatroom.api.v1.response.ChatRoomDetails;
import toy.bookchat.bookchat.domain.chatroom.api.v1.response.UserChatRoomDetailResponse;
import toy.bookchat.bookchat.domain.chatroom.service.ChatRoomService;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPayload;

@RequiredArgsConstructor

@RestController
@RequestMapping("/v1/api")
public class ChatRoomController {

  private final ChatRoomService chatRoomService;

  @PostMapping("/chatrooms")
  public ResponseEntity<Void> createChatRoom(@Valid @RequestPart CreateChatRoomRequest createChatRoomRequest,
      @RequestPart(required = false) MultipartFile chatRoomImage, @UserPayload TokenPayload tokenPayload) {
    Long roomId = chatRoomService.createChatRoom(createChatRoomRequest.toChatRoom(), createChatRoomRequest.toHashTags(),
        createChatRoomRequest.toBook(), chatRoomImage, tokenPayload.getUserId());

    return ResponseEntity.status(HttpStatus.CREATED)
        .headers(hs -> hs.setLocation(URI.create("/v1/api/chatrooms/" + roomId)))
        .build();
  }

  @GetMapping("/users/chatrooms")
  public UserChatRoomsResponseSlice getUserChatRooms(Long bookId, Long postCursorId, Pageable pageable,
      @UserPayload TokenPayload tokenPayload) {
    Slice<UserChatRoomResponse> slicedUserChatRoomResponse = chatRoomService.getUserChatRooms(bookId, postCursorId,
        pageable, tokenPayload.getUserId());
    return UserChatRoomsResponseSlice.of(slicedUserChatRoomResponse);
  }

  @GetMapping("/users/chatrooms/{roomId}")
  public UserChatRoomDetailResponse getUserChatRoomDetails(@PathVariable Long roomId,
      @UserPayload TokenPayload tokenPayload) {
    UserChatRoomDetail userChatRoomDetail = chatRoomService.getUserChatRoomDetails(roomId, tokenPayload.getUserId());
    return UserChatRoomDetailResponse.from(userChatRoomDetail);
  }

  @GetMapping("/chatrooms")
  public ChatRoomsResponseSlice getChatRooms(@ModelAttribute ChatRoomRequest chatRoomRequest, Pageable pageable) {
    chatRoomRequest.validate();
    Slice<ChatRoomResponse> slicedChatRoomResponse = chatRoomService.getChatRooms(chatRoomRequest, pageable);

    return ChatRoomsResponseSlice.of(slicedChatRoomResponse);
  }

  @GetMapping("/chatrooms/{roomId}")
  public ChatRoomDetails getChatRoomDetails(@PathVariable Long roomId, @UserPayload TokenPayload tokenPayload) {
    return chatRoomService.getChatRoomDetails(roomId, tokenPayload.getUserId());
  }

  @PostMapping("/chatrooms/{roomId}")
  public void reviseChatRoom(@Valid @RequestPart ReviseChatRoomRequest reviseChatRoomRequest,
      @RequestPart(required = false) MultipartFile chatRoomImage, @UserPayload TokenPayload tokenPayload) {
    chatRoomService.reviseChatRoom(reviseChatRoomRequest, chatRoomImage, tokenPayload.getUserId());
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
