package com.wangjg.outbox;

import com.wangjg.outbox.dto.MessageBrokerIdentifier;
import com.wangjg.outbox.dto.OutboxRequestDto;
import com.wangjg.outbox.exception.OutboxException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 发送器映射工厂
 */
@Slf4j
public final class MessageBrokerFactory {

    /**
     * MessageBrokerTemplate 实例列表
     */
    private final List<MessageBroker> messageBrokers;

    /**
     * 消息发送器映射
     */
    private static final Map<MessageBrokerIdentifier, MessageBroker> BROKERS = new ConcurrentHashMap<>();

    public MessageBrokerFactory(List<MessageBroker> messageBrokers) {
        this.messageBrokers = messageBrokers;
    }

    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        if (!CollectionUtils.isEmpty(messageBrokers)) {
            for (MessageBroker messageBroker : messageBrokers) {
                List<MessageBrokerIdentifier> identifiers = messageBroker.getBrokerIdentifier();
                identifiers.forEach(i -> BROKERS.put(i, messageBroker));
            }
        }
    }

    /**
     * 获取对应的消息处理器
     */
    public MessageBroker getBroker(MessageBrokerIdentifier key) {
        return Optional.ofNullable(BROKERS.get(key))
                .orElse(EMPTY_BROKER);
    }

    public static EmptyMessageBroker EMPTY_BROKER = new EmptyMessageBroker();

    public static final class EmptyMessageBroker extends MessageBroker {
        @Override
        protected List<MessageBrokerIdentifier> getBrokerIdentifier() {
            return null;
        }

        @Override
        protected void doDeliverMessage(OutboxRequestDto reqDTO) throws OutboxException {
        }
    }

}