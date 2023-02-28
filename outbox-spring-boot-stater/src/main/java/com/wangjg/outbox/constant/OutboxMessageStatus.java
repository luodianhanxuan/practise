package com.wangjg.outbox.constant;

import lombok.Getter;

@Getter
public enum OutboxMessageStatus {
    DELETE(-1, "已删除"),
    SENDING(0, "投递中"),
    SENDED(1, "已发送"),
    RETRY_LIMITED(2, "重试次数过多")
    ;

    private final int type;
    private final String msg;

    OutboxMessageStatus(int type, String msg) {
        this.type = type;
        this.msg = msg;
    }
}
