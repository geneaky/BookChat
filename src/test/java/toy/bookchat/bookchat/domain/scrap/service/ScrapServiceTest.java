package toy.bookchat.bookchat.domain.scrap.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.domain.scrap.repository.ScrapRepository;
import toy.bookchat.bookchat.domain.scrap.service.dto.request.CreateScrapRequest;

@ExtendWith(MockitoExtension.class)
class ScrapServiceTest {

    @Mock
    private ScrapRepository scrapRepository;
    @Mock
    private BookShelfRepository bookShelfRepository;
    @InjectMocks
    private ScrapService scrapService;

    @Test
    void 스크랩_저장_성공() throws Exception {
        CreateScrapRequest createScrapRequest = ScrapServiceTestFixture.create();

        when(bookShelfRepository.findByIdAndUserId(any(), any())).thenReturn(
            ScrapServiceTestFixture.mockBookShelf());
        
        scrapService.scrap(createScrapRequest, 928L);

        verify(scrapRepository).save(any());
    }

    private static class ScrapServiceTestFixture {

        private static CreateScrapRequest create() {
            return CreateScrapRequest.builder()
                .bookShelfId(409L)
                .scrapContent("스크랩 내용")
                .build();
        }

        private static Optional<BookShelf> mockBookShelf() {
            return Optional.of(BookShelf.builder().build());
        }
    }
}