package com.example.kafka.consumer;

import com.example.kafka.model.Payment;
import com.example.kafka.model.Notification;
import com.example.kafka.producer.NotificationProducer;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class PaymentConsumer {
    private final String bootstrapServers;
    private final String topic;
    private final String groupId;
    private final ObjectMapper mapper = new ObjectMapper();

    public PaymentConsumer(String bootstrapServers, String topic, String groupId) {
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

        NotificationProducer notificationProducer = new NotificationProducer(bootstrapServers, "notifications");

        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println("Received Payment: " + record.value());

                    Payment payment = mapper.readValue(record.value(), Payment.class);

                    Notification notification = new Notification(
                            "NTF-" + payment.getPaymentId(),
                            "Payment of ₹" + payment.getAmount() + " received successfully",
                            "EMAIL"
                    );

                    notificationProducer.sendNotification(notification);
                    System.out.println("Notification triggered for " + payment.getPaymentId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            consumer.close();
            notificationProducer.close();
        }
    }
}
