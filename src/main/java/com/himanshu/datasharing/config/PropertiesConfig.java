package com.himanshu.datasharing.config;

import com.himanshu.datasharing.StandaloneLauncher;
import com.himanshu.datasharing.kafka.IKafkaSupplier;
import com.himanshu.datasharing.kafka.IPropertyProvider;
import com.himanshu.datasharing.kafka.KafkaConsumerProvider;
import com.himanshu.datasharing.kafka.KafkaProducerProvider;
import com.himanshu.datasharing.kafka.KafkaTopicManager;
import com.himanshu.datasharing.kafka.PropertiesConfigurationProvider;
import com.himanshu.datasharing.kafka.PropertyProverImpl;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="himanshu.goyal@navis.com">Himanshu Goyal</a>
 */
@Configuration
public class PropertiesConfig {

    @Bean
    public PropertiesConfigurationProvider getPropertiesConfigurationProvider() {
        return new PropertiesConfigurationProvider();
    }

    @Bean
    public IPropertyProvider getPropertyProvider() {
        return new PropertyProverImpl();
    }

    @Bean(name = IKafkaSupplier.CONSUMER_BEAN)
    public IKafkaSupplier<KafkaConsumer> getKafkaConsumer() {
        return new KafkaConsumerProvider();
    }

    @Bean(name = IKafkaSupplier.PRODUCER_BEAN)
    public IKafkaSupplier<KafkaProducer> getKafkaProducer() {
        return new KafkaProducerProvider();
    }

    @Bean
    public KafkaTopicManager getKafkaTopicManager() {
        return new KafkaTopicManager();
    }

    @Bean
    public StandaloneLauncher getStandAloneLauncher() {
        return new StandaloneLauncher();
    }
}
