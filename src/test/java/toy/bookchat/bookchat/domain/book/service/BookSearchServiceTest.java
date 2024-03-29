package toy.bookchat.bookchat.domain.book.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.config.web.BookSearchProperties;
import toy.bookchat.bookchat.domain.book.service.dto.request.BookSearchRequest;
import toy.bookchat.bookchat.domain.book.service.dto.request.KakaoBook;
import toy.bookchat.bookchat.domain.book.service.dto.response.BookSearchResponse;

@ExtendWith(MockitoExtension.class)
class BookSearchServiceTest {

    private final String isbn = "8996991341 9788996991342";
    private final String title = "미움받을 용기";
    private final String datetime = "2014-11-17";
    private final String[] authors = {"기시미 이치로", "고가 후미타케"};
    private final String publisher = "인플루엔셜";
    private final String bookCoverImageUrl = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1467038%3Ftimestamp%3D20220505201559";
    private final String result = "{\"documents\":[{\"authors\":[\"기시미 이치로\",\"고가 후미타케\"],\"contents\":\"어릴 때부터 성격이 어두워 사람들과 쉽게 친해지지 못하는 사람이 있다. 언제까지 다른 사람들과의 관계 때문에 전전긍긍하며 살아야 할지, 그는 오늘도 고민이다. 이런 그의 고민에 “인간은 변할 수 있고, 누구나 행복해 질 수 있다. 단 그러기 위해서는 ‘용기’가 필요하다”고 말한 철학자가 있다. 바로 프로이트, 융과 함께 ‘심리학의 3대 거장’으로 일컬어지고 있는 알프레드 아들러다.  『미움받을 용기』는 아들러 심리학에 관한 일본의 1인자 철학자\",\"datetime\":\"2014-11-17T00:00:00.000+09:00\",\"isbn\":\"8996991341 9788996991342\",\"price\":14900,\"publisher\":\"인플루엔셜\",\"sale_price\":13410,\"status\":\"정상판매\",\"thumbnail\":\"https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1467038%3Ftimestamp%3D20220505201559\",\"title\":\"미움받을 용기\",\"translators\":[\"전경아\"],\"url\":\"https://search.daum.net/search?w=bookpage&bookId=1467038&q=%EB%AF%B8%EC%9B%80%EB%B0%9B%EC%9D%84+%EC%9A%A9%EA%B8%B0\"}],\"meta\":{\"is_end\":true,\"pageable_count\":1,\"total_count\":1}}";
    private final String apiUri = "https://test.test.com/test/test/book?target=";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private BookFetcher<KakaoBook> bookFetcher;
    @Mock
    private BookSearchProperties bookSearchProperties;
    @InjectMocks
    private BookSearchServiceImpl bookSearchService;

    private BookSearchRequest getBookSearchRequest(String query, Integer size, Integer page,
        BookSearchSort bookSearchSort) {
        return BookSearchRequest.builder()
            .query(query)
            .size(size)
            .page(page)
            .sort(bookSearchSort)
            .build();
    }

    @Test
    void isbn으로_외부api_호출_json결과_응답() throws Exception {
        KakaoBook kakaoBook = objectMapper.readValue(result, KakaoBook.class);
        BookSearchRequest bookSearchRequest = getBookSearchRequest(isbn, null, null, null);

        when(bookSearchProperties.getUri()).thenReturn(apiUri);
        when(bookFetcher.fetchBooks(any(), any())).thenReturn(kakaoBook);

        BookSearchResponse bookSearchResponse = bookSearchService.searchByQuery(
            bookSearchRequest);

        assertAll(
            () -> assertThat(bookSearchResponse.getBookResponses().get(0).getIsbn()).isEqualTo(
                isbn),
            () -> assertThat(bookSearchResponse.getBookResponses().get(0).getTitle()).isEqualTo(
                title),
            () -> assertThat(bookSearchResponse.getBookResponses().get(0).getPublishAt()).isEqualTo(
                datetime),
            () -> assertThat(bookSearchResponse.getBookResponses().get(0).getPublisher()).isEqualTo(
                publisher),
            () -> assertThat(bookSearchResponse.getBookResponses().get(0).getAuthors()).isEqualTo(
                List.of(authors)),
            () -> assertThat(
                bookSearchResponse.getBookResponses().get(0).getBookCoverImageUrl()).isEqualTo(
                bookCoverImageUrl)
        );
    }

