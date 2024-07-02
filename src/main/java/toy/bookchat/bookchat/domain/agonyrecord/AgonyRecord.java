package toy.bookchat.bookchat.domain.agonyrecord;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class AgonyRecord {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    @Builder
    private AgonyRecord(Long id, String title, String content, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    public String getCreateTimeInYearMonthDayFormat() {
        return createdAt.toLocalDate().toString();
    }
}
