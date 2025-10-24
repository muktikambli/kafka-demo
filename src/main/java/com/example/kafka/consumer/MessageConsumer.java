package com.example.kafka.consumer;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class MessageConsumer {

    private final KafkaConsumer<String, String> consumer;

    public MessageConsumer(String bootstrapServers, String topicName, String groupId) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topicName));
    }

    public void consume() {
        System.out.println("🔄 Listening for messages...");
        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("📥 Received message: key=%s value=%s partition=%s offset=%d%n",
                            record.key(), record.value(), record.partition(), record.offset());
                }
            }
        } finally {
            consumer.close();
        }
    }
}
