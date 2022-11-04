package toy.bookchat.bookchat.domain.bookshelf.service.dto.request;

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

    /* TODO: 2022-11-04 독서 완료시 별점 입력하는 필드 추가
     */
    public ChangeBookStatusRequestDto(ReadingStatus readingStatus) {
        this.readingStatus = readingStatus;
    }
}
