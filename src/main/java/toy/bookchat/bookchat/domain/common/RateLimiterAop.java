package toy.bookchat.bookchat.domain.common;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.exception.toomanyrequests.RateOverLimitException;

@Aspect
@Component
public class RateLimiterAop {

    private final ConcurrentHashMap<String, RateLimiter> rateLimiterMappingTable = new ConcurrentHashMap<>();

    @Pointcut("@annotation(toy.bookchat.bookchat.domain.common.RateLimit)")
    public void pointCut() {
    }

    @Before("pointCut()")
    public void rateLimit(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);
        RateLimiter rateLimiter = rateLimiterMappingTable.putIfAbsent(rateLimit.keyName(),
            new RateLimiterImpl(rateLimit.capacity(), rateLimit.tokens(), rateLimit.seconds()));

        if (!rateLimiter.tryConsume()) {
            throw new RateOverLimitException();
        }
        /* TODO: 2023-02-07 ip를 별도로 기억해서 차단하는 방식으로하기엔 메모리가 부족하기 때문에 초당 보낼 수 있는 전체 호출 횟수를 제한.
            (추후 ip방식 혹은 어플 스토어 계정을 메모리나 db에 넣는 방식으로 바꿀 수도 있음.)
         */
    }
}
