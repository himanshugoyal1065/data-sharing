package com.himanshu.datasharing;

import com.himanshu.datasharing.kafka.IKafkaSupplier;
import com.himanshu.datasharing.kafka.IPropertyProvider;
import com.himanshu.datasharing.kafka.KafkaTopicManager;
import com.himanshu.datasharing.kafka.PropertiesEnum;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="himanshu.goyal@navis.com">Himanshu Goyal</a>
 */
public class StandaloneLauncher {

    @Autowired
    private IPropertyProvider propertyProvider;

    @Autowired
    private IKafkaSupplier<KafkaConsumer> kafkaConsumerSupplier;

    @Autowired
    private IKafkaSupplier<KafkaProducer> kafkaProducerSupplier;

    @Autowired
    private KafkaTopicManager kafkaTopicManager;

    public void start() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            //TODO display the list of files/topics already available on the current bootstrap server.

            Set<String> topics = new HashSet<>();
            try {
                System.out.println("the following files are there in the system ");
                topics = kafkaTopicManager.getTopics();
                for (String topicName : topics) {
                    System.out.println(topicName);
                }
            } catch (Exception ignore) {

            }

            int n = Integer.parseInt(br.readLine());
            switch (n) {
                case 1:
                    System.out.println("you can change your bootstrap server here");
                    System.out.println("enter the address of you bootStrap server");
                    String bootStrapServer = br.readLine();
                    //TODO update the value inside the properties file.
                    propertyProvider.accept(Collections.singletonMap(PropertiesEnum.BOOTSTRAP_SERVER.getValue(), bootStrapServer));
                    break;
                case 2:
                    System.out.println("Making you as the producer");
                    //get the path of the file to be send..
                    String path = br.readLine();
                    KafkaProducer<String, Byte[]> kafkaProducer = kafkaProducerSupplier.get();
                    streamTheFile(path, kafkaProducer);
                    System.out.println("the file is saved in it's topic");
                    for (String topicName : topics) {
                        System.out.println(topicName);
                    }
                    break;
                case 3://download
                    System.out.println("making you as the consumer");
                    System.out.println("enter the name of the item from the list");
                    String itemName = br.readLine();
                    //we should listen to itemName topic now..
                    KafkaConsumer<String, Byte[]> kafkaConsumer = kafkaConsumerSupplier.get();

                    //the name of the new file (for example: C:/try/try.mp4)
                    String newFileNameAndLocation = br.readLine();

                    //TODO complete the implementation

                    readTheFileStream(itemName, kafkaConsumer, newFileNameAndLocation);
                    for (String topicName : topics) {
                        System.out.println(topicName);
                    }
                    break;
                case 4:
                    System.out.println("streaming the file(meaning we are not downloading the file");
                    System.out.println("for now, we only support video streaming");
                    System.out.println("enter the file from the list");

                    String fileNameToBeStreamed = br.readLine();

                    KafkaConsumer<String, Byte[]> kafkaConsumerForStreaming = kafkaConsumerSupplier.get();

                    
                    break;
                case 5:
                    System.out.println("deleting a topic");
                    String topicName = br.readLine();
                    if (kafkaTopicManager.deleteTopics(topicName)) {
                        System.out.println("the topic is deleted");
                    }
                    else {
                        System.out.println("error while deleting the topic");
                    }
                case 9:
                    System.out.println("the application is stopped");
                    break;
            }
        }
    }

    private void readTheFileStream(String itemName, KafkaConsumer<String, Byte[]> kafkaConsumer, String inNewFileLocation) throws IOException {

        TopicPartition topicPartition = new TopicPartition(itemName, 0);
        kafkaConsumer.assign(Collections.singletonList(topicPartition));
        kafkaConsumer.seekToBeginning(Collections.singletonList(topicPartition));
        long start = kafkaConsumer.position(topicPartition);
        long last = kafkaTopicManager.getTopicLength(topicPartition);

        //get the fileOutputStream for the new file location
        File file = new File(inNewFileLocation);
        file.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(file);


        while (last - start > 0) {
            ConsumerRecords consumerRecords = kafkaConsumer.poll(Duration.ofMillis(10));
            for (Object consumerRecord : consumerRecords) {
                if (consumerRecord instanceof ConsumerRecord) {
                   Object stream = ((ConsumerRecord) consumerRecord).value();
                   if (stream instanceof byte[]) {
                       byte[] byteStream = (byte[]) stream;
                       fileOutputStream.write(byteStream);
                   }
                }
            }
            kafkaConsumer.commitAsync();
            start = kafkaConsumer.position(topicPartition);
        }

        fileOutputStream.close();
    }

    private void streamTheFile(String path, KafkaProducer<String, Byte[]> kafkaProducer) throws Exception {
        File file = new File(path);

        if (!file.exists()) {
            System.out.println("the file entered is not available");
            return;
        }

        System.out.println("the exists in the system");

        FileInputStream fileInputStream = new FileInputStream(file);

        String fileName = getFileNameFromPath(path);

        if (!kafkaTopicManager.createTopic(fileName)) {
            System.out.println("the file " + fileName + " already exists");
            System.out.println("returning");
            return;
        }

        byte buffer[] = new byte[4096];
        while (fileInputStream.read(buffer) != -1) {
            ProducerRecord producerRecord = new ProducerRecord<>(fileName, buffer);
            kafkaProducer.send(producerRecord, (metadata, exception) -> {
                if (exception == null) {
                    System.out.println("the producer has successfully executed");
                    System.out.println(metadata.hasOffset() + " " + metadata.hasTimestamp() + " " + metadata.offset() + " " +
                            metadata.partition() + " " + metadata.timestamp() + " " + metadata.topic());
                } else {
                    exception.printStackTrace();
                }
            });
        }
        fileInputStream.close();
    }

    public String getFileNameFromPath(String inPath) {
        String str[] = inPath.split("\\\\");
        return removeSpaces(str[str.length - 1].trim());
    }

    public String removeSpaces(String inString) {
        String newString = "";
        String str[] = inString.split("");
        for (int i = 0; i < str.length; i++) {
            if (str[i].equals(" ")) {
                continue;
            }
            newString += str[i];
        }

        return newString;
    }
}