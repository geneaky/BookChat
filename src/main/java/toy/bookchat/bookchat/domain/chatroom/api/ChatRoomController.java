package toy.bookchat.bookchat.domain.chatroom.api;

import java.util.Optional;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import toy.bookchat.bookchat.domain.chatroom.service.ChatRoomService;
import toy.bookchat.bookchat.domain.chatroom.service.dto.request.CreateChatRoomRequest;
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
    public void createChatRoom(@Valid @RequestPart CreateChatRoomRequest createChatRoomRequest,
        @RequestPart Optional<MultipartFile> chatRoomImage,
        @UserPayload TokenPayload tokenPayload) {
        chatRoomService.createChatRoom(createChatRoomRequest, chatRoomImage,
            tokenPayload.getUserId());
    }
}
