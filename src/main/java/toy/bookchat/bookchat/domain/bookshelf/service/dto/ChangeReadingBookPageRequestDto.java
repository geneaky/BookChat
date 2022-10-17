package toy.bookchat.bookchat.domain.bookshelf.service.dto;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.ISBN;
import org.hibernate.validator.constraints.ISBN.Type;

@Getter
public class ChangeReadingBookPageRequestDto {

    @ISBN(type = Type.ANY)
    private String isbn;
    @NotNull
    private Integer pages;

    public ChangeReadingBookPageRequestDto(String isbn, Integer pages) {
        this.isbn = isbn;
        this.pages = pages;
    }
}
