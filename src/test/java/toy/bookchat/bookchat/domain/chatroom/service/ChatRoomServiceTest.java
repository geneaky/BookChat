package toy.bookchat.bookchat.domain.chatroom.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.domain.chatroom.service.dto.request.CreateChatRoomRequest;
import toy.bookchat.bookchat.domain.chatroomhashtag.repository.ChatRoomHashTagRepository;
import toy.bookchat.bookchat.domain.chatroomhost.repository.ChatRoomHostRepository;
import toy.bookchat.bookchat.domain.hashtag.repository.HashTagRepository;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

    @Mock
    ChatRoomRepository chatRoomRepository;
    @Mock
    ChatRoomHostRepository chatRoomHostRepository;
    @Mock
    HashTagRepository hashTagRepository;
    @Mock
    ChatRoomHashTagRepository chatRoomHashTagRepository;
    @Mock
    BookRepository bookRepository;
    @Mock
    UserRepository userRepository;

    @InjectMocks
    ChatRoomService chatRoomService;

    @Test
    void 채팅방_생성_성공() throws Exception {
        when(bookRepository.findByIsbnAndPublishAt(any(), any())).thenReturn(
            Optional.ofNullable(mock(Book.class)));
        when(userRepository.findById(any())).thenReturn(Optional.of(mock(User.class)));

        chatRoomService.createChatRoom(mock(CreateChatRoomRequest.class), 1L);

        verify(chatRoomRepository).save(any());
        verify(chatRoomHostRepository).save(any());
        verify(hashTagRepository).save(any());
        verify(chatRoomHashTagRepository).save(any());
    }

    @Test
    void 등록되지_않은_책으로_채팅방_생성시_책을_등록_후_생성한다() throws Exception {
        when(userRepository.findById(any())).thenReturn(Optional.of(mock(User.class)));

        chatRoomService.createChatRoom(mock(CreateChatRoomRequest.class), 1L);

        verify(bookRepository).save(any());
        verify(chatRoomRepository).save(any());
        verify(chatRoomHostRepository).save(any());
        verify(hashTagRepository).save(any());
        verify(chatRoomHashTagRepository).save(any());
    }
}