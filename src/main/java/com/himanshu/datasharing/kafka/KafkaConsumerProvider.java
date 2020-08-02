package com.himanshu.datasharing.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;

import javax.annotation.PostConstruct;
import java.util.Properties;
import java.util.UUID;

/**
 * @author <a href="himanshu.goyal@navis.com">Himanshu Goyal</a>
 */
public class KafkaConsumerProvider implements IKafkaSupplier<KafkaConsumer> {

    private Properties properties;

    @Autowired
    private IPropertyProvider propertyProvider;

    @PostConstruct
    public void init() {
        properties = propertyProvider.get();
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
    }

    @Nullable
    @Override
    public KafkaConsumer get() {
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        return new KafkaConsumer<String, String>(properties);
    }
}
