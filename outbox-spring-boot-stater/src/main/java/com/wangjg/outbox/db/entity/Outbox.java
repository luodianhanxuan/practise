package com.wangjg.outbox.db.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Outbox implements Serializable {

    /**
     * 消息主键
     */
    private Long id;

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
     * 响应体
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
    private String retryInterval;

    /**
     * 消息状态,DELETE(-1, "已删除");SENDING(0, "投递中");HANDLED(1, "已处理")
     */
    private Integer sendStatus;

    /**
     * 是否需要消息回执: 0 否, 1 是
     */
    private Integer needReceipt;

    /**
     * 记录创建时间
     */
    private Date createAt;

    /**
     * 记录更新时间
     */
    private Date updateAt;

    /**
     * 扩展字段
     */
    private String extend;
}
