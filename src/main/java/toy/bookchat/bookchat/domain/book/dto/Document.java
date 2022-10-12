package toy.bookchat.bookchat.domain.book.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
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
}
