package toy.bookchat.bookchat.domain.book.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.util.UriComponentsBuilder;
import toy.bookchat.bookchat.domain.book.dto.BookDto;

@RestClientTest(value = BookSearchService.class)
public class BookSearchServiceTest {

    @Autowired
    private BookSearchService bookSearchService;
    @Autowired
    private MockRestServiceServer mockServer;

    @Value("${book.api.uri}")
    private String apiUri;

    @Test
    public void isbn으로_외부api_호출_json결과_응답() throws Exception {
        String isbn = "8996991341 9788996991342";
        String title = "미움받을 용기";
        String[] author = {"기시미 이치로", "고가 후미타케"};
        String bookCoverImageUrl = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1467038%3Ftimestamp%3D20220505201559";
        String result = "{\"documents\":[{\"authors\":[\"기시미 이치로\",\"고가 후미타케\"],\"contents\":\"어릴 때부터 성격이 어두워 사람들과 쉽게 친해지지 못하는 사람이 있다. 언제까지 다른 사람들과의 관계 때문에 전전긍긍하며 살아야 할지, 그는 오늘도 고민이다. 이런 그의 고민에 “인간은 변할 수 있고, 누구나 행복해 질 수 있다. 단 그러기 위해서는 ‘용기’가 필요하다”고 말한 철학자가 있다. 바로 프로이트, 융과 함께 ‘심리학의 3대 거장’으로 일컬어지고 있는 알프레드 아들러다.  『미움받을 용기』는 아들러 심리학에 관한 일본의 1인자 철학자\",\"datetime\":\"2014-11-17T00:00:00.000+09:00\",\"isbn\":\"8996991341 9788996991342\",\"price\":14900,\"publisher\":\"인플루엔셜\",\"sale_price\":13410,\"status\":\"정상판매\",\"thumbnail\":\"https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1467038%3Ftimestamp%3D20220505201559\",\"title\":\"미움받을 용기\",\"translators\":[\"전경아\"],\"url\":\"https://search.daum.net/search?w=bookpage\\u0026bookId=1467038\\u0026q=%EB%AF%B8%EC%9B%80%EB%B0%9B%EC%9D%84+%EC%9A%A9%EA%B8%B0\"}],\"meta\":{\"is_end\":true,\"pageable_count\":1,\"total_count\":1}}";

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
            .fromUri(URI.create(apiUri + "isbn"))
            .queryParam("query", isbn);

        System.out.println(uriComponentsBuilder.toUriString());
        mockServer.expect(requestTo(uriComponentsBuilder.build().toUri()))
            .andRespond(withSuccess(result, MediaType.APPLICATION_JSON));

        BookDto bookDto = bookSearchService.searchByIsbn(isbn);

        assertThat(bookDto.getIsbn()).isEqualTo(isbn);
        assertThat(bookDto.getTitle()).isEqualTo(title);
        assertThat(bookDto.getAuthor()).isEqualTo(author);
        assertThat(bookDto.getBookCoverImageUrl()).isEqualTo(bookCoverImageUrl);
    }
}