    @Test
    void title로_외부api_호출_json결과_응답() throws Exception {
        BookSearchRequest bookSearchRequest = getBookSearchRequest(title, null, null, null);
        KakaoBook kakaoBook = objectMapper.readValue(result, KakaoBook.class);

        when(bookSearchProperties.getUri()).thenReturn(apiUri);
        when(bookFetcher.fetchBooks(any(), any())).thenReturn(kakaoBook);

        BookSearchResponse bookSearchResponse = bookSearchService.searchByQuery(
            bookSearchRequest);

        assertAll(
            () -> assertThat(bookSearchResponse.getBookResponses().get(0).getIsbn()).isEqualTo(
                isbn),
            () -> assertThat(bookSearchResponse.getBookResponses().get(0).getTitle()).isEqualTo(
                title),
            () -> assertThat(bookSearchResponse.getBookResponses().get(0).getPublishAt()).isEqualTo(
                datetime),
            () -> assertThat(bookSearchResponse.getBookResponses().get(0).getPublisher()).isEqualTo(
                publisher),
            () -> assertThat(bookSearchResponse.getBookResponses().get(0).getAuthors()).isEqualTo(
                List.of(authors)),
            () -> assertThat(
                bookSearchResponse.getBookResponses().get(0).getBookCoverImageUrl()).isEqualTo(
                bookCoverImageUrl)
        );
    }

    @Test
    void author로_외부api_호출_json결과_응답() throws Exception {
        BookSearchRequest bookSearchRequest = getBookSearchRequest(authors[0], null, null,
            null);
        KakaoBook kakaoBook = objectMapper.readValue(result, KakaoBook.class);

        when(bookSearchProperties.getUri()).thenReturn(apiUri);
        when(bookFetcher.fetchBooks(any(), any())).thenReturn(kakaoBook);

        BookSearchResponse bookSearchResponse = bookSearchService.searchByQuery(bookSearchRequest);

        assertAll(
            () -> assertThat(bookSearchResponse.getBookResponses().get(0).getIsbn()).isEqualTo(
                isbn),
            () -> assertThat(bookSearchResponse.getBookResponses().get(0).getTitle()).isEqualTo(
                title),
            () -> assertThat(bookSearchResponse.getBookResponses().get(0).getPublishAt()).isEqualTo(
                datetime),
            () -> assertThat(bookSearchResponse.getBookResponses().get(0).getPublisher()).isEqualTo(
                publisher),
            () -> assertThat(bookSearchResponse.getBookResponses().get(0).getAuthors()).isEqualTo(
                List.of(authors)),
            () -> assertThat(
                bookSearchResponse.getBookResponses().get(0).getBookCoverImageUrl()).isEqualTo(
                bookCoverImageUrl)
        );
    }

