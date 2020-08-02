package com.himanshu.datasharing.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * @author <a href="himanshu.goyal@navis.com">Himanshu Goyal</a>
 */
public class KafkaTopicManager {

    private AdminClient adminClient;

    @Autowired
    private IPropertyProvider propertyProvider;

    @PostConstruct
    public void init() {
        adminClient = AdminClient.create(propertyProvider.get());
    }

    public boolean checkTopic(String inTopicName) throws ExecutionException, InterruptedException {
        return getTopics().contains(inTopicName) ? true : false;
    }

    public boolean createTopic(String inTopicName) throws Exception {
        if (checkTopic(inTopicName)) {
            return false;
        }
        CreateTopicsResult createTopicsResult = adminClient.createTopics(
                Collections.singletonList(new NewTopic(inTopicName, 1, (short) 3)));

        return true;
    }

    public boolean deleteTopics(String inTopicName) throws Exception {
        if (!checkTopic(inTopicName)) {
            return false;
        }
        DeleteTopicsResult deleteTopicsResult = adminClient.deleteTopics(Collections.singleton(inTopicName));
        return true;
    }

    public Set<String> getTopics() throws ExecutionException, InterruptedException {
        ListTopicsResult listTopicsResult = adminClient.listTopics();
        KafkaFuture<Set<String>> kafkaFuture = listTopicsResult.names();
        Set<String> topicNames = kafkaFuture.get();
        return topicNames;
    }

    public long getTopicLength(TopicPartition inTopicPartition) {

        Properties properties = propertyProvider.get();

        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        KafkaConsumer<String, Byte[]> kafkaConsumer = new KafkaConsumer<String, Byte[]>(properties);

        kafkaConsumer.assign(Collections.singletonList(inTopicPartition));
        kafkaConsumer.seekToEnd(Collections.singletonList(inTopicPartition));

        return kafkaConsumer.position(inTopicPartition);
    }
}
