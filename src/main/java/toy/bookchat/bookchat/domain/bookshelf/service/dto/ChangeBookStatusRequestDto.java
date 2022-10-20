package toy.bookchat.bookchat.domain.bookshelf.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChangeBookStatusRequestDto {

    @NotNull
    @JsonProperty("readingStatus")
    private ReadingStatus readingStatus;

    public ChangeBookStatusRequestDto(ReadingStatus readingStatus) {
        this.readingStatus = readingStatus;
    }
}
