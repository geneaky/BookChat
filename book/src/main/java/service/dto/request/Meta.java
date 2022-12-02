package service.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Meta {

    private Boolean is_end;
    private Integer pageable_count;
    private Integer total_count;

    @Builder
    private Meta(Boolean is_end, Integer pageable_count, Integer total_count) {
        this.is_end = is_end;
        this.pageable_count = pageable_count;
        this.total_count = total_count;
    }
}
