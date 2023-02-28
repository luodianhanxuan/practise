package com.wangjg.outbox.dto;

import com.wangjg.outbox.exception.OutboxException;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.time.Duration;
import java.util.Date;

import static org.springframework.util.ObjectUtils.isEmpty;

@Data
public class OutboxDto {

    /**
     * 业务场景类型
     */
    private String sceneType;

    /**
     * 业务代码,标识消息业务类型
     */
    private String businessCode;

    /**
     * 通知任务请求编号
     */
    private String requestId;

    /**
     * 同一个上下文多个不同方式的消息通知内容
     */
    private String sendMethod;

    /**
     * 投递起始时间,精度分钟,默认当前时间
     */
    private Date startTime;

    /**
     * 下次发送时间,精度分钟,默认当前时间
     */
    private Date nextTime;

    /**
     * 消息体
     */
    private String payload;

    /**
     * 消息回执
     */
    private String response;

    /**
     * 需要重试次数
     */
    private Integer tryTimes;

    /**
     * 已经发送过的次数
     */
    private Integer triedTimes;

    /**
     * 重试间隔：单位分钟
     */
    private Duration retryInterval;

    /**
     * 是否需要回执: 0:否,1:是
     */
    private Integer needReceipt;

    /**
     * 扩展字段
     */
    private String extend;

    public static InternalBuilder builder() {
        return new InternalBuilder();
    }

    @Data
    @Accessors(chain = true)
    public static class InternalBuilder {
        /**
         * 业务场景类型
         */
        private String sceneType;

        /**
         * 业务代码,标识消息业务类型
         */
        private String businessCode;

        /**
         * 通知任务请求编号
         */
        private String requestId;

        /**
         * 同一个上下文多个不同方式的消息通知内容
         */
        private String sendMethod;

        /**
         * 投递起始时间,精度分钟,默认当前时间
         */
        private Date startTime;

        /**
         * 下次发送时间,精度分钟,默认当前时间
         */
        private Date nextTime;

        /**
         * 消息体
         */
        private String payload;

        /**
         * 需要重试次数
         */
        private Integer tryTimes;

        /**
         * 已经发送过的次数
         */
        private Integer triedTimes;

        /**
         * 重试间隔：单位分钟
         */
        private Duration retryInterval;

        /**
         * 是否需要回执: 0:否,1:是
         */
        private Integer needReceipt;

        /**
         * 扩展字段
         */
        private String extend;

        public OutboxDto build() {
            if (isEmpty(sceneType)) {
                throw new OutboxException("业务场景类型不能为空");
            }
            if (isEmpty(businessCode)) {
                throw new OutboxException("业务场景代码不能为空");
            }
            if (isEmpty(requestId)) {
                throw new OutboxException("通知任务请求编号不能为空");
            }
            if (isEmpty(sendMethod)) {
                throw new OutboxException("发送方式不能为空");
            }

            OutboxDto retVal = new OutboxDto();
            BeanUtils.copyProperties(this, retVal);
            return retVal;
        }
    }

    public static void main(String[] args) {
        OutboxDto build = OutboxDto.builder().setRetryInterval(Duration.ofDays(2).plusMinutes(2))
                .setSceneType("123").setSendMethod("async").setBusinessCode("123").setRequestId("123123").build();
        System.out.println(build.getRetryInterval());
    }
}
