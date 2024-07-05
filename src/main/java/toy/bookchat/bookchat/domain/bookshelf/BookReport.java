package toy.bookchat.bookchat.domain.bookshelf;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BookReport {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime reportedAt;

    @Builder
    private BookReport(Long id, String title, String content, LocalDateTime reportedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.reportedAt = reportedAt;
    }
}
