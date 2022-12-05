package toy.bookchat.bookchat.domain.chatroom.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.BookRequest;
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

    private static BookRequest getBookRequest() {
        return BookRequest.builder()
            .isbn("123124")
            .title("effective java")
            .authors(List.of("joshua"))
            .publishAt(LocalDate.now())
            .build();
    }

    private static CreateChatRoomRequest getCreateChatRoomRequest(BookRequest bookRequest) {
        return CreateChatRoomRequest.builder()
            .roomSize(3)
            .roomName("test room")
            .hashTags(List.of("java", "joshua"))
            .bookRequest(bookRequest)
            .build();
    }

    @Test
    void 채팅방_생성_성공() throws Exception {
        BookRequest bookRequest = getBookRequest();
        CreateChatRoomRequest createChatRoomRequest = getCreateChatRoomRequest(bookRequest);

        when(bookRepository.findByIsbnAndPublishAt(any(), any())).thenReturn(
            Optional.ofNullable(mock(Book.class)));
        when(userRepository.findById(any())).thenReturn(Optional.of(mock(User.class)));

        chatRoomService.createChatRoom(createChatRoomRequest, 1L);

        verify(chatRoomRepository).save(any());
        verify(chatRoomHostRepository).save(any());
        verify(hashTagRepository, times(2)).save(any());
        verify(chatRoomHashTagRepository, times(2)).save(any());
    }

    @Test
    void 등록되지_않은_책으로_채팅방_생성시_책을_등록_후_생성한다() throws Exception {
        BookRequest bookRequest = getBookRequest();
        CreateChatRoomRequest createChatRoomRequest = getCreateChatRoomRequest(bookRequest);

        when(userRepository.findById(any())).thenReturn(Optional.of(mock(User.class)));

        chatRoomService.createChatRoom(createChatRoomRequest, 1L);

        verify(bookRepository).save(any());
        verify(chatRoomRepository).save(any());
        verify(chatRoomHostRepository).save(any());
        verify(hashTagRepository, times(2)).save(any());
        verify(chatRoomHashTagRepository, times(2)).save(any());
    }
}