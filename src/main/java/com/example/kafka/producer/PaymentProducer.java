package com.example.kafka.producer;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.kafka.model.Payment;

import java.util.Properties;

public class PaymentProducer {

    private final KafkaProducer<String, String> producer;
    private final String topic;
    private final ObjectMapper mapper = new ObjectMapper();

    public PaymentProducer(String bootstrapServers, String topic) {
        this.topic = topic;

        // Configure producer properties
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all"); // ensures message durability

        // Create Kafka producer
        this.producer = new KafkaProducer<>(props);
    }

    public void sendPayment(Payment payment) {
        try {
            // Convert payment object to JSON
            String json = mapper.writeValueAsString(payment);

            // Create a record with key=paymentId, value=json
            ProducerRecord<String, String> record =
                    new ProducerRecord<>(topic, payment.getPaymentId(), json);

            // Send asynchronously and log result
            producer.send(record, (metadata, e) -> {
                if (e == null) {
                    System.out.printf("Sent Payment: %s | topic=%s | partition=%d | offset=%d%n",
                            payment.getPaymentId(), metadata.topic(), metadata.partition(), metadata.offset());
                } else {
                    System.err.printf("Error sending payment %s: %s%n", payment.getPaymentId(), e.getMessage());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        producer.close();
        System.out.println("PaymentProducer closed.");
    }
}
