package com.wangjg.outboxstatertest;

import com.wangjg.outbox.MessageBroker;
import com.wangjg.outbox.MessageBrokerFactory;
import com.wangjg.outbox.dto.MessageBrokerIdentifier;
import com.wangjg.outbox.dto.OutboxRequestDto;
import com.wangjg.outbox.exception.OutboxException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class Test2MessageDelivery extends MessageBroker {

    @Override
    protected List<MessageBrokerIdentifier> getIdentifier() {
        return Collections.singletonList(new MessageBrokerIdentifier() {{
//            this.setSceneType("test");
//            this.setBusinessCode("test");
//            this.setSendMethod("test");
        }});
    }

    @Override
    protected void doDeliverMessage(OutboxRequestDto reqDTO) throws OutboxException {
        System.out.println("xiu........");
    }


    public static void main(String[] args) {
        MessageBrokerFactory factory = new MessageBrokerFactory(Arrays.asList(
                new MessageBroker[]{
                        new TestMessageDelivery(),
                        new Test1MessageDelivery()})
        );
        factory.init();
        MessageBrokerIdentifier messageBrokerIdentifier = new MessageBrokerIdentifier();
        messageBrokerIdentifier.setBusinessCode("test");
        messageBrokerIdentifier.setSceneType("test123");
        messageBrokerIdentifier.setSendMethod("test");
        messageBrokerIdentifier.setRequestId("test");
        MessageBroker broker = factory.getBroker(messageBrokerIdentifier);
        System.out.println(broker.getClass().getCanonicalName());

    }
}
