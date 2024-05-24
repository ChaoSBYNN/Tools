# 

```java
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author Spike_Zhang
 * @description Spring定时任务
 * TODO 分布式部署多节点时改用xxl-job
 */
@Slf4j
@EnableScheduling
@Configuration
public class SchedulerConfig implements SchedulingConfigurer {

    @Resource
    private ExpirationNotifyHelper expirationNotifyHelper;

    @PostConstruct
    public void init() {
        log.info("process run starting scheduler");
//        expirationNotifyHelper.execute();
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
        taskRegistrar.addTriggerTask(
            // 定义执行任务内容
            () -> {
                log.info("starting contract scheduler");
                expirationNotifyHelper.execute();
            },
            // 定义执行周期 每日执行
            triggerContext -> new CronTrigger("0 0 1 * * ?").nextExecutionTime(triggerContext)
        );
    }
 
    // 使用定长线程池
    public Executor taskExecutor() {
        return Executors.newScheduledThreadPool(3);
    }
}
```