    @Test
    void 외부api_호출시_size지정() throws Exception {
        BookSearchRequest bookSearchRequest = getBookSearchRequest("이펙티브", 5, null, null);
        String result = "{\"documents\":[{\"authors\":[\"빌 와그너\"],\"contents\":\"C#은 전통적인 .NET 기반 개발에서 유니티 게임 엔진으로 개발 영역을 확대하면서 더욱 주목받고 있다. 또한 자마린으로 다양한 모바일 플랫폼에 대응할 수 있어 수요가 계속 늘고 있다. 이에 이 책은 소프트웨어 개발자가 C#을 더 효율적으로 사용할 수 있도록 다양한 팁과 실용적인 방법을 제공한다.  저자는 C#의 힘을 온전히 활용하여 효율적이고 성능이 뛰어난 코드를 작성하는 방법 50가지를 알려준다. 탁월한 C# 경험을 바탕으로 리소스 관리부터\",\"datetime\":\"2017-11-06T00:00:00.000+09:00\",\"isbn\":\"1162240032 9791162240038\",\"price\":25000,\"publisher\":\"한빛미디어\",\"sale_price\":22500,\"status\":\"정상판매\",\"thumbnail\":\"https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1611826%3Ftimestamp%3D20220709154251\",\"title\":\"Effective C#(이펙티브)(3판)\",\"translators\":[\"김명신\"],\"url\":\"https://search.daum.net/search?w=bookpage&bookId=1611826&q=Effective+C%23%28%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C%29%283%ED%8C%90%29\"},{\"authors\":[\"에드먼드 라우\"],\"contents\":\"있다. 이 책은 이와 반대로, 무작정 열심히만 하는 것이 아니라 제한된 시간과 에너지라는 자원을 가장 큰 효과를 내는 곳에 투자하여 지금보다 더 적게 일하고도 더 큰 성과를 내라고 주장한다. 바로 이것이 효율적으로 일하며 탁월한 성과를 내는 이펙티브 엔지니어의 특징이다. 이러한 주장을 뒷받침하기 위해 저자 자신의 경험과 노하우, 세계 최고 IT 기업의 책임 개발자들의 사례를 소개하고 이를 바탕으로 개인과 조직에 적용할 수 있는 구체적인 방법까지 제시한다\",\"datetime\":\"2022-06-27T00:00:00.000+09:00\",\"isbn\":\"1140700286 9791140700288\",\"price\":22000,\"publisher\":\"길벗\",\"sale_price\":19800,\"status\":\"정상판매\",\"thumbnail\":\"https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F6100574%3Ftimestamp%3D20220709163633\",\"title\":\"이펙티브 엔지니어\",\"translators\":[\"이미령\"],\"url\":\"https://search.daum.net/search?w=bookpage&bookId=6100574&q=%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C+%EC%97%94%EC%A7%80%EB%8B%88%EC%96%B4\"},{\"authors\":[\"댄 밴더캄\"],\"contents\":\"타입스크립트는 타입 정보를 지닌 자바스크립트의 상위 집합으로, 자바스크립트의 골치 아픈 문제점들을 해결해 준다. 이 책은 《이펙티브 C++》와 《이펙티브 자바》의 형식을 차용해 타입스크립트의 동작 원리, 해야 할 것과 하지 말아야 할 것에 대한 구체적인 조언을 62가지 항목으로 나누어 담았다. 각 항목의 조언을 실제로 적용한 예제를 통해 연습하다 보면 타입스크립트를 효율적으로 사용하는 방법을 익힐 수 있다. 타입스크립트를 기초적인 수준에서만 활용했다면\",\"datetime\":\"2021-06-22T00:00:00.000+09:00\",\"isbn\":\"8966263135 9788966263134\",\"price\":25000,\"publisher\":\"인사이트\",\"sale_price\":22500,\"status\":\"정상판매\",\"thumbnail\":\"https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F5736428%3Ftimestamp%3D20220709155826\",\"title\":\"이펙티브 타입스크립트\",\"translators\":[\"장원호\"],\"url\":\"https://search.daum.net/search?w=bookpage&bookId=5736428&q=%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C+%ED%83%80%EC%9E%85%EC%8A%A4%ED%81%AC%EB%A6%BD%ED%8A%B8\"},{\"authors\":[\"마르친 모스칼라\"],\"contents\":\"이 책은 더 나은 코틀린 개발자가 될 수 있도록 도움을 주는 안내서입니다. 코틀린에 어떤 기능이 있는지, 어떤 표준 라이브러리가 있는지 알고 있다고 코틀린을 강력하고 실용적으로 사용할 수 있는 것은 아닙니다. 코틀린을 제대로 사용하려면, 그 기능을 언제, 어떻게 적절하게 사용해야 하는지 알아야 합니다. 이 책은 많은 사람�* Connection #0 to host dapi.kakao.com left intact� 제대로 활용하지 못하고 있는 기능을 간단한 규칙으로 제시하고, 52가지 아이템을 실제 사례를 통해 자세하게 설명합니다. 각각\",\"datetime\":\"2022-01-21T00:00:00.000+09:00\",\"isbn\":\"8966263372 9788966263370\",\"price\":28000,\"publisher\":\"인사이트\",\"sale_price\":25200,\"status\":\"정상판매\",\"thumbnail\":\"https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F5945070%3Ftimestamp%3D20220709155017\",\"title\":\"이펙티브 코틀린(프로그래밍인사이트)\",\"translators\":[\"윤인성\"],\"url\":\"https://search.daum.net/search?w=bookpage&bookId=5945070&q=%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C+%EC%BD%94%ED%8B%80%EB%A6%B0%28%ED%94%84%EB%A1%9C%EA%B7%B8%EB%9E%98%EB%B0%8D%EC%9D%B8%EC%82%AC%EC%9D%B4%ED%8A%B8%29\"},{\"authors\":[\"조슈아 블로크\"],\"contents\":\"자바 6 출시 직후 출간된 『이펙티브 자바 2판』 이후로 자바는 커다란 변화를 겪었다. 그래서 졸트상에 빛나는 이 책도 자바 언어와 라이브러리의 최신 기능을 십분 활용하도록 내용 전반을 철저히 다시 썼다. 모던 자바가 여러 패러다임을 지원하기 시작하면서 자바 개발자들에게는 구체적인 모범 사례가 더욱 절실해졌고, 관련 조언을 이 책에 담아낸 것이다.  3판에는 자바 7, 8, 9에서 자바 언어와 라이브러리에 추가된 특성들을 녹여냈다. 특히 그동안 객체\",\"datetime\":\"2018-11-01T00:00:00.000+09:00\",\"isbn\":\"8966262287 9788966262281\",\"price\":36000,\"publisher\":\"인사이트\",\"sale_price\":32400,\"status\":\"정상판매\",\"thumbnail\":\"https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F3782516%3Ftimestamp%3D20220709160720\",\"title\":\"이펙티브 자바 3/E\",\"translators\":[\"개앞맵시\"],\"url\":\"https://search.daum.net/search?w=bookpage&bookId=3782516&q=%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C+%EC%9E%90%EB%B0%94+3%2FE\"}],\"meta\":{\"is_end\":false,\"pageable_count\":24,\"total_count\":24}}";
        KakaoBook kakaoBook = objectMapper.readValue(result, KakaoBook.class);

        when(bookSearchProperties.getUri()).thenReturn(apiUri);
        when(bookFetcher.fetchBooks(any(), any())).thenReturn(kakaoBook);

        BookSearchResponse bookSearchResponse = bookSearchService.searchByQuery(
            bookSearchRequest);

        assertThat(bookSearchResponse.getBookResponses().size()).isEqualTo(5);
    }

