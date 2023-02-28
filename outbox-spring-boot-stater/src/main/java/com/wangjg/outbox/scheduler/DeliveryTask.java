package com.wangjg.outbox.scheduler;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.wangjg.outbox.MessageBroker;
import com.wangjg.outbox.MessageBrokerFactory;
import com.wangjg.outbox.config.OutboxProperties;
import com.wangjg.outbox.db.entity.Outbox;
import com.wangjg.outbox.db.repo.OutboxRepository;
import com.wangjg.outbox.dto.MessageBrokerIdentifier;
import com.wangjg.outbox.dto.OutboxDto;
import com.wangjg.outbox.exception.OutboxException;
import com.wangjg.outbox.util.SyncHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class DeliveryTask implements Runnable {

    private final OutboxProperties outboxProperties;
    private final OutboxRepository outboxRepository;
    private final MessageBrokerFactory messageBrokerFactory;


    /**
     * 指定场景独占，排他场景
     */
    private boolean exclusive;

    /**
     * 指定业务场景
     */
    private String sceneType;

    /**
     * 要排除的业务场景
     */
    private Set<String> exclusionSceneTypes;

    public DeliveryTask(OutboxRepository outboxRepository,
                        MessageBrokerFactory messageBrokerFactory,
                        OutboxProperties outboxProperties) {
        this.outboxRepository = outboxRepository;
        this.messageBrokerFactory = messageBrokerFactory;
        this.outboxProperties = outboxProperties;
    }

    public DeliveryTask setSceneType(String s) {
        this.sceneType = s;
        return this;
    }

    public DeliveryTask setExclusive(boolean exclusive) {
        this.exclusive = exclusive;
        return this;
    }

    public DeliveryTask setExclusionSceneTypes(Set<String> exclusionSceneTypes) {
        this.exclusionSceneTypes = exclusionSceneTypes;
        return this;
    }

    /**
     * 遍历发送中状态的任务 执行任务、重试次数+1
     */

    public void doSend() {
        boolean debugEnabled = log.isDebugEnabled();
        if (!outboxProperties.getTaskConfigVal(sceneType, OutboxProperties.TaskProp::getEnable)) {
            if (debugEnabled) {
                log.debug("outbox 扫描触发发送任务功能关闭: exclusive: {}, sceneType : {}, exclusionSceneTypes: {}", exclusive, sceneType,
                        CollectionUtils.isEmpty(exclusionSceneTypes) ? null : StringUtils.collectionToCommaDelimitedString(exclusionSceneTypes));
            }
            return;
        }
        if (debugEnabled) {
            log.debug("outbox 扫描触发发送任务开始: exclusive: {}, sceneType : {}, exclusionSceneTypes: {}", exclusive, sceneType,
                    CollectionUtils.isEmpty(exclusionSceneTypes) ? null : StringUtils.collectionToCommaDelimitedString(exclusionSceneTypes));
        }

        // 发送中
        List<Long> recordIds;
        if (exclusive) {
            if (ObjectUtils.isEmpty(sceneType)) {
                throw new OutboxException("独占式任务，sceneType 不能为空");
            }
            recordIds = outboxRepository.toSendIdsOfReferSceneType(sceneType);
        } else {
            if (CollectionUtils.isEmpty(exclusionSceneTypes)) {
                recordIds = outboxRepository.toSendIdsOfAllSceneType();
            } else {
                recordIds = outboxRepository.toSendIdsOfExclusionSceneTypes(exclusionSceneTypes);
            }
        }

        if (CollectionUtils.isEmpty(recordIds)) {
            log.debug("没有扫描到需要触发发送的任务数据。");
            return;
        }

        List<List<Long>> idPages = Lists.partition(recordIds, outboxProperties.getTaskConfigVal(sceneType, OutboxProperties.TaskProp::getJobBatch));

        for (List<Long> ids : idPages) {
            List<Outbox> tasksInPage = outboxRepository.listForOutBoxByIds(ids);
            CompletableFuture.runAsync(() -> {
                // 消息处理
                for (Outbox toSend : tasksInPage) {
                    // 加锁
                    SyncHelper.OUTBOX_STRING_POOL.sync(toSend.getSceneType() + "_" + toSend.getRequestId() + "_" + toSend.getSendMethod(), () -> {
                        try {
                            MessageBrokerIdentifier messageBrokerIdentifier = new MessageBrokerIdentifier();
                            messageBrokerIdentifier.setSceneType(toSend.getSceneType());
                            messageBrokerIdentifier.setSendMethod(toSend.getSendMethod());

                            MessageBroker handler = messageBrokerFactory.getBroker(messageBrokerIdentifier);
                            OutboxDto dto = new OutboxDto();
                            BeanUtils.copyProperties(toSend, dto);
                            handler.deliverMessageAndUpdateStatus(toSend, dto);
                        } catch (Exception e) {
                            log.error(String.format("outbox < %s > 发送异常 ：exclusive: %s, sceneType : %s, exclusionSceneTypes: %s ",
                                            JSON.toJSONString(toSend),
                                            exclusive,
                                            sceneType,
                                            CollectionUtils.isEmpty(exclusionSceneTypes) ? null :
                                                    StringUtils.collectionToCommaDelimitedString(exclusionSceneTypes))
                                    , e);
                        }
                    });
                }
            });
        }

        if (debugEnabled) {
            log.debug("DeliveryTask#doSend 扫描触发发送结束：exclusive: {}, sceneType : {}, exclusionSceneTypes: {}",
                    exclusive,
                    sceneType,
                    CollectionUtils.isEmpty(exclusionSceneTypes) ? null : StringUtils.collectionToCommaDelimitedString(exclusionSceneTypes));
        }
    }

    @Override
    public void run() {
        doSend();
    }
}
