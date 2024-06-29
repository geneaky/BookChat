package toy.bookchat.bookchat.domain.agony.api.v1.request;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.db_module.bookshelf.BookShelfEntity;
import toy.bookchat.bookchat.domain.agony.Agony;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateBookAgonyRequest {

    @NotBlank
    private String title;
    @NotBlank
    private String hexColorCode;

    @Builder
    private CreateBookAgonyRequest(String title, String hexColorCode) {
        this.title = title;
        this.hexColorCode = hexColorCode;
    }

    public toy.bookchat.bookchat.db_module.agony.AgonyEntity getAgony(BookShelfEntity bookShelfEntity) {
        return toy.bookchat.bookchat.db_module.agony.AgonyEntity.builder()
            .title(this.title)
            .hexColorCode(this.hexColorCode)
            .bookShelfEntity(bookShelfEntity)
            .build();
    }

    public Agony toTarget() {
        return Agony.builder()
            .title(this.title)
            .hexColorCode(this.hexColorCode)
            .build();
    }
}
