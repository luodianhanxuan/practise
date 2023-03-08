package com.wangjg.outbox;

import com.alibaba.fastjson.JSON;
import com.wangjg.outbox.dto.MessageBrokerIdentifier;
import com.wangjg.outbox.exception.OutboxException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 发送器映射工厂
 */
@Slf4j
public final class MessageBrokerFactory {

    /**
     * MessageBrokerTemplate 实例列表
     */
    private final List<MessageBroker> messageBrokers;

    private static final String SPLIT = ",";

    /**
     * 消息发送器映射
     */
    private static final Map<MessageBrokerIdentifier, MessageBroker> BROKERS = new HashMap<>();

    public static Map<String, Integer> ID_ORDER_SCORE_MAPPING;

    static {
        ID_ORDER_SCORE_MAPPING = new HashMap<>();
        ID_ORDER_SCORE_MAPPING.put("sceneType", 5);
        ID_ORDER_SCORE_MAPPING.put("businessCode", 10);
        ID_ORDER_SCORE_MAPPING.put("sendMethod", 15);
        ID_ORDER_SCORE_MAPPING.put("requestId", 20);
    }

    /**
     * id 相关字段填充打分：用于做优先级匹配
     */
    private static final Map<MessageBrokerIdentifier, Integer> FULL_FIELD_SCORE = new HashMap<>();

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
                List<MessageBrokerIdentifier> identifierMatchers = messageBroker.getIdentifier();
                identifierMatchers.forEach(idMatcher -> {
                    Integer fieldFullScore = getFieldFullScore(idMatcher);
                    FULL_FIELD_SCORE.put(idMatcher, fieldFullScore);
                    BROKERS.put(idMatcher, messageBroker);
                });
            }
        }
    }

    private Integer getFieldFullScore(MessageBrokerIdentifier idMatcher) {
        int score = 0;
        if (!ObjectUtils.isEmpty(idMatcher.getSceneType())) {
            score = score + ID_ORDER_SCORE_MAPPING.get("sceneType");
        }
        if (!ObjectUtils.isEmpty(idMatcher.getBusinessCode())) {
            score = score + ID_ORDER_SCORE_MAPPING.get("businessCode");
        }
        if (!ObjectUtils.isEmpty(idMatcher.getSendMethod())) {
            score = score + ID_ORDER_SCORE_MAPPING.get("sendMethod");
        }
        if (!ObjectUtils.isEmpty(idMatcher.getSendMethod())) {
            score = score + ID_ORDER_SCORE_MAPPING.get("requestId");
        }
        return score;
    }

    /**
     * 获取对应的消息处理器
     */
    public MessageBroker getBroker(MessageBrokerIdentifier id) {
        Map<Integer, List<MessageBroker>> matchesOfHandler = new HashMap<>();
        for (MessageBrokerIdentifier idMacher : BROKERS.keySet()) {
            if (matchesIfNotNull(idMacher, id)) {
                List<MessageBroker> tmp = matchesOfHandler.computeIfAbsent(FULL_FIELD_SCORE.get(idMacher), (k) -> new LinkedList<>());
                MessageBroker broker = BROKERS.get(idMacher);
                if (broker == null) {
                    continue;
                }

                tmp.add(broker);
            }
        }
        if (CollectionUtils.isEmpty(matchesOfHandler)) {
            throw new OutboxException("没有找到合适的消息处理器");
        }

        Optional<Integer> maxScore = matchesOfHandler.keySet().stream().max(Integer::compareTo);
        if (!maxScore.isPresent()) {
            throw new OutboxException("没有找到合适的消息处理器");
        }

        Integer fieldFullScore = maxScore.get();
        if (fieldFullScore <= 0) {
            // 字段全为 null
            throw new OutboxException("没有找到合适的消息处理器");
        }

        List<MessageBroker> brokers = matchesOfHandler.get(fieldFullScore);
        if (CollectionUtils.isEmpty(brokers)) {
            throw new OutboxException("没有找到合适的消息处理器");
        }

        if (brokers.size() > 1) {
            log.error("根据 id 找到多个匹配的消息处理器: id --> {} , borkers ---> {}", JSON.toJSONString(id),
                    StringUtils.collectionToCommaDelimitedString(brokers.stream().map(b -> b.getClass().getCanonicalName()).collect(Collectors.toSet())));
            throw new OutboxException("根据 id 找到多个匹配的消息处理器");
        }

        return brokers.get(0);
    }

    private boolean matchesIfNotNull(MessageBrokerIdentifier idMacher, MessageBrokerIdentifier id) {
        boolean matches = true;
        if (!ObjectUtils.isEmpty(idMacher.getSceneType())) {
            matches = id.getSceneType().matches(completeRegexp(idMacher.getSceneType()));
        }

        if (matches && !ObjectUtils.isEmpty(idMacher.getBusinessCode())) {
            matches = id.getBusinessCode().matches(completeRegexp(idMacher.getBusinessCode()));
        }

        if (matches && !ObjectUtils.isEmpty(idMacher.getSendMethod())) {
            matches = id.getSendMethod().matches(completeRegexp(idMacher.getSendMethod()));
        }

        if (matches && !ObjectUtils.isEmpty(idMacher.getRequestId())) {
            matches = id.getRequestId().matches(completeRegexp(idMacher.getRequestId()));
        }

        return matches;
    }

    public String completeRegexp(String regexp) {
        if (!regexp.startsWith("^")) {
            regexp = "^" + regexp;
        }
        if (!regexp.endsWith("$")) {
            regexp = regexp + "$";
        }
        return regexp;
    }

    private String concatFieldValues(MessageBrokerIdentifier id) {
        StringJoiner sj = new StringJoiner(SPLIT);
        if (!ObjectUtils.isEmpty(id.getSceneType())) {
            sj.add(id.getSceneType());
        }

        if (!ObjectUtils.isEmpty(id.getBusinessCode())) {
            sj.add(id.getBusinessCode());
        }

        if (!ObjectUtils.isEmpty(id.getSendMethod())) {
            sj.add(id.getSendMethod());
        }

        if (!ObjectUtils.isEmpty(id.getRequestId())) {
            sj.add(id.getRequestId());
        }

        return sj.toString().replace("^", "").replace("$", "");
    }
}