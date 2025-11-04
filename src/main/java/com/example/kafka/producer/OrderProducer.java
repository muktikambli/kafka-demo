package com.example.kafka.producer;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.kafka.model.Order;

import java.util.Properties;

public class OrderProducer {
    private final KafkaProducer<String, String> producer;
    private final String topic;
    private final ObjectMapper mapper = new ObjectMapper();

    public OrderProducer(String bootstrapServers, String topic) {
        this.topic = topic;
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        this.producer = new KafkaProducer<>(props);
    }

    public void sendOrder(Order order) {
        try {
            String json = mapper.writeValueAsString(order);
            producer.send(new ProducerRecord<>(topic, order.getOrderId(), json), (metadata, e) -> {
                if (e == null) {
                    System.out.printf("Sent Order: %s to topic=%s partition=%d offset=%d%n",
                            order.getOrderId(), metadata.topic(), metadata.partition(), metadata.offset());
                } else {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        producer.close();
    }
}
