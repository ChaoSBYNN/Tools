在Spring中创建定时任务可以通过两种方式：使用注解@Scheduled或者使用配置类继承SchedulingConfigurer接口。

使用@Scheduled注解

在Spring配置类上添加@EnableScheduling注解来启用定时任务。

创建定时任务方法，并使用@Scheduled注解来指定任务的执行计划。

```java
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
 
@EnableScheduling
@Component
public class ScheduledTasks {
 
    @Scheduled(fixedRate = 1000)
    public void reportCurrentTime() {
        System.out.println("现在时间：" + System.currentTimeMillis());
    }
}
```

使用SchedulingConfigurer接口

实现SchedulingConfigurer接口。

重写configureTasks方法，在其中定义定时任务。

```java
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.context.annotation.Configuration;
 
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
 
@Configuration
public class SchedulerConfig implements SchedulingConfigurer {
 
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
        taskRegistrar.addTriggerTask(
            // 定义执行任务内容
            () -> System.out.println("使用SchedulingConfigurer运行定时任务：" + System.currentTimeMillis()),
            // 定义执行周期
            triggerContext -> new CronTrigger("0/5 * * * * ?").nextExecutionTime(triggerContext)
        );
    }
 
    // 使用定长线程池
    public Executor taskExecutor() {
        return Executors.newScheduledThreadPool(5);
    }
}
```

两种方式都可以创建定时任务，使用@Scheduled注解更为简洁，但如果需要更复杂的定时任务调度，如使用cron表达式，则需要实现SchedulingConfigurer接口。
