package toy.bookchat.bookchat.domain.common;

public interface RateLimiter {

    boolean tryConsume();
}
