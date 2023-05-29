package toy.bookchat.bookchat.domain.scrap.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.domain.scrap.Scrap;
import toy.bookchat.bookchat.domain.scrap.repository.ScrapRepository;
import toy.bookchat.bookchat.domain.scrap.service.dto.request.CreateScrapRequest;
import toy.bookchat.bookchat.domain.scrap.service.dto.response.ScrapResponse;

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

    @Test
    void 스크랩_조회_성공() throws Exception {
        ScrapResponse scrapResponse1 = ScrapResponse.builder()
            .scrapId(1L)
            .scrapContent("content1")
            .build();
        ScrapResponse scrapResponse2 = ScrapResponse.builder()
            .scrapId(2L)
            .scrapContent("content2")
            .build();
        ScrapResponse scrapResponse3 = ScrapResponse.builder()
            .scrapId(3L)
            .scrapContent("content3")
            .build();
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by("id").ascending());
        Slice<ScrapResponse> slice = new SliceImpl<>(
            List.of(scrapResponse1, scrapResponse2, scrapResponse3), pageRequest, true);

        when(scrapRepository.findScraps(any(), any(), any(), any())).thenReturn(slice);

        scrapService.getScraps(1L, 1L, pageRequest, 1L);

        verify(scrapRepository).findScraps(any(), any(), any(), any());
    }

    @Test
    void 스크랩_삭제_성공() throws Exception {

        when(bookShelfRepository.findByIdAndUserId(any(), any())).thenReturn(
            ScrapServiceTestFixture.mockBookShelf());
        when(scrapRepository.findAllById(any())).thenReturn(ScrapServiceTestFixture.getScrapList());

        scrapService.deleteScraps(1L, List.of(1L, 2L, 3L), 1L);

        verify(scrapRepository).deleteAllInBatch(any());
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

        private static List<Scrap> getScrapList() {
            Scrap scrap1 = Scrap.builder()
                .scrapContent("scrap content1")
                .build();
            Scrap scrap2 = Scrap.builder()
                .scrapContent("scrap content2")
                .build();

            return List.of(scrap1, scrap2);
        }
    }
}