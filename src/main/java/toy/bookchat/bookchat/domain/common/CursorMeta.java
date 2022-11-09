package toy.bookchat.bookchat.domain.common;

import lombok.Getter;
import org.springframework.data.domain.Slice;

@Getter
public final class CursorMeta {

    private final int sliceSize;
    private final int contentSize;
    private final boolean hasContent;
    private final boolean isFirst;
    private final boolean isLast;
    private final boolean hasNext;
    private final boolean hasPrevious;


    private CursorMeta(Slice<?> slice) {
        this.sliceSize = slice.getPageable().getPageSize();
        this.contentSize = slice.getContent().size();
        this.hasContent = slice.hasContent();
        this.isFirst = slice.isFirst();
        this.isLast = slice.isLast();
        this.hasNext = slice.hasNext();
        this.hasPrevious = slice.hasPrevious();
    }

    public static CursorMeta from(Slice<?> slice) {
        return new CursorMeta(slice);
    }
}
