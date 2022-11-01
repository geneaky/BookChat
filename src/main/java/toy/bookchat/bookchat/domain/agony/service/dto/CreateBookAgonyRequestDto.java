package toy.bookchat.bookchat.domain.agony.service.dto;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateBookAgonyRequestDto {

    @NotBlank
    private String title;
    @NotBlank
    private String hexColorCode;

    public Agony getAgony(BookShelf bookShelf) {
        return Agony.builder()
            .hexColorCode(this.hexColorCode)
            .bookShelf(bookShelf)
            .build();
    }
}
