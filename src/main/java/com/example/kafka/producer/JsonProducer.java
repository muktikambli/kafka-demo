package com.example.kafka.producer;

import com.example.kafka.model.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class JsonProducer {
    private static final Logger log = LoggerFactory.getLogger(JsonProducer.class);
    private final KafkaProducer<String, String> producer;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String topic;

    public JsonProducer(String bootstrapServers, String topic) {
        this.topic = topic;
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        this.producer = new KafkaProducer<>(props);
    }

    public void sendOrder(Order order) {
        try {
            String json = mapper.writeValueAsString(order);
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, order.getOrderId(), json);
            producer.send(record, (metadata, exception) -> {
                if (exception == null) {
                    log.info("Sent order {} to topic={} partition={} offset={}", order.getOrderId(),
                            metadata.topic(), metadata.partition(), metadata.offset());
                } else {
                    log.error("Failed to send order {}", order.getOrderId(), exception);
                }
            });
        } catch (Exception e) {
            log.error("Serialization error for order {}", order, e);
        }
    }

    public void close() {
        producer.flush();
        producer.close();
    }
}
