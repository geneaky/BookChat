package toy.bookchat.bookchat.domain.book.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.util.UriComponentsBuilder;
import toy.bookchat.bookchat.domain.book.dto.BookSearchRequestDto;
import toy.bookchat.bookchat.domain.book.dto.BookSearchResponseDto;

@RestClientTest(value = BookSearchService.class)
public class BookSearchServiceTest {

    private final String isbn = "8996991341 9788996991342";
    private final String title = "미움받을 용기";
    private final String[] authors = {"기시미 이치로", "고가 후미타케"};
    private final String publisher = "인플루엔셜";
    private final String bookCoverImageUrl = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1467038%3Ftimestamp%3D20220505201559";
    private final String result = "{\"documents\":[{\"authors\":[\"기시미 이치로\",\"고가 후미타케\"],\"contents\":\"어릴 때부터 성격이 어두워 사람들과 쉽게 친해지지 못하는 사람이 있다. 언제까지 다른 사람들과의 관계 때문에 전전긍긍하며 살아야 할지, 그는 오늘도 고민이다. 이런 그의 고민에 “인간은 변할 수 있고, 누구나 행복해 질 수 있다. 단 그러기 위해서는 ‘용기’가 필요하다”고 말한 철학자가 있다. 바로 프로이트, 융과 함께 ‘심리학의 3대 거장’으로 일컬어지고 있는 알프레드 아들러다.  『미움받을 용기』는 아들러 심리학에 관한 일본의 1인자 철학자\",\"datetime\":\"2014-11-17T00:00:00.000+09:00\",\"isbn\":\"8996991341 9788996991342\",\"price\":14900,\"publisher\":\"인플루엔셜\",\"sale_price\":13410,\"status\":\"정상판매\",\"thumbnail\":\"https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1467038%3Ftimestamp%3D20220505201559\",\"title\":\"미움받을 용기\",\"translators\":[\"전경아\"],\"url\":\"https://search.daum.net/search?w=bookpage\\u0026bookId=1467038\\u0026q=%EB%AF%B8%EC%9B%80%EB%B0%9B%EC%9D%84+%EC%9A%A9%EA%B8%B0\"}],\"meta\":{\"is_end\":true,\"pageable_count\":1,\"total_count\":1}}";
    @Autowired
    private BookSearchService bookSearchService;
    @Autowired
    private MockRestServiceServer mockServer;
    @Value("${book.api.uri}")
    private String apiUri;

    private BookSearchRequestDto getBookSearchRequestDto(String isbn, String title, String author,
        Integer size, Integer page, BookSearchSort bookSearchSort) {
        return BookSearchRequestDto.builder()
            .isbn(isbn)
            .title(title)
            .author(author)
            .size(size)
            .page(page)
            .bookSearchSort(bookSearchSort)
            .build();
    }

    @Test
    public void isbn으로_외부api_호출_json결과_응답() throws Exception {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
            .fromUri(URI.create(apiUri + "isbn"))
            .queryParam("query", isbn);

        BookSearchRequestDto bookSearchRequestDto = getBookSearchRequestDto(isbn,
            title, authors[0], null, null, null);

        mockServer.expect(
                requestTo(uriComponentsBuilder.build().encode(StandardCharsets.UTF_8).toUri()))
            .andRespond(withSuccess(result, MediaType.APPLICATION_JSON));

        BookSearchResponseDto bookSearchResponseDto = bookSearchService.searchByIsbn(
            bookSearchRequestDto);

        assertThat(bookSearchResponseDto.getBookDtos().get(0).getIsbn()).isEqualTo(isbn);
        assertThat(bookSearchResponseDto.getBookDtos().get(0).getTitle()).isEqualTo(title);
        assertThat(bookSearchResponseDto.getBookDtos().get(0).getPublisher()).isEqualTo(publisher);
        assertThat(bookSearchResponseDto.getBookDtos().get(0).getAuthor()).isEqualTo(
            List.of(authors));
        assertThat(bookSearchResponseDto.getBookDtos().get(0).getBookCoverImageUrl()).isEqualTo(
            bookCoverImageUrl);
    }

    @Test
    public void title로_외부api_호출_json결과_응답() throws Exception {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
            .fromUri(URI.create(apiUri + "title"))
            .queryParam("query", title);

        BookSearchRequestDto bookSearchRequestDto = getBookSearchRequestDto(isbn,
            title, authors[0], null, null, null);

        mockServer.expect(
                requestTo(uriComponentsBuilder.build().encode(StandardCharsets.UTF_8).toUri()))
            .andRespond(withSuccess(result, MediaType.APPLICATION_JSON));

        BookSearchResponseDto bookSearchResponseDto = bookSearchService.searchByTitle(
            bookSearchRequestDto);

        assertThat(bookSearchResponseDto.getBookDtos().get(0).getIsbn()).isEqualTo(isbn);
        assertThat(bookSearchResponseDto.getBookDtos().get(0).getTitle()).isEqualTo(title);
        assertThat(bookSearchResponseDto.getBookDtos().get(0).getPublisher()).isEqualTo(publisher);
        assertThat(bookSearchResponseDto.getBookDtos().get(0).getAuthor()).isEqualTo(
            List.of(authors));
        assertThat(bookSearchResponseDto.getBookDtos().get(0).getBookCoverImageUrl()).isEqualTo(
            bookCoverImageUrl);
    }

