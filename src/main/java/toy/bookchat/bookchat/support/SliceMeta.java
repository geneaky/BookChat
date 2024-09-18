package toy.bookchat.bookchat.support;

import lombok.Getter;
import org.springframework.data.domain.Slice;

@Getter
public final class SliceMeta {

  private final int sliceSize;
  private final int sliceNumber;
  private final int contentSize;
  private final boolean hasContent;
  private final boolean isFirst;
  private final boolean isLast;
  private final boolean hasNext;
  private final boolean hasPrevious;


  private SliceMeta(Slice<?> slice) {
    this.sliceSize = slice.getPageable().getPageSize();
    this.sliceNumber = slice.getPageable().getPageNumber();
    this.contentSize = slice.getContent().size();
    this.hasContent = slice.hasContent();
    this.isFirst = slice.isFirst();
    this.isLast = slice.isLast();
    this.hasNext = slice.hasNext();
    this.hasPrevious = slice.hasPrevious();
  }

  public static SliceMeta from(Slice<?> slice) {
    return new SliceMeta(slice);
  }
}
