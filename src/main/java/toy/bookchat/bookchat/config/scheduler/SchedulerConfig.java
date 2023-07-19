package toy.bookchat.bookchat.config.scheduler;

import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@EnableScheduling
@Configuration
public class SchedulerConfig implements SchedulingConfigurer {

    private final Integer POOL_SIZE = 5;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler executor = new ThreadPoolTaskScheduler();
        executor.setPoolSize(POOL_SIZE);
        executor.setRejectedExecutionHandler(new CallerRunsPolicy());
        executor.initialize();
        taskRegistrar.setTaskScheduler(executor);
    }
}
