package toy.bookchat.bookchat.domain.agony.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.agony.AgonyTitleAndColorCode;
import toy.bookchat.bookchat.domain.agonyrecord.service.AgonyRecordCleaner;
import toy.bookchat.bookchat.domain.bookshelf.service.BookShelfReader;

@ExtendWith(MockitoExtension.class)
class AgonyServiceTest {

    @Mock
    private BookShelfReader bookShelfReader;
    @Mock
    private AgonyAppender agonyAppender;
    @Mock
    private AgonyReader agonyReader;
    @Mock
    private AgonyCleaner agonyCleaner;
    @Mock
    private AgonyManager agonyManager;
    @Mock
    private AgonyRecordCleaner agonyRecordCleaner;
    @InjectMocks
    private AgonyService agonyService;

    @Test
    void 고민_생성_성공() throws Exception {
        Agony agony = Agony.builder()
            .title("title")
            .hexColorCode("color")
            .build();

        agonyService.storeBookShelfAgony(agony, 1L, 1L);

        verify(agonyAppender).append(any(), any());
    }

    @Test
    void 서재에_등록된_고민_단_건_조회_성공() throws Exception {
        agonyService.searchAgony(1L, 1L, 1L);
        verify(agonyReader).readAgony(any(), any(), any());
    }

    @Test
    void 사용자_서재에_등록된_고민_조회_성공() throws Exception {
        PageRequest pageRequest = PageRequest.of(0, 1, Sort.by("id").descending());
        agonyService.searchSliceOfAgonies(1L, 1L, pageRequest, 1L);

        verify(agonyReader).readSlicedAgony(any(), any(), any(), any());
    }

    @Test
    void 고민폴더_삭제_성공() throws Exception {
        agonyService.deleteAgony(1L, List.of(1L, 2L, 3L), 1L);

        verify(agonyCleaner).clean(any(), any(), any());
        verify(agonyRecordCleaner).clean(any(), any(), any());
    }

    @Test
    void 고민폴더_수정_성공() throws Exception {
        AgonyTitleAndColorCode agonyTitleAndColorCode = AgonyTitleAndColorCode.builder()
            .title("폴더 이름 바꾸기")
            .hexColorCode("보라색")
            .build();
        agonyService.reviseAgony(1L, 1L, 1L, agonyTitleAndColorCode);

        verify(agonyManager).modify(any(), any(), any(), any());
    }
}