package com.wangjg.outboxstatertest;

import com.wangjg.outbox.dto.OutboxDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("test")
public class TestController {

    @Resource
    private TestMessageDelivery messageDelivery;

    @GetMapping
    public void send() {
        messageDelivery.sendMsg(
                OutboxDto.builder()
                        .setSceneType("test")
                        .setSendMethod("sync")
                        .setBusinessCode("123")
                        .setRequestId("test1")
                        .setPayload("hello outbox")
                        .build());
    }
}
