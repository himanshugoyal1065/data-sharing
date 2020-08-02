package com.himanshu.datasharing.kafka;

import java.util.function.Supplier;

/**
 * @author <a href="himanshu.goyal@navis.com">Himanshu Goyal</a>
 */
public interface IKafkaSupplier<T> extends Supplier<T> {
    String CONSUMER_BEAN = "kafkaConsumer";
    String PRODUCER_BEAN = "kafkaProducer";
}
