package com.wangjg.outbox.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@ConfigurationProperties("outbox")
@Data
public class OutboxProperties {

    /**
     * 通用配置，系统默认配置。没有特定场景时应用此命名空间
     */
    public IntervalProp sys = new IntervalProp();


    /**
     * 特定场景配置
     */
    public Map<String, IntervalProp> scene = new HashMap<>();

    public OutboxProperties() {
        //  ---------------------------  给默认值  ----------------------------

        // 调度配置
        TaskProp taskProp = new TaskProp();
        taskProp.setEnable(true);
        taskProp.setJobBatch(50);
        taskProp.setCron("0 0/1 * * * ?");
        taskProp.setEnableSingleTask(true);

        // 消息传递配置
        DeliveryProp deliveryProp = new DeliveryProp();
        deliveryProp.setMaxTryTime(10);
        deliveryProp.setRetryInterval(Duration.ofMinutes(30));

        sys.setDelivery(deliveryProp);
        sys.setTask(taskProp);
        scene.put("sys", sys);
    }

    public <T> T getTaskConfigVal(String sceneType, Function<TaskProp, T> getter) {
        TaskProp task = null;
        if (sceneType != null && scene.containsKey(sceneType)) {
            task = scene.get(sceneType).getTask();
        }
        TaskProp sysTask = scene.get("sys").getTask();

        return Objects.isNull(task) ? getter.apply(sysTask) : getter.apply(task);
    }


    public <T> T getDeliveryConfigVal(String sceneType, Function<DeliveryProp, T> getter) {
        DeliveryProp delivery = null;
        if (sceneType != null && scene.containsKey(sceneType)) {
            delivery = scene.get(sceneType).getDelivery();
        }

        DeliveryProp sysDelivery = scene.get("sys").getDelivery();

        return Objects.isNull(delivery) ? getter.apply(sysDelivery) : getter.apply(delivery);
    }

    public Set<String> getSingletonTaskSceneType() {

        return scene.keySet().stream()
                .filter(key -> !Objects.equals("sys", key))
                .filter(key -> {
                    IntervalProp intervalProp = scene.get(key);
                    TaskProp task = intervalProp.getTask();
                    return task.enableSingleTask;
                }).collect(Collectors.toSet());
    }

    @Getter
    @Setter
    public static class IntervalProp {

        /**
         * 调度配置
         */
        public TaskProp task;

        /**
         * 消息单独配置
         */
        public DeliveryProp delivery;

    }

    @Getter
    @Setter
    public static class TaskProp {

        /**
         * 此场景是否单独调度，创建调度时用到此设置
         */
        public Boolean enableSingleTask;

        /**
         * 功能开关：若通过此配置此单独调度场景调度功能开关
         */
        public Boolean enable;

        /**
         * 分批次查询待发送的消息
         */
        private Integer jobBatch;

        /**
         * 调度 cron 表达式
         */
        private String cron;
    }

    @Getter
    @Setter
    public static class DeliveryProp {
        /**
         * 最大重试次数
         */
        private Integer maxTryTime;

        /**
         * 重试间隔，支持最小单位为毫秒
         */
        private Duration retryInterval;
    }
}
