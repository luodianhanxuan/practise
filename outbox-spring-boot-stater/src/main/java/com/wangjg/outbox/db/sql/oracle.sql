create table OUTBOX
(
    id             number                    not null primary key,
    scene_type     varchar(64)               not null,
    business_code  varchar(128)              not null,
    request_id     varchar(64)               not null,
    send_method    varchar(32)               not null,
    start_time     date,
    next_time      date,
    payload        clob,
    response       clob,
    try_times      number(10)    DEFAULT '10',
    tried_times    number(10)    DEFAULT '0',
    retry_interval varchar2(100),
    send_status    number(1)     DEFAULT '0' not null,
    need_receipt   number(1)     DEFAULT '0' not null,
    create_at      date,
    update_at      date,
    extend         varchar(1024) DEFAULT ''
);

create index MESSAGE_DELIVER_SCENE_TYPE_REQUEST_ID_SEND_METHOD on OUTBOX (scene_type, request_id, send_method);
create index MESSAGE_DELIVER_SCENE_TYPE_MESSAGE_CODE on OUTBOX (scene_type, business_code);
create index MESSAGE_DELIVER_SCENE_TYPE on OUTBOX (scene_type);
create index MESSAGE_DELIVER_NEXT_TIME on OUTBOX (next_time);


comment on table OUTBOX is '消息投递任务表';
comment on column OUTBOX.id is '消息主键';
comment on column OUTBOX.scene_type is '业务场景类型';
comment on column OUTBOX.business_code is '业务代码,标识消息业务类型';
comment on column OUTBOX.request_id is '通知任务请求编号';
comment on column OUTBOX.send_method is '发送方式：同一个业务可多种不同方式的消息通知内容：同步: sync、异步：async';
comment on column OUTBOX.start_time is '投递起始时间,精度分钟,默认当前时间';
comment on column OUTBOX.next_time is '已经重试过的次数';
comment on column OUTBOX.payload is '消息体';
comment on column OUTBOX.response is '消息回执内容（备用字段）';
comment on column OUTBOX.try_times is '总共发送次数';
comment on column OUTBOX.tried_times is '已经重试过的次数';
comment on column OUTBOX.retry_interval is '重试间隔';
comment on column OUTBOX.send_status is '消息状态,DELETE(-1, "已删除");SENDING(0, "投递中");SENDED(1, "已处理"); RETRY_LIMITED(2, "重试次数过多")';
comment on column OUTBOX.need_receipt is '是否需要消息回执：0 否,1 是';
comment on column OUTBOX.create_at is '记录创建时间';
comment on column OUTBOX.update_at is '记录更新时间';
comment on column OUTBOX.extend is '扩展字段';


create sequence S_OUTBOX;