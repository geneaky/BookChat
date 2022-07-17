package toy.bookchat.bookchat.domain.bookshelf;

public enum Star {
    ZERO(0),
    HALF(0.5f),
    ONE(1),
    ONE_HALF(1.5f),
    TWO(2),
    TWO_HALF(2.5f),
    THREE(3),
    THREE_HALF(3.5f),
    FOUR(4),
    FOUR_HALF(4.5f),
    FIVE(5);

    private final float star;

    Star(float star) {
        this.star = star;
    }
}
