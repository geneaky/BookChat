package toy.bookchat.bookchat.domain.chatroom.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.domain.chatroom.service.dto.request.CreateChatRoomRequest;
import toy.bookchat.bookchat.domain.chatroomhost.ChatRoomHost;
import toy.bookchat.bookchat.domain.chatroomhost.repository.ChatRoomHostRepository;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.exception.book.BookNotFoundException;
import toy.bookchat.bookchat.exception.user.UserNotFoundException;

@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomHostRepository chatRoomHostRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public ChatRoomService(
        ChatRoomRepository chatRoomRepository,
        ChatRoomHostRepository chatRoomHostRepository,
        BookRepository bookRepository,
        UserRepository userRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomHostRepository = chatRoomHostRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void createChatRoom(CreateChatRoomRequest createChatRoomRequest, Long userId) {
        Book book = bookRepository.findByIsbnAndPublishAt(createChatRoomRequest.getIsbn(),
            createChatRoomRequest.getPublishAt()).orElseThrow(BookNotFoundException::new);

        User mainHost = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        ChatRoomHost chatRoomHost = ChatRoomHost.builder()
            .mainHost(mainHost)
            .build();
        chatRoomHostRepository.save(chatRoomHost);

        ChatRoom chatRoom = createChatRoomRequest.makeChatRoom(book, chatRoomHost);
        chatRoomRepository.save(chatRoom);
    }
}
