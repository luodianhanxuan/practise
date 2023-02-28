package com.wangjg.outbox.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OutboxRequestDto implements Serializable {

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
     * 扩展字段
     */
    private String extend;
}