    @Test
    public void author로_외부api_호출_json결과_응답() throws Exception {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
            .fromUri(URI.create(apiUri + "author"))
            .queryParam("query", authors[0]);

        BookSearchRequestDto bookSearchRequestDto = getBookSearchRequestDto(isbn,
            title, authors[0], null, null, null);

        mockServer.expect(
                requestTo(uriComponentsBuilder.build().encode(StandardCharsets.UTF_8).toUri()))
            .andRespond(withSuccess(result, MediaType.APPLICATION_JSON));

        BookSearchResponseDto bookSearchResponseDto = bookSearchService.searchByAuthor(
            bookSearchRequestDto);

        assertThat(bookSearchResponseDto.getBookDtos().get(0).getIsbn()).isEqualTo(isbn);
        assertThat(bookSearchResponseDto.getBookDtos().get(0).getTitle()).isEqualTo(title);
        assertThat(bookSearchResponseDto.getBookDtos().get(0).getPublisher()).isEqualTo(publisher);
        assertThat(bookSearchResponseDto.getBookDtos().get(0).getAuthor()).isEqualTo(
            List.of(authors));
        assertThat(bookSearchResponseDto.getBookDtos().get(0).getBookCoverImageUrl()).isEqualTo(
            bookCoverImageUrl);
    }

