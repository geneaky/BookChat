package toy.bookchat.bookchat.domain.agony.service.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateBookAgonyRequest {

    @NotBlank
    private String title;
    @NotBlank
    private String hexColorCode;

    public Agony getAgony(BookShelf bookShelf) {
        return Agony.builder()
            .title(this.title)
            .hexColorCode(this.hexColorCode)
            .bookShelf(bookShelf)
            .user(bookShelf.getUser())
            .build();
    }

    public CreateBookAgonyRequest(String title, String hexColorCode) {
        this.title = title;
        this.hexColorCode = hexColorCode;
    }
}
