package toy.bookchat.bookchat.domain.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

class RateLimiterAopTest {

    AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();

    @Test
    void pointcut_매칭_성공() throws Exception {
        pointcut.setExpression("@annotation(toy.bookchat.bookchat.domain.common.RateLimit)");
        assertThat(pointcut.matches(RateLimit.class)).isTrue();
    }
}