    //sort page size 설정
    //page 1 ~ 50 사이의 값 default = 1 결과 페이지 번호
    //size 1 ~ 50 사이의 값 default = 10 한 페이지에 보여줄 문서 수
    //sort 결과 문서 정렬 방식, accuracy(정확도순), latest(발간일순), default = accuracy
    //total_count = 문서수 (size)
    //pageable_count = 페이지 수 (page)
    //is_end 마지막 페이지 여부
    //
    @Test
    public void title로_외부api_호출시_size지정() throws Exception {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
            .fromUri(URI.create(apiUri + "title"))
            .queryParam("query", "이펙티브")
            .queryParam("size", 5);

        BookSearchRequestDto bookSearchRequestDto = getBookSearchRequestDto(null,
            "이펙티브", null, 5, null, null);

        String result =
            "{\"documents\":[{\"authors\":[\"빌 와그너\"],\"contents\":\"C#은 전통적인 .NET 기반 개발에서 유니티 게임 엔진으로 개발 영역을 확대하면서 더욱 주목받고 있다. 또한 자마린으로 다양한 모바일 플랫폼에 대응할 수 있어 수요가 계속 늘고 있다. 이에 이 책은 소프트웨어 개발자가 C#을 더 효율적으로 사용할 수 있도록 다양한 팁과 실용적인 방법을 제공한다.  저자는 C#의 힘을 온전히 활용하여 효율적이고 성능이 뛰어난 코드를 작성하는 방법 50가지를 알려준다. 탁월한 C# 경험을 바탕으로 리소스 관리부터\",\"datetime\":\"2017-11-06T00:00:00.000+09:00\",\"isbn\":\"1162240032 9791162240038\",\"price\":25000,\"publisher\":\"한빛미디어\",\"sale_price\":22500,\"status\":\"정상판매\",\"thumbnail\":\"https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1611826%3Ftimestamp%3D20220709154251\",\"title\":\"Effective C#(이펙티브)(3판)\",\"translators\":[\"김명신\"],\"url\":\"https://search.daum.net/search?w=bookpage\\u0026bookId=1611826\\u0026q=Effective+C%23%28%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C%29%283%ED%8C%90%29\"},{\"authors\":[\"에드먼드 라우\"],\"contents\":\"있다. 이 책은 이와 반대로, 무작정 열심히만 하는 것이 아니라 제한된 시간과 에너지라는 자원을 가장 큰 효과를 내는 곳에 투자하여 지금보다 더 적게 일하고도 더 큰 성과를 내라고 주장한다. 바로 이것이 효율적으로 일하며 탁월한 성과를 내는 이펙티브 엔지니어의 특징이다. 이러한 주장을 뒷받침하기 위해 저자 자신의 경험과 노하우, 세계 최고 IT 기업의 책임 개발자들의 사례를 소개하고 이를 바탕으로 개인과 조직에 적용할 수 있는 구체적인 방법까지 제시한다\",\"datetime\":\"2022-06-27T00:00:00.000+09:00\",\"isbn\":\"1140700286 9791140700288\",\"price\":22000,\"publisher\":\"길벗\",\"sale_price\":19800,\"status\":\"정상판매\",\"thumbnail\":\"https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F6100574%3Ftimestamp%3D20220709163633\",\"title\":\"이펙티브 엔지니어\",\"translators\":[\"이미령\"],\"url\":\"https://search.daum.net/search?w=bookpage\\u0026bookId=6100574\\u0026q=%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C+%EC%97%94%EC%A7%80%EB%8B%88%EC%96%B4\"},{\"authors\":[\"댄 밴더캄\"],\"contents\":\"타입스크립트는 타입 정보를 지닌 자바스크립트의 상위 집합으로, 자바스크립트의 골치 아픈 문제점들을 해결해 준다. 이 책은 《이펙티브 C++》와 《이펙티브 자바》의 형식을 차용해 타입스크립트의 동작 원리, 해야 할 것과 하지 말아야 할 것에 대한 구체적인 조언을 62가지 항목으로 나누어 담았다. 각 항목의 조언을 실제로 적용한 예제를 통해 연습하다 보면 타입스크립트를 효율적으로 사용하는 방법을 익힐 수 있다. 타입스크립트를 기초적인 수준에서만 활용했다면\",\"datetime\":\"2021-06-22T00:00:00.000+09:00\",\"isbn\":\"8966263135 9788966263134\",\"price\":25000,\"publisher\":\"인사이트\",\"sale_price\":22500,\"status\":\"정상판매\",\"thumbnail\":\"https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F5736428%3Ftimestamp%3D20220709155826\",\"title\":\"이펙티브 타입스크립트\",\"translators\":[\"장원호\"],\"url\":\"https://search.daum.net/search?w=bookpage\\u0026bookId=5736428\\u0026q=%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C+%ED%83%80%EC%9E%85%EC%8A%A4%ED%81%AC%EB%A6%BD%ED%8A%B8\"},{\"authors\":[\"마르친 모스칼라\"],\"contents\":\"이 책은 더 나은 코틀린 개발자가 될 수 있도록 도움을 주는 안내서입니다. 코틀린에 어떤 기능이 있는지, 어떤 표준 라이브러리가 있는지 알고 있다고 코틀린을 강력하고 실용적으로 사용할 수 있는 것은 아닙니다. 코틀린을 제대로 사용하려면, 그 기능을 언제, 어떻게 적절하게 사용해야 하는지 알아야 합니다. 이 책은 많은 사람�* Connection #0 to host dapi.kakao.com left intact\n"
                + "� 제대로 활용하지 못하고 있는 기능을 간단한 규칙으로 제시하고, 52가지 아이템을 실제 사례를 통해 자세하게 설명합니다. 각각\",\"datetime\":\"2022-01-21T00:00:00.000+09:00\",\"isbn\":\"8966263372 9788966263370\",\"price\":28000,\"publisher\":\"인사이트\",\"sale_price\":25200,\"status\":\"정상판매\",\"thumbnail\":\"https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F5945070%3Ftimestamp%3D20220709155017\",\"title\":\"이펙티브 코틀린(프로그래밍인사이트)\",\"translators\":[\"윤인성\"],\"url\":\"https://search.daum.net/search?w=bookpage\\u0026bookId=5945070\\u0026q=%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C+%EC%BD%94%ED%8B%80%EB%A6%B0%28%ED%94%84%EB%A1%9C%EA%B7%B8%EB%9E%98%EB%B0%8D%EC%9D%B8%EC%82%AC%EC%9D%B4%ED%8A%B8%29\"},{\"authors\":[\"조슈아 블로크\"],\"contents\":\"자바 6 출시 직후 출간된 『이펙티브 자바 2판』 이후로 자바는 커다란 변화를 겪었다. 그래서 졸트상에 빛나는 이 책도 자바 언어와 라이브러리의 최신 기능을 십분 활용하도록 내용 전반을 철저히 다시 썼다. 모던 자바가 여러 패러다임을 지원하기 시작하면서 자바 개발자들에게는 구체적인 모범 사례가 더욱 절실해졌고, 관련 조언을 이 책에 담아낸 것이다.  3판에는 자바 7, 8, 9에서 자바 언어와 라이브러리에 추가된 특성들을 녹여냈다. 특히 그동안 객체\",\"datetime\":\"2018-11-01T00:00:00.000+09:00\",\"isbn\":\"8966262287 9788966262281\",\"price\":36000,\"publisher\":\"인사이트\",\"sale_price\":32400,\"status\":\"정상판매\",\"thumbnail\":\"https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F3782516%3Ftimestamp%3D20220709160720\",\"title\":\"이펙티브 자바 3/E\",\"translators\":[\"개앞맵시\"],\"url\":\"https://search.daum.net/search?w=bookpage\\u0026bookId=3782516\\u0026q=%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C+%EC%9E%90%EB%B0%94+3%2FE\"}],\"meta\":{\"is_end\":false,\"pageable_count\":24,\"total_count\":24}}";

        mockServer.expect(
                requestTo(uriComponentsBuilder.build().encode(StandardCharsets.UTF_8).toUri()))
            .andRespond(withSuccess(result, MediaType.APPLICATION_JSON));

        BookSearchResponseDto bookSearchResponseDto = bookSearchService.searchByTitle(
            bookSearchRequestDto);

        assertThat(bookSearchResponseDto.getBookDtos().size()).isEqualTo(5);
    }

    @Test
    public void title로_외부api_호출시_size지정하지_않으면_default_10() throws Exception {

    }
}