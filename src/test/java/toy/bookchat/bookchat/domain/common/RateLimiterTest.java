package toy.bookchat.bookchat.domain.common;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.github.bucket4j.local.LocalBucket;
import io.github.bucket4j.local.SynchronizationStrategy;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

class RateLimiterTest {

    @Test
    @Disabled("bucket4j 학습 테스트")
    void greedy_요청제한_테스트() throws Exception {
        LocalBucket bucket = Bucket.builder()
            //토큰 최대 2개 들고 있을 수 있는 버킷에서
            //5초에 1개씩 버켓이 생성될때
            .addLimit(Bandwidth.classic(2, Refill.greedy(2, Duration.ofSeconds(10))))
            .build();

        int result = 0;
        for (int i = 0; i < 3; i++) {
            if (bucket.tryConsume(1)) {
                result++;
            }
            Thread.sleep(2500); // 1,2번째 토큰을 뽑고 2.5초씩 2번 대기하면 총 5초가 지난뒤 토큰 하나가 채워짐
        }

        assertThat(result).isEqualTo(3);
    }

    @Test
    @Disabled("bucket4j 학습 테스트")
    void interval_요청제한_테스트() throws Exception {
        LocalBucket bucket = Bucket.builder()
            //토큰 최대 2개 들고 있을 수 있는 버킷에서
            //10초 기다린 후 토큰 2개 생성됨
            .addLimit(Bandwidth.classic(2, Refill.intervally(2, Duration.ofSeconds(10))))
            .build();

        int result = 0;

        for (int i = 0; i < 4; i++) {
            if (bucket.tryConsume(1)) {
                result++;
            }
            if (i == 1) { //토큰 다 쓰고 10초 기달려야 다시 채워짐
                Thread.sleep(10000);
            }
        }

        assertThat(result).isEqualTo(4);
    }

    @RepeatedTest(100)
    @Disabled("bucket4j 학습 테스트")
    void 멀티스레드_요청제한_테스트() throws Exception {
        //기본전략 lock free >> 높은 race condition과 스레드가 서로 블로킹하지 않는다
        //synchronized >> race condition을 고려하지 않고 메모리 할당없이 사용 / 다른 스레드를 블로킹함
        // none >> 싱글스레드에서 사용하는 전략 >> 동시성 문제 발생
        LocalBucket bucket = Bucket.builder()
            .addLimit(
                Bandwidth.classic(100, Refill.greedy(1, Duration.ofSeconds(100)))) //토큰 50개가지고 테스트
            .withSynchronizationStrategy(SynchronizationStrategy.LOCK_FREE)
            .build();

        int THREAD_COUNT = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(300);
        AtomicInteger result = new AtomicInteger();
        //동시성 문제가 발생한다면 결과가 300보다 작게나올것
        for (int i = 0; i < THREAD_COUNT; i++) {
            //thread 10개에서 30개씩 토큰 받아서 카운트
            executorService.execute(() -> {
                for (int j = 0; j < 30; j++) {
                    if (bucket.tryConsume(1)) {
                        result.addAndGet(1);
                    }
                    latch.countDown();
                }
            });
        }
        latch.await();

        assertThat(result.get()).isEqualTo(100);
    }


}