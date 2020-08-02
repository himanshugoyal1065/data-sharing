package com.himanshu.datasharing.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;

import javax.annotation.PostConstruct;
import java.util.Properties;

/**
 * @author <a href="himanshu.goyal@navis.com">Himanshu Goyal</a>
 */
public class KafkaProducerProvider implements IKafkaSupplier<KafkaProducer> {

    private KafkaProducer<String, Byte[]> kafkaProducer;

    @Autowired
    private IPropertyProvider propertyProvider;

    @PostConstruct
    public void init() {
        Properties properties = propertyProvider.get();
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());

        //idempotence
        properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        properties.setProperty(ProducerConfig.RETRIES_CONFIG, String.valueOf(Integer.MAX_VALUE));
        properties.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");


        kafkaProducer = new KafkaProducer<>(properties);
    }

    @Nullable
    @Override
    public KafkaProducer get() {

        if (kafkaProducer != null) {
            return kafkaProducer;
        }
        return null;
    }
}
