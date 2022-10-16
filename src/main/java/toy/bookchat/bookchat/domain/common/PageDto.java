package toy.bookchat.bookchat.domain.common;

import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public abstract class PageDto {

    private final Long totalElements;

    private final int totalPages;

    private final int pageSize;

    private final int pageNumber;

    private final Long offset;

    private final boolean first;

    private final boolean last;

    private final boolean empty;

    public PageDto(Page<?> page) {
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.pageSize = page.getPageable().getPageSize();
        this.pageNumber = page.getPageable().getPageNumber();
        this.offset = page.getPageable().getOffset();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.empty = page.isEmpty();
    }
}
