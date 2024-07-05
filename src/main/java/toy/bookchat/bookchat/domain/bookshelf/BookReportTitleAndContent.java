package toy.bookchat.bookchat.domain.bookshelf;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BookReportTitleAndContent {

    private String title;
    private String content;

    @Builder
    private BookReportTitleAndContent(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
