package toy.bookchat.bookchat.domain.common;

import java.util.List;
import java.util.function.Function;
import lombok.Getter;
import org.springframework.data.domain.Slice;

@Getter
public final class CursorMeta<T, R> {

    private final int sliceSize;
    private final int contentSize;
    private final boolean hasContent;
    private final boolean isFirst;
    private final boolean isLast;
    private final boolean hasNext;

    private final R nextCursorId;

    public CursorMeta(Slice<T> slice, Function<T, R> getId) {
        this.sliceSize = slice.getPageable().getPageSize();
        this.contentSize = slice.getContent().size();
        this.hasContent = slice.hasContent();
        this.isFirst = slice.isFirst();
        this.isLast = slice.isLast();
        this.hasNext = slice.hasNext();
        this.nextCursorId = getNextCursorId(slice.getContent(), getId);
    }

    private R getNextCursorId(List<T> content, Function<T, R> getId) {
        return content.stream().map(getId).reduce((id1, id2) -> id2).orElse(null);
    }
}
