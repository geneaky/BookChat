package toy.bookchat.bookchat.support;

import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public final class PageMeta {

  private final Long totalElements;

  private final int totalPages;

  private final int pageSize;

  private final int pageNumber;

  private final Long offset;

  private final boolean first;

  private final boolean last;

  private final boolean empty;

  private PageMeta(Page<?> page) {
    this.totalElements = page.getTotalElements();
    this.totalPages = page.getTotalPages();
    this.pageSize = page.getPageable().getPageSize();
    this.pageNumber = page.getPageable().getPageNumber();
    this.offset = page.getPageable().getOffset();
    this.first = page.isFirst();
    this.last = page.isLast();
    this.empty = page.isEmpty();
  }

  public static PageMeta from(Page<?> page) {
    return new PageMeta(page);
  }
}
