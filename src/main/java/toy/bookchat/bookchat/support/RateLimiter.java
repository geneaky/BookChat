package toy.bookchat.bookchat.support;

public interface RateLimiter {

  boolean tryConsume();
}
