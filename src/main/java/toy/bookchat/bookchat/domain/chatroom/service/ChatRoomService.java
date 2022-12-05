package toy.bookchat.bookchat.domain.chatroom.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.domain.chatroom.service.dto.request.CreateChatRoomRequest;
import toy.bookchat.bookchat.domain.chatroomhashtag.ChatRoomHashTag;
import toy.bookchat.bookchat.domain.chatroomhashtag.repository.ChatRoomHashTagRepository;
import toy.bookchat.bookchat.domain.chatroomhost.ChatRoomHost;
import toy.bookchat.bookchat.domain.chatroomhost.repository.ChatRoomHostRepository;
import toy.bookchat.bookchat.domain.hashtag.HashTag;
import toy.bookchat.bookchat.domain.hashtag.repository.HashTagRepository;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.exception.user.UserNotFoundException;

@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomHostRepository chatRoomHostRepository;
    private final HashTagRepository hashTagRepository;
    private final ChatRoomHashTagRepository chatRoomHashTagRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public ChatRoomService(
        ChatRoomRepository chatRoomRepository,
        ChatRoomHostRepository chatRoomHostRepository,
        HashTagRepository hashTagRepository,
        ChatRoomHashTagRepository chatRoomHashTagRepository,
        BookRepository bookRepository,
        UserRepository userRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomHostRepository = chatRoomHostRepository;
        this.hashTagRepository = hashTagRepository;
        this.chatRoomHashTagRepository = chatRoomHashTagRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void createChatRoom(CreateChatRoomRequest createChatRoomRequest, Long userId) {
        Book book = bookRepository.findByIsbnAndPublishAt(createChatRoomRequest.getIsbn(),
                createChatRoomRequest.getPublishAt())
            .orElseGet(() -> bookRepository.save(createChatRoomRequest.createBook()));

        User mainHost = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        ChatRoomHost chatRoomHost = saveChatRoomHost(mainHost);

        ChatRoom chatRoom = saveChatRoom(createChatRoomRequest, book,
            chatRoomHost);

        registerHashTagOnChatRoom(createChatRoomRequest, chatRoom);
    }

    private ChatRoom saveChatRoom(CreateChatRoomRequest createChatRoomRequest, Book book,
        ChatRoomHost chatRoomHost) {
        ChatRoom chatRoom = createChatRoomRequest.makeChatRoom(book, chatRoomHost);
        chatRoomRepository.save(chatRoom);
        return chatRoom;
    }

    private ChatRoomHost saveChatRoomHost(User mainHost) {
        ChatRoomHost chatRoomHost = ChatRoomHost.builder()
            .mainHost(mainHost)
            .build();
        chatRoomHostRepository.save(chatRoomHost);
        return chatRoomHost;
    }

    private void registerHashTagOnChatRoom(CreateChatRoomRequest createChatRoomRequest,
        ChatRoom chatRoom) {
        createChatRoomRequest.getHashTags().stream()
            .map(tagName -> hashTagRepository.findByTagName(tagName)
                .orElseGet(() -> hashTagRepository.save(HashTag.of(tagName))))
            .forEach(
                hashTag -> chatRoomHashTagRepository.save(ChatRoomHashTag.of(chatRoom, hashTag)));
    }
}
