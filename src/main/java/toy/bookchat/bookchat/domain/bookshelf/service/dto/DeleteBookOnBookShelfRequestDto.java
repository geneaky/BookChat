package toy.bookchat.bookchat.domain.bookshelf.service.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.ISBN;
import org.hibernate.validator.constraints.ISBN.Type;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DeleteBookOnBookShelfRequestDto {

    @ISBN(type = Type.ANY)
    private String isbn;

    public DeleteBookOnBookShelfRequestDto(String isbn) {
        this.isbn = isbn;
    }
}
