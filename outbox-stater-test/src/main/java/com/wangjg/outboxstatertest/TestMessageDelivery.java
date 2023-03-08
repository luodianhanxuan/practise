package com.wangjg.outboxstatertest;

import com.wangjg.outbox.MessageBroker;
import com.wangjg.outbox.dto.MessageBrokerIdentifier;
import com.wangjg.outbox.dto.OutboxRequestDto;
import com.wangjg.outbox.exception.OutboxException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class TestMessageDelivery extends MessageBroker {

    @Override
    protected List<MessageBrokerIdentifier> getIdentifier() {
        return Collections.singletonList(new MessageBrokerIdentifier() {{
            this.setSceneType("test");
        }});
    }

    @Override
    protected void doDeliverMessage(OutboxRequestDto reqDTO) throws OutboxException {
        System.out.println("xiu........");
    }
}
