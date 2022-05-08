package toy.bookchat.bookchat.domain.book.dto;

import lombok.Getter;

@Getter
public class Document {

    private String[] authors;
    private String contents;
    private String datetime;
    private String isbn;
    private Integer price;
    private String publisher;
    private Integer sale_price;
    private String status;
    private String thumbnail;
    private String title;
    private String[] translators;
    private String url;
}
