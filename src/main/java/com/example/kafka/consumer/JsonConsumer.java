package com.example.kafka.consumer;

import com.example.kafka.model.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collection;
import java.util.Properties;

public class JsonConsumer implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(JsonConsumer.class);
    private final KafkaConsumer<String, String> consumer;
    private final ObjectMapper mapper = new ObjectMapper();
    private volatile boolean running = true;

    public JsonConsumer(String bootstrapServers, Collection<String> topics, String groupId) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        this.consumer = new KafkaConsumer<>(props);
        consumer.subscribe(topics);
        log.info("Consumer subscribed to {}", topics);
    }

    @Override
    public void run() {
        try {
            while (running) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                for (ConsumerRecord<String, String> r : records) {
                    try {
                        Order order = mapper.readValue(r.value(), Order.class);
                        log.info("Consumed order: key={} value={} partition={} offset={}", r.key(), order, r.partition(), r.offset());
                        // TODO: business logic
                    } catch (Exception e) {
                        log.error("Failed to deserialize record value: {}", r.value(), e);
                        // TODO: DLQ logic
                    }
                }
            }
        } catch (WakeupException e) {
            // expected on shutdown
        } finally {
            consumer.close();
            log.info("Consumer closed");
        }
    }

    public void shutdown() {
        running = false;
        consumer.wakeup();
    }
}
