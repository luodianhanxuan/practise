create table OUTBOX
(
    id             integer primary key comment '消息主键',
    scene_type     varchar(64)               not null comment '业务场景类型',
    business_code  varchar(128)              not null comment '业务代码,标识消息业务类型',
    request_id     varchar(64)               not null comment '通知任务请求编号',
    send_method    varchar(32)               not null comment '发送方式：同一个业务可多种不同方式的消息通知内容：同步: sync、异步：async',
    start_time     date                      not null comment '投递起始时间,精度分钟,默认当前时间',
    next_time      date          default null comment '已经重试过的次数',
    payload        text                      not null comment '消息体',
    response       text          default null comment '消息回执内容（备用字段）',
    try_times      integer       default null comment '总共发送次数',
    tried_times    integer(10)   default null comment '已经重试过的次数',
    retry_interval varchar(100)  default null comment '重试间隔',
    send_status    int(1)        DEFAULT '0' not null comment '消息状态,DELETE(-1, "已删除");SENDING(0, "投递中");SENDED(1, "已处理"); RETRY_LIMITED(2, "重试次数过多")',
    need_receipt   int(1)        DEFAULT '0' not null comment '是否需要消息回执：0 否,1 是',
    create_at      date comment '记录创建时间',
    update_at      date comment '记录更新时间',
    extend         varchar(1024) not null comment '扩展字段'
) comment '消息投递任务表';

create index MESSAGE_DELIVER_SCENE_TYPE_REQUEST_ID_SEND_METHOD on OUTBOX (scene_type, request_id, send_method);
create index MESSAGE_DELIVER_SCENE_TYPE_MESSAGE_CODE on OUTBOX (scene_type, business_code);
create index MESSAGE_DELIVER_SCENE_TYPE on OUTBOX (scene_type);
create index MESSAGE_DELIVER_NEXT_TIME on OUTBOX (next_time);