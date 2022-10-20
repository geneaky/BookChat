package toy.bookchat.bookchat.domain.bookshelf.service.dto;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChangeReadingBookPageRequestDto {

    @NotNull
    private Integer pages;

    public ChangeReadingBookPageRequestDto(Integer pages) {
        this.pages = pages;
    }
}
