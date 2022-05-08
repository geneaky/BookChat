package toy.bookchat.bookchat.domain.book.dto;

import lombok.Getter;

@Getter
public class Meta {

    private Boolean is_end;
    private Integer pageable_count;
    private Integer total_count;
}
