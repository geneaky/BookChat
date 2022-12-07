package toy.bookchat.bookchat.domain.common;

import lombok.Getter;
import org.springframework.data.domain.Slice;

@Getter
public final class CursorMeta<T> {

    private final int sliceSize;
    private final int contentSize;
    private final boolean hasContent;
    private final boolean isFirst;
    private final boolean isLast;
    private final boolean hasNext;

    private final T nextCursorId;

    private CursorMeta(Slice<?> slice, T nextCursorId) {
        this.sliceSize = slice.getPageable().getPageSize();
        this.contentSize = slice.getContent().size();
        this.hasContent = slice.hasContent();
        this.isFirst = slice.isFirst();
        this.isLast = slice.isLast();
        this.hasNext = slice.hasNext();
        this.nextCursorId = nextCursorId;
    }

    public static <T> CursorMeta<T> from(Slice<?> slice, T nextCursorId) {
        return new CursorMeta<>(slice, nextCursorId);
    }
}
