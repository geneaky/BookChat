package toy.bookchat.bookchat.domain.book.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Meta {

    private Boolean is_end;
    private Integer pageable_count;
    private Integer total_count;
}
