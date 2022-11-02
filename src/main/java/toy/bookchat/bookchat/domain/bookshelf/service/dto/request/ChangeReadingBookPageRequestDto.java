package toy.bookchat.bookchat.domain.bookshelf.service.dto.request;

import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChangeReadingBookPageRequestDto {

    @NotNull
    private Integer pages;

    public ChangeReadingBookPageRequestDto(Integer pages) {
        this.pages = pages;
    }
}
