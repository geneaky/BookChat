package toy.bookchat.bookchat.domain.book.service.dto.request;

import static org.springframework.util.StringUtils.hasText;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Document {

    private List<String> authors;
    private String contents;
    private String datetime;
    private String isbn;
    private Integer price;
    private String publisher;
    private Integer sale_price;
    private String status;
    private String thumbnail;
    private String title;
    private List<String> translators;
    private String url;

    public String getYearMonthDay() {
        return this.datetime.substring(0, 10);
    }

    public boolean hasPerfectDocument() {
        if (!hasText(thumbnail)) {
            return false;
        }

        if (!hasText(datetime)) {
            return false;
        }

        if (!hasText(publisher)) {
            return false;
        }

        if (!hasText(isbn)) {
            return false;
        }

        if (!hasText(title)) {
            return false;
        }

        if (hasNotAuthor()) {
            return false;
        }

        return true;
    }

    private boolean hasNotAuthor() {
        return authors == null || authors.isEmpty();
    }
}
