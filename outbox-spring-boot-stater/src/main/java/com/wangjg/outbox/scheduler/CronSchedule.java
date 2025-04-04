package com.wangjg.outbox.scheduler;

import com.wangjg.outbox.config.OutboxProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.util.CollectionUtils;

import java.util.Set;

public class CronSchedule implements InitializingBean,
        ApplicationContextAware {

    private final OutboxProperties outboxProperties;
    private ApplicationContext applicationContext;
    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;

    public CronSchedule(OutboxProperties outboxProperties,
                        ThreadPoolTaskScheduler threadPoolTaskScheduler) {

        this.outboxProperties = outboxProperties;
        this.threadPoolTaskScheduler = threadPoolTaskScheduler;
    }

    @Override
    public void afterPropertiesSet() {
        // 单独设置调度任务的场景
        Set<String> tasksOfReferSceneType = outboxProperties.getSingletonTaskSceneType();
        if (!CollectionUtils.isEmpty(tasksOfReferSceneType)) {
            tasksOfReferSceneType
                    .forEach(sceneType -> {
                        DeliveryTask task = applicationContext.getBean(DeliveryTask.class).setExclusive(true).setSceneType(sceneType);
                        threadPoolTaskScheduler.schedule(task,
                                new CronTrigger(outboxProperties.getTaskConfigVal(sceneType, OutboxProperties.TaskProp::getCron)));
                    });
        }

        // system 默认调度
        DeliveryTask task = applicationContext.getBean(DeliveryTask.class).setExclusive(false).setExclusionSceneTypes(tasksOfReferSceneType);
        threadPoolTaskScheduler.schedule(task, new CronTrigger(outboxProperties.getTaskConfigVal(null, OutboxProperties.TaskProp::getCron)));
    }


    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
