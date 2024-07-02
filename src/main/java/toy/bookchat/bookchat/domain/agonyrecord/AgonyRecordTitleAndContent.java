package toy.bookchat.bookchat.domain.agonyrecord;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AgonyRecordTitleAndContent {

    private String title;
    private String content;

    @Builder
    private AgonyRecordTitleAndContent(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
