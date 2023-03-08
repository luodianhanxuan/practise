package com.wangjg.outbox.dto;

import com.wangjg.outbox.exception.OutboxException;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

import static org.springframework.util.ObjectUtils.isEmpty;

@Data
public class OutboxResponseDto implements Serializable {

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
     * 消息体
     */
    private String payload;

    /**
     * 响应消息体
     */
    private String response;

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
         * 消息体
         */
        private String payload;

        /**
         * 响应体
         */
        private String response;

        /**
         * 扩展字段
         */
        private String extend;

        public OutboxResponseDto build() {
            if (isEmpty(getSceneType())) {
                throw new OutboxException("业务场景不能为空");
            }
            if (isEmpty(getSendMethod())) {
                throw new OutboxException("发送方式不能为空");
            }
            if (isEmpty(getBusinessCode())) {
                throw new OutboxException("业务代码不能为空");
            }
            if (isEmpty(getRequestId())) {
                throw new OutboxException("请求编号不能为空");
            }

            OutboxResponseDto retVal = new OutboxResponseDto();
            BeanUtils.copyProperties(this, retVal);
            return retVal;
        }
    }
}
