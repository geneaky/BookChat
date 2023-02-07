package toy.bookchat.bookchat.domain.common;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import java.time.Duration;

public class RateLimiterImpl implements RateLimiter {

    public static final int TOKEN_PER_REQUEST = 1;
    private final Bucket bucket;

    public RateLimiterImpl(long capacity, long tokens, long seconds) {
        this.bucket = Bucket.builder()
            .addLimit(
                Bandwidth.classic(capacity, Refill.greedy(tokens, Duration.ofSeconds(seconds))))
            .build();
    }

    @Override
    public boolean tryConsume() {
        return this.bucket.tryConsume(TOKEN_PER_REQUEST);
    }
}
