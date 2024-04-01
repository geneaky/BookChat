package toy.bookchat.bookchat.domain.chat.repository.query;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import toy.bookchat.bookchat.domain.chat.Chat;

public interface ChatQueryRepository {

    Slice<Chat> getChatRoomChats(Long roomId, Long postCursorId, Pageable pageable, Long userId);

    Optional<Chat> getUserChatRoomChat(Long chatId, Long userId);
}