    @Test
    void 외부api_호출시_page지정() throws Exception {
        BookSearchRequest bookSearchRequest = getBookSearchRequest("이펙티브", 5, 1, null);
        String result = "{\"documents\":[{\"authors\":[\"빌 와그너\"],\"contents\":\"C#은 전통적인 .NET 기반 개발에서 유니티 게임 엔진으로 개발 영역을 확대하면서 더욱 주목받고 있다. 또한 자마린으로 다양한 모바일 플랫폼에 대응할 수 있어 수요가 계속 늘고 있다. 이에 이 책은 소프트웨어 개발자가 C#을 더 효율적으로 사용할 수 있도록 다양한 팁과 실용적인 방법을 제공한다.  저자는 C#의 힘을 온전히 활용하여 효율적이고 성능이 뛰어난 코드를 작성하는 방법 50가지를 알려준다. 탁월한 C# 경험을 바탕으로 리소스 관리부터\",\"datetime\":\"2017-11-06T00:00:00.000+09:00\",\"isbn\":\"1162240032 9791162240038\",\"price\":25000,\"publisher\":\"한빛미디어\",\"sale_price\":22500,\"status\":\"정상판매\",\"thumbnail\":\"https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1611826%3Ftimestamp%3D20220709154251\",\"title\":\"Effective C#(이펙티브)(3판)\",\"translators\":[\"김명신\"],\"url\":\"https://search.daum.net/search?w=bookpage&bookId=1611826&q=Effective+C%23%28%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C%29%283%ED%8C%90%29\"},{\"authors\":[\"에드먼드 라우\"],\"contents\":\"있다. 이 책은 이와 반대로, 무작정 열심히만 하는 것이 아니라 제한된 시간과 에너지라는 자원을 가장 큰 효과를 내는 곳에 투자하여 지금보다 더 적게 일하고도 더 큰 성과를 내라고 주장한다. 바로 이것이 효율적으로 일하며 탁월한 성과를 내는 이펙티브 엔지니어의 특징이다. 이러한 주장을 뒷받침하기 위해 저자 자신의 경험과 노하우, 세계 최고 IT 기업의 책임 개발자들의 사례를 소개하고 이를 바탕으로 개인과 조직에 적용할 수 있는 구체적인 방법까지 제시한다\",\"datetime\":\"2022-06-27T00:00:00.000+09:00\",\"isbn\":\"1140700286 9791140700288\",\"price\":22000,\"publisher\":\"길벗\",\"sale_price\":19800,\"status\":\"정상판매\",\"thumbnail\":\"https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F6100574%3Ftimestamp%3D20220709163633\",\"title\":\"이펙티브 엔지니어\",\"translators\":[\"이미령\"],\"url\":\"https://search.daum.net/search?w=bookpage&bookId=6100574&q=%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C+%EC%97%94%EC%A7%80%EB%8B%88%EC%96%B4\"},{\"authors\":[\"댄 밴더캄\"],\"contents\":\"타입스크립트는 타입 정보를 지닌 자바스크립트의 상위 집합으로, 자바스크립트의 골치 아픈 문제점들을 해결해 준다. 이 책은 《이펙티브 C++》와 《이펙티브 자바》의 형식을 차용해 타입스크립트의 동작 원리, 해야 할 것과 하지 말아야 할 것에 대한 구체적인 조언을 62가지 항목으로 나누어 담았다. 각 항목의 조언을 실제로 적용한 예제를 통해 연습하다 보면 타입스크립트를 효율적으로 사용하는 방법을 익힐 수 있다. 타입스크립트를 기초적인 수준에서만 활용했다면\",\"datetime\":\"2021-06-22T00:00:00.000+09:00\",\"isbn\":\"8966263135 9788966263134\",\"price\":25000,\"publisher\":\"인사이트\",\"sale_price\":22500,\"status\":\"정상판매\",\"thumbnail\":\"https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F5736428%3Ftimestamp%3D20220709155826\",\"title\":\"이펙티브 타입스크립트\",\"translators\":[\"장원호\"],\"url\":\"https://search.daum.net/search?w=bookpage&bookId=5736428&q=%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C+%ED%83%80%EC%9E%85%EC%8A%A4%ED%81%AC%EB%A6%BD%ED%8A%B8\"},{\"authors\":[\"마르친 모스칼라\"],\"contents\":\"이 책은 더 나은 코틀린 개발자가 될 수 있도록 도움을 주는 안내서입니다. 코틀린에 어떤 기능이 있는지, 어떤 표준 라이브러리가 있는지 알고 있다고 코틀린을 강력하고 실용적으로 사용할 수 있는 것은 아닙니다. 코틀린을 제대로 사용하려면, 그 기능을 언제, 어떻게 적절하게 사용해야 하는지 알아야 합니다. 이 책은 많은 사람�* Connection #0 to host dapi.kakao.com left intact� 제대로 활용하지 못하고 있는 기능을 간단한 규칙으로 제시하고, 52가지 아이템을 실제 사례를 통해 자세하게 설명합니다. 각각\",\"datetime\":\"2022-01-21T00:00:00.000+09:00\",\"isbn\":\"8966263372 9788966263370\",\"price\":28000,\"publisher\":\"인사이트\",\"sale_price\":25200,\"status\":\"정상판매\",\"thumbnail\":\"https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F5945070%3Ftimestamp%3D20220709155017\",\"title\":\"이펙티브 코틀린(프로그래밍인사이트)\",\"translators\":[\"윤인성\"],\"url\":\"https://search.daum.net/search?w=bookpage&bookId=5945070&q=%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C+%EC%BD%94%ED%8B%80%EB%A6%B0%28%ED%94%84%EB%A1%9C%EA%B7%B8%EB%9E%98%EB%B0%8D%EC%9D%B8%EC%82%AC%EC%9D%B4%ED%8A%B8%29\"},{\"authors\":[\"조슈아 블로크\"],\"contents\":\"자바 6 출시 직후 출간된 『이펙티브 자바 2판』 이후로 자바는 커다란 변화를 겪었다. 그래서 졸트상에 빛나는 이 책도 자바 언어와 라이브러리의 최신 기능을 십분 활용하도록 내용 전반을 철저히 다시 썼다. 모던 자바가 여러 패러다임을 지원하기 시작하면서 자바 개발자들에게는 구체적인 모범 사례가 더욱 절실해졌고, 관련 조언을 이 책에 담아낸 것이다.  3판에는 자바 7, 8, 9에서 자바 언어와 라이브러리에 추가된 특성들을 녹여냈다. 특히 그동안 객체\",\"datetime\":\"2018-11-01T00:00:00.000+09:00\",\"isbn\":\"8966262287 9788966262281\",\"price\":36000,\"publisher\":\"인사이트\",\"sale_price\":32400,\"status\":\"정상판매\",\"thumbnail\":\"https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F3782516%3Ftimestamp%3D20220709160720\",\"title\":\"이펙티브 자바 3/E\",\"translators\":[\"개앞맵시\"],\"url\":\"https://search.daum.net/search?w=bookpage&bookId=3782516&q=%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C+%EC%9E%90%EB%B0%94+3%2FE\"}],\"meta\":{\"is_end\":false,\"pageable_count\":24,\"total_count\":24}}";
        KakaoBook kakaoBook = objectMapper.readValue(result, KakaoBook.class);

        when(bookSearchProperties.getUri()).thenReturn(apiUri);
        when(bookFetcher.fetchBooks(any(), any())).thenReturn(kakaoBook);

        BookSearchResponse bookSearchResponse = bookSearchService.searchByQuery(
            bookSearchRequest);

        assertThat(bookSearchResponse.getBookResponses().size()).isEqualTo(5);
    }

