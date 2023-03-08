package com.wangjg.outbox.dto;

import com.google.common.base.Objects;
import lombok.Data;

import java.io.Serializable;

@Data
public class MessageBrokerIdentifier implements Serializable {

    /**
     * 业务场景：必填
     */
    private String sceneType;

    /**
     * 业务代码
     */
    private String businessCode;

    /**
     * 通知方式
     */
    private String sendMethod;

    /**
     *  请求ID
     */
    private String requestId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageBrokerIdentifier)) return false;
        MessageBrokerIdentifier that = (MessageBrokerIdentifier) o;
        return Objects.equal(sceneType, that.sceneType) && Objects.equal(businessCode, that.businessCode) && Objects.equal(sendMethod, that.sendMethod) && Objects.equal(requestId, that.requestId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sceneType, businessCode, sendMethod, requestId);
    }

    public static void main(String[] args) {
        MessageBrokerIdentifier messageBrokerIdentifier1 = new MessageBrokerIdentifier();
        messageBrokerIdentifier1.setSceneType("1");
        messageBrokerIdentifier1.setSendMethod("2");

        MessageBrokerIdentifier messageBrokerIdentifier2 = new MessageBrokerIdentifier();
        messageBrokerIdentifier2.setSceneType("1");
        messageBrokerIdentifier2.setSendMethod("2");

        System.out.println(messageBrokerIdentifier1.equals(messageBrokerIdentifier2));
        System.out.println(messageBrokerIdentifier1 == messageBrokerIdentifier2);
    }
}
