package com.wangjg.outbox;

import com.alibaba.fastjson.JSON;
import com.wangjg.outbox.config.OutboxProperties;
import com.wangjg.outbox.constant.OutboxMessageStatus;
import com.wangjg.outbox.db.entity.Outbox;
import com.wangjg.outbox.db.repo.OutboxRepository;
import com.wangjg.outbox.dto.MessageBrokerIdentifier;
import com.wangjg.outbox.dto.OutboxDto;
import com.wangjg.outbox.dto.OutboxRequestDto;
import com.wangjg.outbox.dto.OutboxResponseDto;
import com.wangjg.outbox.exception.OutboxException;
import com.wangjg.outbox.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.springframework.util.ObjectUtils.isEmpty;

/**
 * 消处理器抽象模板
 */
@Slf4j
public abstract class MessageBroker implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private final OutboxRepository outboxRepository;

    private final OutboxProperties outboxProperties;

    private ApplicationContext applicationContext;

    public MessageBroker() {
        outboxRepository = applicationContext.getBean(OutboxRepository.class);
        outboxProperties = applicationContext.getBean(OutboxProperties.class);
    }

    @Override
    public void initialize(@NonNull ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 异步发消息的线程池
     */
    private final ExecutorService executorService = new ThreadPoolExecutor(5, 10, 60, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1000),
            r -> {
                Thread thread = new Thread(r);
                thread.setName("outboxMessageHandlerThread");
                //设置异常捕获器
                thread.setUncaughtExceptionHandler((t, e) -> log.error(String.format("消息投递发送异常 e: %s", e.getMessage())));
                return thread;
            }, new ThreadPoolExecutor.AbortPolicy());

    protected abstract List<MessageBrokerIdentifier> getBrokerIdentifier();

    /**
     * 投递消息 方式可以是:(1)异步 MQ / (2)同步: http，在此方法内真正发起请求
     */
    protected abstract void doDeliverMessage(OutboxRequestDto reqDTO) throws OutboxException;


    /**
     * 模板-投递消息 加事务
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.SUPPORTS)
    public void sendMsg(OutboxDto reqDTO) {
        boolean debugEnabled = log.isDebugEnabled();

        checkData(reqDTO);
        fillDefaultConfig(reqDTO);

        Outbox outbox = new Outbox();
        BeanUtils.copyProperties(reqDTO, outbox);

        outbox.setRetryInterval(reqDTO.getRetryInterval().toString());

        Date now = new Date();
        outbox.setCreateAt(now);
        outbox.setUpdateAt(now);

        // 参数校验
        // 填充缺省参数
        // 保存至数据库中
        outboxRepository.insert(outbox);

        // 执行器 异步尝试执行投递任务deliverMessage()
        if (meetDeliverCondition(outbox, now)) {
            executorService.submit(() -> {
                // 同类调用事务失效处理
                deliverMessageAndUpdateStatus(outbox, reqDTO);
                if (debugEnabled) {
                    log.debug("消息投递结束：< {} > ", JSON.toJSONString(outbox));
                }
            });
        }
    }

    private boolean meetDeliverCondition(Outbox outbox, Date now) {
        Date nextTime = outbox.getNextTime();
        return now.compareTo(nextTime) >= 0;
    }

    private void checkData(OutboxDto dto) {
        if (isEmpty(dto.getSceneType())) {
            throw new OutboxException("业务场景类型不能为空");
        }
        if (isEmpty(dto.getBusinessCode())) {
            throw new OutboxException("业务场景代码不能为空");
        }
        if (isEmpty(dto.getRequestId())) {
            throw new OutboxException("通知任务请求编号不能为空");
        }
        if (isEmpty(dto.getSendMethod())) {
            throw new OutboxException("发送方式不能为空");
        }
    }

    private void fillDefaultConfig(OutboxDto outboxDto) {
        if (outboxDto.getTryTimes() == null) {
            outboxDto.setTryTimes(outboxProperties.getDeliveryConfigVal(outboxDto.getSceneType()
                    , OutboxProperties.DeliveryProp::getMaxTryTime));
        }

        if (outboxDto.getRetryInterval() == null) {
            outboxDto.setRetryInterval(outboxProperties.getDeliveryConfigVal(outboxDto.getSceneType()
                    , OutboxProperties.DeliveryProp::getRetryInterval));
        }

        if (outboxDto.getNeedReceipt() == null) {
            outboxDto.setNeedReceipt(0);
        }

        Date now = new Date();
        if (outboxDto.getStartTime() == null) {
            outboxDto.setStartTime(now);
        }

        if (outboxDto.getNextTime() == null) {
            outboxDto.setNextTime(outboxDto.getStartTime());
        }

        outboxDto.setTriedTimes(0);
    }

    /**
     * 执行投递并将重试次数+1
     */
    public void deliverMessageAndUpdateStatus(Outbox entity, OutboxDto outboxDto) {
        OutboxRequestDto requestDTO = new OutboxRequestDto();
        BeanUtils.copyProperties(outboxDto, requestDTO);

        try {
            // [投递消息-子类实现]
            doDeliverMessage(requestDTO);
            if (entity.getNeedReceipt() <= 0) {
                // 更新不需要回执则更形成`已发送`
                entity.setSendStatus(OutboxMessageStatus.SENDED.getType());
            }
        } catch (Exception e) {
            log.error(String.format("消息投递发生异常：entity: < %s > outboxDto: < %s >", JSON.toJSONString(entity), JSON.toJSONString(outboxDto)), e);
        } finally {
            entity.setTriedTimes(entity.getTriedTimes() + 1);
            // 处理重试场景：是否超过尝试发送次数，如果是则直接改为重试次数太多状态，否则更新下次执行时间
            if (entity.getTryTimes() != -1 && (entity.getTriedTimes() >= entity.getTryTimes())) {
                entity.setSendStatus(OutboxMessageStatus.RETRY_LIMITED.getType());
            } else {
                Duration retryInterval = outboxDto.getRetryInterval();
                entity.setNextTime(DateUtils.plus(entity.getNextTime(), retryInterval.toMillis(), ChronoUnit.MILLIS));
            }
            outboxRepository.updateById(entity);
        }
        if (log.isDebugEnabled()) {
            log.debug("消息投递结束： id={} outboxDto= < {} >", entity.getId(), JSON.toJSONString(outboxDto));
        }
    }

    /**
     * 处理回执
     */
    public void markResponse(OutboxResponseDto responseDto) {
        String sceneType = responseDto.getSceneType();
        String requestId = responseDto.getRequestId();
        String sendMethod = responseDto.getSendMethod();
        String businessCode = responseDto.getBusinessCode();
        Outbox outboxMsg = outboxRepository.getOutboxMsg(sceneType, businessCode, requestId, sendMethod);

        if (isEmpty(outboxMsg)) {
            log.warn("数据库中没有找到需要处理的消息数据 < {} >", JSON.toJSONString(responseDto));
            return;
        }

        outboxMsg.setSendStatus(OutboxMessageStatus.SENDED.getType());
        String response = responseDto.getResponse();

        if (!isEmpty(response)) {
            outboxMsg.setResponse(response);
        }

        String extend = responseDto.getExtend();
        if (!isEmpty(extend)) {
            outboxMsg.setExtend(extend);
        }

        outboxRepository.updateById(outboxMsg);
    }


}