    @Test
    void 외부api_호출시_sort지정() throws Exception {
        BookSearchRequest bookSearchRequest = getBookSearchRequest("이펙티브", 5, 1,
            BookSearchSort.LATEST);
        String result = "{\"documents\":[{\"authors\":[\"빌 와그너\"],\"contents\":\"C#은 전통적인 .NET 기반 개발에서 유니티 게임 엔진으로 개발 영역을 확대하면서 더욱 주목받고 있다. 또한 자마린으로 다양한 모바일 플랫폼에 대응할 수 있어 수요가 계속 늘고 있다. 이에 이 책은 소프트웨어 개발자가 C#을 더 효율적으로 사용할 수 있도록 다양한 팁과 실용적인 방법을 제공한다.  저자는 C#의 힘을 온전히 활용하여 효율적이고 성능이 뛰어난 코드를 작성하는 방법 50가지를 알려준다. 탁월한 C# 경험을 바탕으로 리소스 관리부터\",\"datetime\":\"2017-11-06T00:00:00.000+09:00\",\"isbn\":\"1162240032 9791162240038\",\"price\":25000,\"publisher\":\"한빛미디어\",\"sale_price\":22500,\"status\":\"정상판매\",\"thumbnail\":\"https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1611826%3Ftimestamp%3D20220709154251\",\"title\":\"Effective C#(이펙티브)(3판)\",\"translators\":[\"김명신\"],\"url\":\"https://search.daum.net/search?w=bookpage&bookId=1611826&q=Effective+C%23%28%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C%29%283%ED%8C%90%29\"},{\"authors\":[\"에드먼드 라우\"],\"contents\":\"있다. 이 책은 이와 반대로, 무작정 열심히만 하는 것이 아니라 제한된 시간과 에너지라는 자원을 가장 큰 효과를 내는 곳에 투자하여 지금보다 더 적게 일하고도 더 큰 성과를 내라고 주장한다. 바로 이것이 효율적으로 일하며 탁월한 성과를 내는 이펙티브 엔지니어의 특징이다. 이러한 주장을 뒷받침하기 위해 저자 자신의 경험과 노하우, 세계 최고 IT 기업의 책임 개발자들의 사례를 소개하고 이를 바탕으로 개인과 조직에 적용할 수 있는 구체적인 방법까지 제시한다\",\"datetime\":\"2022-06-27T00:00:00.000+09:00\",\"isbn\":\"1140700286 9791140700288\",\"price\":22000,\"publisher\":\"길벗\",\"sale_price\":19800,\"status\":\"정상판매\",\"thumbnail\":\"https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F6100574%3Ftimestamp%3D20220709163633\",\"title\":\"이펙티브 엔지니어\",\"translators\":[\"이미령\"],\"url\":\"https://search.daum.net/search?w=bookpage&bookId=6100574&q=%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C+%EC%97%94%EC%A7%80%EB%8B%88%EC%96%B4\"},{\"authors\":[\"댄 밴더캄\"],\"contents\":\"타입스크립트는 타입 정보를 지닌 자바스크립트의 상위 집합으로, 자바스크립트의 골치 아픈 문제점들을 해결해 준다. 이 책은 《이펙티브 C++》와 《이펙티브 자바》의 형식을 차용해 타입스크립트의 동작 원리, 해야 할 것과 하지 말아야 할 것에 대한 구체적인 조언을 62가지 항목으로 나누어 담았다. 각 항목의 조언을 실제로 적용한 예제를 통해 연습하다 보면 타입스크립트를 효율적으로 사용하는 방법을 익힐 수 있다. 타입스크립트를 기초적인 수준에서만 활용했다면\",\"datetime\":\"2021-06-22T00:00:00.000+09:00\",\"isbn\":\"8966263135 9788966263134\",\"price\":25000,\"publisher\":\"인사이트\",\"sale_price\":22500,\"status\":\"정상판매\",\"thumbnail\":\"https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F5736428%3Ftimestamp%3D20220709155826\",\"title\":\"이펙티브 타입스크립트\",\"translators\":[\"장원호\"],\"url\":\"https://search.daum.net/search?w=bookpage&bookId=5736428&q=%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C+%ED%83%80%EC%9E%85%EC%8A%A4%ED%81%AC%EB%A6%BD%ED%8A%B8\"},{\"authors\":[\"마르친 모스칼라\"],\"contents\":\"이 책은 더 나은 코틀린 개발자가 될 수 있도록 도움을 주는 안내서입니다. 코틀린에 어떤 기능이 있는지, 어떤 표준 라이브러리가 있는지 알고 있다고 코틀린을 강력하고 실용적으로 사용할 수 있는 것은 아닙니다. 코틀린을 제대로 사용하려면, 그 기능을 언제, 어떻게 적절하게 사용해야 하는지 알아야 합니다. 이 책은 많은 사람�* Connection #0 to host dapi.kakao.com left intact� 제대로 활용하지 못하고 있는 기능을 간단한 규칙으로 제시하고, 52가지 아이템을 실제 사례를 통해 자세하게 설명합니다. 각각\",\"datetime\":\"2022-01-21T00:00:00.000+09:00\",\"isbn\":\"8966263372 9788966263370\",\"price\":28000,\"publisher\":\"인사이트\",\"sale_price\":25200,\"status\":\"정상판매\",\"thumbnail\":\"https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F5945070%3Ftimestamp%3D20220709155017\",\"title\":\"이펙티브 코틀린(프로그래밍인사이트)\",\"translators\":[\"윤인성\"],\"url\":\"https://search.daum.net/search?w=bookpage&bookId=5945070&q=%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C+%EC%BD%94%ED%8B%80%EB%A6%B0%28%ED%94%84%EB%A1%9C%EA%B7%B8%EB%9E%98%EB%B0%8D%EC%9D%B8%EC%82%AC%EC%9D%B4%ED%8A%B8%29\"},{\"authors\":[\"조슈아 블로크\"],\"contents\":\"자바 6 출시 직후 출간된 『이펙티브 자바 2판』 이후로 자바는 커다란 변화를 겪었다. 그래서 졸트상에 빛나는 이 책도 자바 언어와 라이브러리의 최신 기능을 십분 활용하도록 내용 전반을 철저히 다시 썼다. 모던 자바가 여러 패러다임을 지원하기 시작하면서 자바 개발자들에게는 구체적인 모범 사례가 더욱 절실해졌고, 관련 조언을 이 책에 담아낸 것이다.  3판에는 자바 7, 8, 9에서 자바 언어와 라이브러리에 추가된 특성들을 녹여냈다. 특히 그동안 객체\",\"datetime\":\"2018-11-01T00:00:00.000+09:00\",\"isbn\":\"8966262287 9788966262281\",\"price\":36000,\"publisher\":\"인사이트\",\"sale_price\":32400,\"status\":\"정상판매\",\"thumbnail\":\"https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F3782516%3Ftimestamp%3D20220709160720\",\"title\":\"이펙티브 자바 3/E\",\"translators\":[\"개앞맵시\"],\"url\":\"https://search.daum.net/search?w=bookpage&bookId=3782516&q=%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C+%EC%9E%90%EB%B0%94+3%2FE\"}],\"meta\":{\"is_end\":false,\"pageable_count\":24,\"total_count\":24}}";
        KakaoBook kakaoBook = objectMapper.readValue(result, KakaoBook.class);

        when(bookSearchProperties.getUri()).thenReturn(apiUri);
        when(bookFetcher.fetchBooks(any(), any())).thenReturn(kakaoBook);

        BookSearchResponse bookSearchResponse = bookSearchService.searchByQuery(
            bookSearchRequest);

        assertThat(bookSearchResponse.getBookResponses().size()).isEqualTo(5);
    }

