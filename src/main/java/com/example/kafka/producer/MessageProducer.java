package com.example.kafka.producer;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import java.util.Properties;

public class MessageProducer {

    private final KafkaProducer<String, String> producer;
    private final String topicName;

    public MessageProducer(String bootstrapServers, String topicName) {
        this.topicName = topicName;
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producer = new KafkaProducer<>(props);
    }

    public void sendMessage(String key, String value) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topicName, key, value);
        producer.send(record, (metadata, exception) -> {
            if (exception == null) {
                System.out.printf("✅ Sent message: topic=%s partition=%s offset=%d%n",
                        metadata.topic(), metadata.partition(), metadata.offset());
            } else {
                exception.printStackTrace();
            }
        });
    }

    public void close() {
        producer.close();
    }
}
