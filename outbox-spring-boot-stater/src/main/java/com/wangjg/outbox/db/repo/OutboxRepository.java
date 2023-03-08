package com.wangjg.outbox.db.repo;

import com.wangjg.outbox.constant.OutboxMessageStatus;
import com.wangjg.outbox.db.entity.Outbox;
import com.wangjg.outbox.exception.OutboxException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.teasoft.bee.osql.*;
import org.teasoft.honey.osql.core.ConditionImpl;
import org.teasoft.honey.util.ObjectUtils;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OutboxRepository {

    private final Suid suid;
    private final SuidRich suidRich;

    public OutboxRepository(Suid suid, SuidRich suidRich) {
        this.suid = suid;
        this.suidRich = suidRich;
    }

    public List<Long> toSendIdsOfReferSceneType(String sceneType) {
        Outbox query = new Outbox();
        query.setSendStatus(OutboxMessageStatus.SENDING.getType());
        query.setSceneType(sceneType);
        Date now = new Date();
        Condition condition = new ConditionImpl();
        condition.op("nextTime", Op.le, now);
        condition.orderBy("id", OrderType.DESC);
        condition.selectField("id");

        List<Outbox> outboxes = suid.select(query, condition);

        if (CollectionUtils.isEmpty(outboxes)) {
            return new LinkedList<>();
        }

        return outboxes.stream().map(Outbox::getId).collect(Collectors.toList());
    }

    public List<Long> toSendIdsOfAllSceneType() {
        Outbox query = new Outbox();
        query.setSendStatus(OutboxMessageStatus.SENDING.getType());
        Date now = new Date();
        Condition condition = new ConditionImpl();
        condition.op("nextTime", Op.le, now);
        condition.orderBy("id", OrderType.DESC);
        condition.selectField("id");

        List<Outbox> outboxes = suid.select(query, condition);

        if (CollectionUtils.isEmpty(outboxes)) {
            return new LinkedList<>();
        }

        return outboxes.stream().map(Outbox::getId).collect(Collectors.toList());
    }

    public List<Long> toSendIdsOfExclusionSceneTypes(Set<String> exclusionSceneTypes) {
        Outbox query = new Outbox();
        query.setSendStatus(OutboxMessageStatus.SENDING.getType());
        Date now = new Date();
        Condition condition = new ConditionImpl();
        condition.op("sceneType", Op.notIn, exclusionSceneTypes);
        condition.op("nextTime", Op.le, now);
        condition.orderBy("id", OrderType.DESC);
        condition.selectField("id");

        List<Outbox> outboxes = suid.select(query, condition);

        if (CollectionUtils.isEmpty(outboxes)) {
            return new LinkedList<>();
        }

        return outboxes.stream().map(Outbox::getId).collect(Collectors.toList());
    }

    public List<Outbox> listForOutBoxByIds(List<Long> ids) {
        Condition condition = new ConditionImpl();
        condition.op("id", Op.in, StringUtils.collectionToCommaDelimitedString(ids));
        return suid.select(new Outbox(), condition);
    }

    public void insert(Outbox outbox) {
        long id = suidRich.insert(outbox);
        if (id > 0) {
            outbox.setId(id);
        } else {
            throw new OutboxException("数据插入失败");
        }
    }

    public void updateById(Outbox entity) {
        suid.update(entity);
    }

    public Outbox getOutboxMsg(String sceneType, String businessCode, String requestId, String sendMethod) {
        Outbox query = new Outbox();

        if (ObjectUtils.isEmpty(sceneType)) {
            query.setSceneType(sceneType);
        }
        if (ObjectUtils.isEmpty(businessCode)) {
            query.setBusinessCode(businessCode);
        }
        if (ObjectUtils.isEmpty(requestId)) {
            query.setRequestId(requestId);
        }
        if (ObjectUtils.isEmpty(sendMethod)) {
            query.setSendMethod(sendMethod);
        }
        Condition condition = new ConditionImpl();
        condition.orderBy("createAt", OrderType.DESC);
        List<Outbox> list = suid.select(query, condition);
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }


}
