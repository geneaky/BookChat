package toy.bookchat.bookchat.domain.book.service.dto.request;

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
        if (authors == null) {
            return false;
        }

        if (datetime == null) {
            return false;
        }

        if (isbn == null) {
            return false;
        }

        if (publisher == null) {
            return false;
        }

        if (title == null) {
            return false;
        }

        if (thumbnail == null) {
            return false;
        }

        return true;
    }
}