    @Test
    void 응답도서_특정필드에_null값이_있을_경우_제외처리후_반환() throws Exception {
        BookSearchRequest bookSearchRequest = getBookSearchRequest("자바", 3, 1,
            BookSearchSort.LATEST);
        String result = "{\"documents\": [{\"authors\": null,\"contents\": \"야후의 선임 자바스크립트 아키텍트 더글라스 크락포드의 『자바스크립트 핵심 가이드』. 자바스크립트를 우연히 접했거나 호기심이 생겨 탐험하고 싶어하는 프로그래머를 위해 저술된 것이다.  이 책은 놀라울 정도로 강력한 언어인 자바스크립트에 대한 핵심적인 안내서다. 자바스크립트가 제공하는 여러 가지 기능을 보여준 다음, 그것을 조합하여 사용하는 방법을 찾을 수 있도록 인도한다.  자바스크립트를 우수한 객체지향 언어로 만들 수 있는 장점에 대해서도 다루는 것\",\"datetime\": \"2008-09-30T00:00:00.000+09:00\",\"isbn\": null,\"price\": 22000,\"publisher\": \"한빛미디어\",\"sale_price\": 19800,\"status\": \"정상판매\",\"thumbnail\": \"https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1103216%3Ftimestamp%3D20221011091039\",\"title\": \"자바스크립트 핵심 가이드(더글라스 크락포드의)\",\"translators\": [\"김명신\"],\"url\": \"https://search.daum.net/search?w=bookpage&bookId=1103216&q=%EC%9E%90%EB%B0%94%EC%8A%A4%ED%81%AC%EB%A6%BD%ED%8A%B8+%ED%95%B5%EC%8B%AC+%EA%B0%80%EC%9D%B4%EB%93%9C%28%EB%8D%94%EA%B8%80%EB%9D%BC%EC%8A%A4+%ED%81%AC%EB%9D%BD%ED%8F%AC%EB%93%9C%EC%9D%98%29\"\n    },\n    {\n      \"authors\": [\n        \"신용권\"\n      ],\n      \"contents\": \"『이것이 자바다』은 15년 이상 자바 언어를 교육해온 자바 전문강사의 노하우를 아낌 없이 담아낸 자바 입문서이다. 자바 입문자를 배려한 친절한 설명과 배려로 1장에 풀인원 설치 방법을 제공하여 쉽게 학습환경을 구축할 수 있다. 또한 중급 개발자로 나아가기 위한 람다식(14장), JavaFX(17장), NIO(18~19장) 수록되어 있으며 각 챕터마다 확인문제 제공. 풀이와 답은 인터넷 강의에서 친절한 해설을 통해 알려준다.\",\n      \"datetime\": \"2015-01-05T00:00:00.000+09:00\",\n      \"isbn\": \"8968481474 9788968481475\",\n      \"price\": 30000,\n      \"publisher\": \"한빛미디어\",\n      \"sale_price\": 27000,\n      \"status\": \"정상판매\",\n      \"thumbnail\": \"https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F945967%3Ftimestamp%3D20221011172516\",\n      \"title\": \"이것이 자바다\",\n      \"translators\": [],\n      \"url\": \"https://search.daum.net/search?w=bookpage&bookId=945967&q=%EC%9D%B4%EA%B2%83%EC%9D%B4+%EC%9E%90%EB%B0%94%EB%8B%A4\"\n    },\n    {\n      \"authors\": [\n        \"김병부\"\n      ],\n      \"contents\": \"『자바를 다루는 기술』은 자바 언어의 기초 문법을 친절하고 자세하게 설명한다. 객체 지향 프로그래밍 개념은 물론, 자바의 자료구조, 제네릭(generics), 리플렉션(reflection) 등 고급 응용 기법들을 다양한 예제를 통해 익힐 수 있도록 구성하였다. 또한 저자의 실무 경험 속에서 얻은 노하우와 팁들을 제시하고, 오픈 소스 라이브러리 응용법 등을 통해 실무 적응력을 높여 독자들이 다양한 개발 현장에서 자바 프로젝트를 어려움 없이 수행할 수 있도록\",\n      \"datetime\": null,\n      \"isbn\": \"8966185525 9788966185528\",\n      \"price\": 29000,\n      \"publisher\": null,\n      \"sale_price\": 26100,\n      \"status\": \"정상판매\",\n      \"thumbnail\": \"https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F923727%3Ftimestamp%3D20220103152724\",\n      \"title\": \"자바를 다루는 기술\",\n      \"translators\": [],\n      \"url\": \"https://search.daum.net/search?w=bookpage&bookId=923727&q=%EC%9E%90%EB%B0%94%EB%A5%BC+%EB%8B%A4%EB%A3%A8%EB%8A%94+%EA%B8%B0%EC%88%A0\"}],\"meta\": {\"is_end\": false,\"pageable_count\": 1000,\"total_count\": 4759}}";
        KakaoBook kakaoBook = objectMapper.readValue(result, KakaoBook.class);

        when(bookSearchProperties.getUri()).thenReturn(apiUri);
        when(bookFetcher.fetchBooks(any(), any())).thenReturn(kakaoBook);

        BookSearchResponse bookSearchResponse = bookSearchService.searchByQuery(bookSearchRequest);

        assertAll(
            () -> assertThat(bookSearchResponse.getBookResponses().size()).isOne(),
            () -> assertThat(bookSearchResponse.getMeta().getTotal_count()).isOne()
        );
    }
}