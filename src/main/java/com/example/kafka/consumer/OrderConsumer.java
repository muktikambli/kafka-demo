package com.example.kafka.consumer;

import com.example.kafka.model.Order;
import com.example.kafka.model.Payment;
import com.example.kafka.producer.PaymentProducer;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class OrderConsumer {
    private final String bootstrapServers;
    private final String topic;
    private final String groupId;
    private final ObjectMapper mapper = new ObjectMapper();

    public OrderConsumer(String bootstrapServers, String topic, String groupId) {
        this.bootstrapServers = bootstrapServers;
        this.topic = topic;
        this.groupId = groupId;
    }

    public void consume() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));

        PaymentProducer paymentProducer = new PaymentProducer(bootstrapServers, "payments");

        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println("Received Order: " + record.value());
                    
                    // Convert JSON → Order object
                    Order order = mapper.readValue(record.value(), Order.class);
                    
                    // Simulate payment for this order
                    Payment payment = new Payment(
                            "PAY-" + order.getOrderId(),
                            order.getAmount() * 0.9, // example logic
                            "SUCCESS"
                    );
                    
                    // Trigger PaymentProducer
                    paymentProducer.sendPayment(payment);
                    System.out.println("Payment initiated for " + order.getOrderId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            consumer.close();
            paymentProducer.close();
        }
    }
}
