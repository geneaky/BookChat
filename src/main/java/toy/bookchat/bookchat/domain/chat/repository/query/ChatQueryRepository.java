package toy.bookchat.bookchat.domain.chat.repository.query;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import toy.bookchat.bookchat.domain.chat.ChatEntity;

public interface ChatQueryRepository {

    Slice<ChatEntity> getChatRoomChats(Long roomId, Long postCursorId, Pageable pageable, Long userId);

    Optional<ChatEntity> getUserChatRoomChat(Long chatId, Long userId);
}
