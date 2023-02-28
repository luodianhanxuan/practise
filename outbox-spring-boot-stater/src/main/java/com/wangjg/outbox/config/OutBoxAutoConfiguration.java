package com.wangjg.outbox.config;

import com.wangjg.outbox.MessageBroker;
import com.wangjg.outbox.MessageBrokerFactory;
import com.wangjg.outbox.db.repo.OutboxRepository;
import com.wangjg.outbox.scheduler.CronSchedule;
import com.wangjg.outbox.scheduler.DeliveryTask;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.teasoft.bee.osql.Suid;
import org.teasoft.spring.boot.config.BeeAutoConfiguration;

import java.util.List;

@Configuration
@EnableConfigurationProperties(OutboxProperties.class)
@AutoConfigureAfter(BeeAutoConfiguration.class)
@Getter
public class OutBoxAutoConfiguration {

    private final OutboxProperties outboxProperties;

    public OutBoxAutoConfiguration(OutboxProperties outboxProperties) {
        this.outboxProperties = outboxProperties;
    }

    @Bean
    @ConditionalOnMissingBean(OutboxRepository.class)
    public OutboxRepository outboxRepository(Suid suid) {
        return new OutboxRepository(suid);
    }

    @Bean
    @ConditionalOnMissingBean(CronSchedule.class)
    public CronSchedule cronSchedule(ThreadPoolTaskScheduler threadPoolTaskScheduler) {
        return new CronSchedule(outboxProperties, threadPoolTaskScheduler);
    }

    @Bean
    @ConditionalOnMissingBean(MessageBrokerFactory.class)
    public MessageBrokerFactory messageBrokerFactory(@Autowired(required = false) List<MessageBroker> messageBrokers) {
        return new MessageBrokerFactory(messageBrokers);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
    @ConditionalOnMissingBean(DeliveryTask.class)
    public DeliveryTask deliveryTask(MessageBrokerFactory messageBrokerFactory,
                                     OutboxRepository outboxRepository) {
        return new DeliveryTask(outboxRepository, messageBrokerFactory, outboxProperties);
    }
}
